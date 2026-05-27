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
import java.io.ByteArrayOutputStream
import java.io.InputStream

class CriarServicoViewModel : ViewModel() {

    // Controla o estado de carregamento reativo (Ex: exibe o CircularProgressIndicator na UI)
    var isLoading by mutableStateOf(false)
        private set

    // Lista reativa que o Jetpack Compose escuta para atualizar o Dropdown na hora
    val categoriasIds = mutableStateListOf<CategoryDetail>()

    /**
     * ATUALIZADO: Busca as categorias diretamente do novo endpoint da API (/api/v1/category)
     */
    fun carregarCategorias(token: String) {
        // Evita chamadas repetidas desnecessárias se já houver dados na lista
        if (categoriasIds.isNotEmpty()) return

        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // Chamada ao novo endpoint dedicado a listagem limpa de categorias
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
                // Fallback de segurança para não travar o app do usuário se a API cair
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
     * Envia o novo serviço/objeto para o backend com o ID da categoria selecionada dinamicamente.
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
        viewModelScope.launch {
            isLoading = true
            try {
                // Converte a foto selecionada em Base64 de forma assíncrona
                val base64Foto = if (imagemUri != null) {
                    converterImagemParaBase64(context, imagemUri)
                } else ""

                // Tratamento para garantir que strings de urgência com acento não quebrem o Enum do Banco
                val urgenciaTratada = when (urgencia.uppercase()) {
                    "MÉDIA", "MEDIA" -> "MEDIA"
                    "ALTA" -> "ALTA"
                    else -> "BAIXA"
                }

                val payload = CreateServiceRequest(
                    title = titulo,
                    description = descricao,
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
                    val erroTexto = response.errorBody()?.string()
                    Log.e("API_SERVICE", "Erro bruto retornado do servidor: $erroTexto")
                    onError("Erro ${response.code()}: Falha na validação dos campos.")
                }
            } catch (e: Exception) {
                Log.e("API_SERVICE_ERROR", "Falha crítica de conexão: ${e.message}")
                onError("Não foi possível conectar ao servidor do backend.")
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
                // Redimensiona para 500x500 para não estourar o buffer de transferência de texto HTTP
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