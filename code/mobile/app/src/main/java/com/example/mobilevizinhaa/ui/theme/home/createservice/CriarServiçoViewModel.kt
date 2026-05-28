package com.example.mobilevizinhaa.ui.theme.home.createservice

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils.replace
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Imports corrigidos para as classes antigas que não quebram o app
import com.example.mobilevizinhaa.ui.theme.data.CategoryDetail
import com.example.mobilevizinhaa.ui.theme.data.CreateServiceRequest
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.example.mobilevizinhaa.ui.theme.data.TypeCategoryDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

class CriarServicoViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    // Usando a classe CategoryDetail original
    val categoriasIds = mutableStateListOf<CategoryDetail>()

    fun carregarCategorias(token: String) {
        if (categoriasIds.isNotEmpty()) return

        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.authApi.obterTodasCategorias(authHeader)
                }

                categoriasIds.clear()

                if (response.isSuccessful && response.body() != null) {
                    val listaCategorias = response.body()?.response?.categories ?: emptyList()

                    if (listaCategorias.isNotEmpty()) {
                        categoriasIds.addAll(listaCategorias)
                        Log.d("API_CATEGORIES", "Categorias estruturadas carregadas com sucesso!")
                    } else {
                        Log.w("API_CATEGORIES", "Lista do backend vazia. Injetando fallback seguro...")
                        categoriasIds.addAll(obterCategoriasPadrao())
                    }
                } else {
                    Log.e("API_CATEGORIES", "Erro de resposta HTTP: ${response.code()}")
                    categoriasIds.addAll(obterCategoriasPadrao())
                }
            } catch (e: Exception) {
                Log.e("API_CATEGORIES", "Falha crítica de conexão ao buscar categorias: ${e.message}")
                if (categoriasIds.isEmpty()) {
                    categoriasIds.addAll(obterCategoriasPadrao())
                }
            }
        }
    }

    // Fallback corrigido para TypeCategoryDetail e CategoryDetail com IDs em Int
    private fun obterCategoriasPadrao(): List<CategoryDetail> {
        val tipoServico = TypeCategoryDetail(id = 1, name = "SERVICO")
        val tipoObjeto = TypeCategoryDetail(id = 2, name = "OBJETO")

        return listOf(
            CategoryDetail(id = 1, name = "Reformas & Reparos", description = "Manutenções gerais", typeCategory = tipoServico),
            CategoryDetail(id = 2, name = "Limpeza & Organização", description = "Serviços domésticos", typeCategory = tipoServico),
            CategoryDetail(id = 3, name = "Empréstimos de Objetos", description = "Compartilhamento de utensílios", typeCategory = tipoObjeto),
            CategoryDetail(id = 4, name = "Cuidados & Pet Sitter", description = "Animais e plantas", typeCategory = tipoServico),
            CategoryDetail(id = 5, name = "Aulas & Consultoria", description = "Aulas particulares", typeCategory = tipoServico),
            CategoryDetail(id = 6, name = "Outros Serviços", description = "Utilidades variadas", typeCategory = tipoServico)
        )
    }

    fun postarServico(
        context: Context,
        token: String,
        titulo: String,
        descricao: String,
        urgencia: String,
        tempoEstimado: Int,
        categoryId: Int,
        imagemUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (titulo.trim().length > 45) {
            onError("O título escolhido é muito longo. Use no máximo 45 caracteres.")
            return
        }
        if (titulo.trim().length < 4) {
            onError("Insira um título mais descritivo (mínimo 4 caracteres).")
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // Conversão limpa para Base64 (sem quebras de linha ou modificações de caracteres)
                val base64Foto = if (imagemUri != null) {
                    converterImagemParaBase64(context, imagemUri).trim()
                } else ""

                val urgencyTratada = when (urgencia.uppercase()) {
                    "MÉDIA", "MEDIA" -> "MEDIA"
                    "ALTA" -> "ALTA"
                    else -> "BAIXA"
                }

                val payload = CreateServiceRequest(
                    categoryId = categoryId,
                    description = descricao.trim(),
                    estimatedTime = tempoEstimado,
                    photoBase64 = base64Foto, // Envia a string limpa extraída do Uri
                    status = "ACTIVE",
                    title = titulo.trim(),
                    urgency = urgencyTratada
                )

                Log.d("API_SERVICE", "Enviando payload: $payload")

                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.authApi.criarServico(authHeader, payload)
                }

                Log.d("API_SERVICE", "Código HTTP retornado: ${response.code()}")

                if (response.isSuccessful) {
                    val respostaCorpo = response.body()
                    if (respostaCorpo != null && respostaCorpo.status) {
                        onSuccess()
                    } else {
                        onError(respostaCorpo?.message ?: "Servidor recusou os dados de entrada.")
                    }
                } else {
                    val erroTexto = response.errorBody()?.string()
                    Log.e("API_SERVICE", "Erro retornado pela API: $erroTexto")

                    when (response.code()) {
                        409 -> {
                            try {
                                val jsonErro = JSONObject(erroTexto ?: "")
                                val messageApi = jsonErro.optString("message", "Conflito de integridade de dados.")
                                onError("Não foi possível salvar: $messageApi")
                            } catch (e: Exception) {
                                onError("Este título de serviço já está ativo no seu perfil.")
                            }
                        }
                        400 -> {
                            try {
                                val jsonErro = JSONObject(erroTexto ?: "")
                                val messageApi = jsonErro.optString("message", "Campos inválidos.")
                                onError("Erro na validação do formulário: $messageApi")
                            } catch (e: Exception) {
                                onError("Verifique o preenchimento dos dados obrigatórios.")
                            }
                        }
                        else -> {
                            onError("Erro ${response.code()}: Não foi possível processar a requisição no servidor.")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API_SERVICE_ERROR", "Falha de rede/conexão física: ${e.message}")
                onError("Não foi possível alcançar o servidor. Verifique sua conexão.")
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun converterImagemParaBase64(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmapOriginal != null) {
                val bitmapRedimensionado = Bitmap.createScaledBitmap(bitmapOriginal, 500, 500, true)
                val outputStream = ByteArrayOutputStream()
                bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val bytes = outputStream.toByteArray()
                outputStream.close()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e("CONVERT_IMAGE_BASE64", "Erro ao processar Uri: ${e.message}")
            ""
        }
    }
}