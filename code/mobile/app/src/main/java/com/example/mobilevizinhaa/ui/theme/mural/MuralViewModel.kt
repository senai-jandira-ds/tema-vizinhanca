package com.example.mobilevizinhaa.ui.theme.mural

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend
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
     * CONECTADO À API: Busca os dados reais e filtra estritamente quem está EM ANDAMENTO.
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

                // 1. Consome o endpoint paginado trazendo a lista geral do condomínio
                val response = RetrofitClient.authApiParaServico.listarServicosPaginados(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val envelope = response.body()!!
                    val listaGeralDoCondominio = envelope.responseData.content

                    Log.d("MURAL_FILTRO", "Sucesso! Total bruto recebido da API: ${listaGeralDoCondominio.size}")

                    // 2. 🎯 FILTRO ATIVADO: Filtra apenas quem está em andamento (removendo o teste provisório anterior)
                    val apenasEmAndamento = listaGeralDoCondominio.filter { item ->
                        // .trim() remove espaços extras invisíveis que o banco possa ter salvo por engano
                        val statusLimpo = item.status?.trim()?.uppercase() ?: ""
                        statusLimpo == "EM_ANDAMENTO" || statusLimpo == "EM ANDAMENTO"
                    }

                    Log.d("MURAL_FILTRO", "Filtro Aplicado! Itens exibidos no mural: ${apenasEmAndamento.size}")

                    // 3. Alimenta o StateFlow da UI estritamente com os posts filtrados
                    _uiState.value = MuralUiState(
                        posts = apenasEmAndamento,
                        isLoading = false
                    )
                } else {
                    Log.e("MURAL_FILTRO", "Servidor respondeu com erro Código: ${response.code()}")
                    _uiState.value = MuralUiState(
                        isLoading = false,
                        errorMessage = "Erro ${response.code()}: Não foi possível carregar o mural."
                    )
                }
            } catch (e: Exception) {
                Log.e("MURAL_FILTRO", "Falha catastrófica de conexão / Exceção capturada", e)
                _uiState.value = MuralUiState(
                    isLoading = false,
                    errorMessage = "Sem conexão com o servidor do condomínio."
                )
            }
        }
    }
}