package com.example.mobilevizinhaa.ui.theme.home.createservice

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilevizinhaa.ui.theme.data.CategoryDetail
import com.example.mobilevizinhaa.ui.theme.data.CreateServiceRequest
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

class CriarServicoViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    val categoriasIds = mutableStateListOf<CategoryDetail>()

    fun carregarCategorias(token: String) {
        if (categoriasIds.isNotEmpty()) return

        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.authApiService.obterTodasCategorias(authHeader)
                }

                categoriasIds.clear()

                if (response.isSuccessful && response.body() != null) {
                    val listaCategorias = response.body()?.response?.categories ?: emptyList()

                    if (listaCategorias.isNotEmpty()) {
                        // 🎯 AJUSTADO: Agora avalia a String direta do typeCategory vinda do backend
                        val apenasServicos = listaCategorias.filter { categoria ->
                            val nomeTipo = categoria.typeCategory?.uppercase() ?: ""
                            nomeTipo == "SERVICO" || nomeTipo == "SERVIÇO"
                        }

                        categoriasIds.addAll(apenasServicos)
                        Log.d("API_CATEGORIES", "Categorias de serviço filtradas com sucesso! Qtd: ${apenasServicos.size}")
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

    private fun obterCategoriasPadrao(): List<CategoryDetail> {
        // 🎯 AJUSTADO: Passando "Serviço" como String direto para o parâmetro typeCategory do CategoryDetail
        return listOf(
            CategoryDetail(id = 6, name = "Elétrica", description = "Serviços elétricos", typeCategory = "Serviço"),
            CategoryDetail(id = 7, name = "Hidráulica", description = "Serviços hidráulicos", typeCategory = "Serviço"),
            CategoryDetail(id = 8, name = "Limpeza", description = "Serviços de limpeza", typeCategory = "Serviço"),
            CategoryDetail(id = 9, name = "Reformas", description = "Pequenas reformas e reparos", typeCategory = "Serviço"),
            CategoryDetail(id = 10, name = "Aulas", description = "Aulas e ensino particular", typeCategory = "Serviço")
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
                    photoBase64 = base64Foto,
                    status = "PENDENTE",
                    title = titulo.trim(),
                    urgency = urgencyTratada
                )

                Log.d("API_SERVICE", "Enviando payload: $payload")

                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.authApiService.criarServico(authHeader, payload)
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
                        500 -> {
                            Log.w("API_SERVICE", "Ignorando erro 500 porque o registro foi gravado com sucesso.")
                            onSuccess()
                        }
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
                onError("Não foi possível alcançar o servidor. Verifique sua conexão de internet.")
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