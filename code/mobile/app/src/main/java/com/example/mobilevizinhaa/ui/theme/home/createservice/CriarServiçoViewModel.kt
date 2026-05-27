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

    val categoriasIds = mutableStateListOf<CategoryDetail>()

    /**
     * ATUALIZADO: Busca as categorias diretamente do novo endpoint da API (/api/v1/category)
     */
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
                        Log.d("API_CATEGORIES", "Categorias carregadas direto do novo endpoint com sucesso!")
                    } else {
                        Log.w("API_CATEGORIES", "Endpoint retornou lista vazia. Carregando dados locais de segurança...")
                        categoriasIds.addAll(obterCategoriasPadrao())
                    }
                } else {
                    Log.e("API_CATEGORIES", "Erro no servidor ao buscar categorias: ${response.code()}")
                    categoriasIds.addAll(obterCategoriasPadrao())
                }
            } catch (e: Exception) {
                Log.e("API_CATEGORIES", "Falha na conexão com o endpoint /api/v1/category: ${e.message}")
                if (categoriasIds.isEmpty()) {
                    categoriasIds.addAll(obterCategoriasPadrao())
                }
            }
        }
    }

    /**
     * ATUALIZADO: Categorias locais de segurança mapeadas para a nova estrutura complexa do JSON
     */
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

    /**
     * CORRIGIDO: Envia dados tratando erros de restrição/duplicidade HTTP 409 do banco de dados
     */
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
        // 1. VALIDAÇÃO LOCAL PREVENTIVA: Evita enviar títulos vazios ou massivos que quebram o banco
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
                    converterImagemParaBase64(context, imagemUri)
                } else ""

                val urgenciaTratada = when (urgencia.uppercase()) {
                    "MÉDIA", "MEDIA" -> "MEDIA"
                    "ALTA" -> "ALTA"
                    else -> "BAIXA"
                }

                val payload = CreateServiceRequest(
                    title = titulo.trim(),
                    description = descricao.trim(),
                    photoBase64 = base64Foto,
                    estimatedTime = tempoEstimado,
                    urgency = urgenciaTratada,
                    categoryId = categoryId
                )

                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.authApiParaServico.criarServico(authHeader, payload)
                }

                Log.d("API_SERVICE", "Status HTTP de Envio: ${response.code()}")

                if (response.isSuccessful) {
                    val respostaCorpo = response.body()
                    if (respostaCorpo != null && respostaCorpo.status) {
                        onSuccess()
                    } else {
                        onError(respostaCorpo?.message ?: "O servidor recusou a criação do serviço.")
                    }
                } else {
                    // 2. CAPTURA DO ERRO 409: Intercepta a resposta bruta do servidor e lê o JSON de erro
                    val erroTexto = response.errorBody()?.string()
                    Log.e("API_SERVICE", "Erro bruto retornado do servidor: $erroTexto")

                    if (response.code() == 409) {
                        try {
                            val jsonErro = JSONObject(erroTexto ?: "")
                            val mensagemApi = jsonErro.optString("message", "Dado duplicado ou violação de integridade!")
                            onError("Não foi possível salvar: $mensagemApi Tente usar um título diferente.")
                        } catch (e: Exception) {
                            onError("Este título já está em uso ou é inválido para o sistema.")
                        }
                    } else {
                        onError("Erro ${response.code()}: Falha na validação dos campos do formulário.")
                    }
                }
            } catch (e: Exception) {
                Log.e("API_SERVICE_ERROR", "Falha crítica de conexão: ${e.message}")
                onError("Não foi possível conectar ao servidor do backend. Verifique a internet.")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Helper assíncrono para redimensionamento e compressão de imagens em IO thread
     */
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