package com.example.mobilevizinhaa.ui.theme.mural

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado da UI do Mural
data class MuralUiState(
    val posts: List<ServiceDetailBackend> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MuralViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MuralUiState())
    val uiState: StateFlow<MuralUiState> = _uiState.asStateFlow()

    /**
     * CONECTADO À API: Busca os dados reais de SERVIÇOS e OBJETOS de todos do condomínio,
     * filtrando estritamente por quem está "EM_ANDAMENTO" e "DISPONIVEL".
     * @param token O token de autenticação do usuário logado enviado pela View.
     */
    fun carregarPostsReais(token: String) {
        if (token.isEmpty()) {
            Log.e("MURAL_FILTRO", "ERRO: O token recebido está VAZIO (\"\"). A requisição não foi enviada.")
            _uiState.value = MuralUiState(
                isLoading = false,
                errorMessage = "Erro de autenticação: Token não encontrado."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = MuralUiState(isLoading = true)
            try {
                // Formata o Header de Autorização padrão do projeto
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                Log.d("MURAL_FILTRO", "Enviando requisição com o Header: $authHeader")

                // 🎯 BUSCA PARALELA: Dispara as duas chamadas simultaneamente
                val chamadaServicos = async { RetrofitClient.authApiParaServico.listarServicosPaginados(authHeader) }
                val llamadaObjetos = async { RetrofitClient.authApiParaServico.listarObjetosPaginados(authHeader) }

                val responseServicos = chamadaServicos.await()
                val responseObjetos = llamadaObjetos.await()

                val listaUnificadaMural = mutableListOf<ServiceDetailBackend>()

                // 1. 🛠️ TRATAMENTO DOS SERVIÇOS (Filtra apenas quem está em andamento)
                if (responseServicos.isSuccessful && responseServicos.body() != null) {
                    val envelopeServicos = responseServicos.body()!!
                    val listaGeralServicos = envelopeServicos.responseData.content

                    Log.d("MURAL_FILTRO", "Serviços recebidos (Bruto): ${listaGeralServicos.size}")

                    val servicosFiltrados = listaGeralServicos.filter { item ->
                        val statusLimpo = item.status?.trim()?.uppercase() ?: ""
                        statusLimpo == "EM_ANDAMENTO" || statusLimpo == "EM ANDAMENTO"
                    }.map { item ->
                        item.copy(
                            photoBase64 = limparStringFoto(item.photoBase64),
                            urgency = "SERVICO"
                        )
                    }
                    listaUnificadaMural.addAll(servicosFiltrados)
                }

                // 2. 📦 TRATAMENTO DOS OBJETOS (Filtra apenas quem está disponível)
                if (responseObjetos.isSuccessful && responseObjetos.body() != null) {
                    val envelopeObjetos = responseObjetos.body()!!
                    val container = envelopeObjetos.responseData
                    val listaGeralObjetos = container?.content ?: emptyList()

                    Log.d("MURAL_FILTRO", "Objetos recebidos (Bruto): ${listaGeralObjetos.size}")

                    val objetosFiltrados = listaGeralObjetos.filter { obj ->
                        val statusLimpo = obj.status?.trim()?.uppercase() ?: ""
                        statusLimpo == "DISPONIVEL" || statusLimpo == "DISPONÍVEL"
                    }.map { obj ->
                        // Converte estritamente o ObjectDetailBackend mapeado no Retrofit para a estrutura visual ServiceDetailBackend
                        ServiceDetailBackend(
                            id = obj.id,
                            photoBase64 = limparStringFoto(obj.photo), // Pega a propriedade correta mapeada pelo Retrofit (.photo)
                            title = obj.title ?: "Sem título",
                            estimatedTime = 0,
                            urgency = "OBJETO",
                            description = obj.description ?: "Nenhuma descrição informada.",
                            creationDate = obj.creationDate ?: "Recentemente",
                            status = "DISPONIVEL",
                            resident = obj.resident, // Atribui a instância do ResidentDetail original
                            category = obj.category
                        )
                    }
                    listaUnificadaMural.addAll(objetosFiltrados)
                }

                Log.d("MURAL_FILTRO", "Total Unificado Exibido no Mural: ${listaUnificadaMural.size}")

                if (responseServicos.isSuccessful || responseObjetos.isSuccessful) {
                    _uiState.value = MuralUiState(
                        posts = listaUnificadaMural,
                        isLoading = false
                    )
                } else {
                    _uiState.value = MuralUiState(
                        isLoading = false,
                        errorMessage = "Não foi possível carregar o mural do condomínio."
                    )
                }

            } catch (e: Exception) {
                Log.e("MURAL_FILTRO", "Falha catastrófica no MuralViewModel", e)
                _uiState.value = MuralUiState(
                    isLoading = false,
                    errorMessage = "Sem conexão com o servidor do condomínio."
                )
            }
        }
    }

    private fun limparStringFoto(foto: String?): String {
        val fotoFormatada = foto?.trim() ?: ""
        return if (fotoFormatada.lowercase() == "string" || fotoFormatada.isBlank()) "" else fotoFormatada
    }
}