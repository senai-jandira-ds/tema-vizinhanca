package com.example.mobilevizinhaa.ui.theme.home.createobjeto

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
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
import java.io.ByteArrayOutputStream
import java.io.InputStream

class CriarServicoViewModel : ViewModel() {

    // Controla o estado de carregamento reativo (Ex: exibe o CircularProgressIndicator na UI)
    var isLoading by mutableStateOf(false)
        private set

    // Lista reativa que utiliza diretamente o tipo CategoryDetail unificado com o Swagger
    var categoriasIds by mutableStateOf<List<CategoryDetail>>(emptyList())
        private set

    /**
     * Busca as categorias existentes no condomínio a partir dos serviços listados.
     * Mapeia o JSON recebido eliminando duplicatas.
     */
    fun carregarCategorias(token: String) {
        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.authApi.listarServicos(authHeader)
                }

                if (response.isSuccessful && response.body() != null) {
                    val listaServicos = response.body()?.response?.services ?: emptyList()

                    // Extrai o objeto de categoria ignorando itens nulos
                    val categoriasMapeadas = listaServicos.mapNotNull { item ->
                        item.category
                    }.distinctBy { it.id } // Remove duplicados pelo ID da categoria para alimentar o Dropdown

                    categoriasIds = categoriasMapeadas
                } else {
                    Log.e("API_CATEGORIES", "Erro na resposta de categorias: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_CATEGORIES", "Falha catastrófica ao buscar categorias: ${e.message}")
            }
        }
    }

    /**
     * Envia o novo serviço/objeto para o backend com o ID da categoria selecionada dinamicamente.
     */
    fun postarServico(
        context: Context,
        token: String,
        titulo: String,
        descricao: String,
        urgencia: String, // Parâmetro recebido em String da UI Screen ("BAIXA", "MEDIA", "ALTA")
        tempoEstimado: Int,
        categoryId: Int, // ID selecionado na Combobox
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

                // CORRIGIDO: Tratamento para garantir que strings de urgência com acento não quebrem o Enum do Banco
                val urgenciaTratada = when (urgencia.uppercase()) {
                    "MÉDIA", "MEDIA" -> "MEDIA"
                    "ALTA" -> "ALTA"
                    else -> "BAIXA"
                }

                // CORRIGIDO: Nomeação dos parâmetros ajustada de acordo com as propriedades da Data Class revisada
                val payload = CreateServiceRequest(
                    title = titulo,
                    description = descricao,
                    photoBase64 = base64Foto,
                    estimatedTime = tempoEstimado,
                    urgency = urgenciaTratada,
                    categoryId = categoryId
                )

                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // CORREÇÃO CRUCIAL AQUI: Mudamos para 'authApiParaServico' para contornar o bug do interceptor antigo
                // e fazer o JSON chegar 100% preenchido ao validador do Leonardo Scotti.
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.authApiParaServico.criarServico(authHeader, payload)
                }

                // Log auxiliar para monitorar a resposta do Render no seu Logcat
                Log.d("API_SERVICE", "Status HTTP: ${response.code()}")

                if (response.isSuccessful) {
                    val respostaCorpo = response.body()
                    if (respostaCorpo != null && respostaCorpo.status) {
                        onSuccess()
                    } else {
                        onError(respostaCorpo?.message ?: "O servidor recusou a criação do serviço.")
                    }
                } else {
                    val erroTexto = response.errorBody()?.string()
                    Log.e("API_SERVICE", "Erro bruto do servidor: $erroTexto")
                    onError("Erro ${response.code()}: Falha na validação dos campos.")
                }
            } catch (e: Exception) {
                Log.e("API_SERVICE_ERROR", "Falha de conexão: ${e.message}")
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