package com.example.mobilevizinhaa.ui.theme.listaitens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend
import kotlinx.coroutines.launch

class PedidosObjetosViewModel : ViewModel() {

    // Controla se a tela está carregando os dados do servidor
    var isLoading by mutableStateOf(false)
        private set

    // Armazena mensagens de erro físicas ou de validação da API
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Lista reativa que guardará exclusivamente os itens criados por este usuário
    val listaMeusItens = mutableStateListOf<ServiceDetailBackend>()

    /**
     * Busca todos os serviços da API e realiza o filtro local pelo ID do residente logado.
     *
     * @param token Token JWT do usuário (com ou sem o prefixo Bearer).
     * @param idUsuarioLogado O ID numérico do usuário logado (ex: obtido no LoginResponse ou no /auth/me).
     */
    fun carregarItensDoUsuario(token: String, idUsuarioLogado: Int) {
        // Evita chamadas duplicadas simultâneas se já estiver carregando
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Formata o Header de Autorização corretamente
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // CORREÇÃO: Trocado 'apiService' por 'authApiParaServico' para alinhar com seu RetrofitClient
                val response = RetrofitClient.authApiParaServico.listarServicosPaginados(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val envelope = response.body()!!

                    // Extrai a lista do array "content" dentro do objeto "response"
                    val listaGeralDoCondominio = envelope.responseData.content

                    // Aplica a regra de negócio: traz APENAS os serviços em que o id do residente bate com o logado
                    val filtradoParaOusuario = listaGeralDoCondominio.filter { item ->
                        item.resident?.id == idUsuarioLogado
                    }

                    // Atualiza o estado reativo da tela com segurança (tudo em minúsculo)
                    listaMeusItens.clear()
                    listaMeusItens.addAll(filtradoParaOusuario)

                    Log.d("PEDIDOS_OBJETOS_VM", "Itens carregados e filtrados com sucesso. Total: ${filtradoParaOusuario.size}")
                } else {
                    val codigoErro = response.code()
                    errorMessage = "Erro $codigoErro: Não foi possível sincronizar suas solicitações."
                    Log.e("PEDIDOS_OBJETOS_VM", "Resposta sem sucesso da API: $codigoErro")
                }
            } catch (e: Exception) {
                Log.e("PEDIDOS_OBJETOS_VM", "Falha catastrófica de rede ou parsing: ${e.message}", e)
                errorMessage = "Não foi possível conectar ao servidor. Verifique sua conexão com a internet."
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Função auxiliar opcional para limpar o estado da ViewModel ao fazer logout.
     */
    fun limparDados() {
        listaMeusItens.clear()
        errorMessage = null
    }
}