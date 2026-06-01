package com.example.mobilevizinhaa.ui.theme.listaitens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend
import com.example.mobilevizinhaa.ui.theme.data.ServiceUpdateRequest
import kotlinx.coroutines.launch

class PedidosObjetosViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var listaMeusItens by mutableStateOf<List<ServiceDetailBackend>>(emptyList())
        private set

    /**
     * Busca todos os serviços da API e realiza o filtro local pelo ID do residente logado.
     */
    fun carregarItensDoUsuario(token: String, idUsuarioLogado: Int) {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authApiParaServico.listarServicosPaginados(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val envelope = response.body()!!
                    val listaGeralDoCondominio = envelope.responseData.content

                    val filtradoParaOusuario = listaGeralDoCondominio.filter { item ->
                        item.resident?.id == idUsuarioLogado
                    }

                    listaMeusItens = filtradoParaOusuario
                    Log.d("PEDIDOS_OBJETOS_VM", "Itens carregados: ${filtradoParaOusuario.size}")
                } else {
                    errorMessage = "Erro ${response.code()}: Não foi possível sincronizar suas solicitações."
                }
            } catch (e: Exception) {
                Log.e("PEDIDOS_OBJETOS_VM", "Falha de rede: ${e.message}", e)
                errorMessage = "Não foi possível conectar ao servidor."
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * CORRIGIDO: Agora envia o DTO ServiceUpdateRequest no corpo do método PUT
     * para sanar o erro 500 de falta de request body gerado pelo Spring Boot.
     */
    fun mudarStatusParaAndamento(token: String, idItem: Int) {
        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // 1. Cria o corpo do JSON encapsulando a String exigida pela API do condomínio
                val corpoRequest = ServiceUpdateRequest(status = "EM_ANDAMENTO")

                // 2. Envia o objeto via @Body para o endpoint mapeado no Retrofit
                val response = RetrofitClient.authApiParaServico.atualizarStatusServico(authHeader, idItem, corpoRequest)

                if (response.isSuccessful) {
                    // Mapeia a lista existente modificando APENAS o item que mudou de status.
                    // Isso força a tela que você criou a se redesenhar automaticamente na hora!
                    listaMeusItens = listaMeusItens.map { item ->
                        if (item.id == idItem) item.copy(status = "EM_ANDAMENTO") else item
                    }
                    Log.d("INTEGRACAO_VM", "Status do item $idItem alterado para EM_ANDAMENTO com sucesso local e remoto.")
                } else {
                    Log.e("INTEGRACAO_VM", "Falha ao mudar status. Código HTTP: ${response.code()} | Erro: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("INTEGRACAO_VM", "Erro na requisição de alteração de status", e)
            }
        }
    }

    /**
     * INTEGRADO: Deleta a solicitação permanentemente no backend e remove da lista local na hora.
     */
    fun excluirItemDoUsuario(token: String, idItem: Int) {
        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // Dispara o DELETE para a API
                val response = RetrofitClient.authApiParaServico.deletarServico(authHeader, idItem)

                if (response.isSuccessful) {
                    // Remove o item excluído da lista local para sumir da tela instantaneamente
                    listaMeusItens = listaMeusItens.filter { item -> item.id != idItem }
                    Log.d("INTEGRACAO_VM", "Item $idItem deletado com sucesso.")
                } else {
                    Log.e("INTEGRACAO_VM", "Falha ao deletar item: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("INTEGRACAO_VM", "Erro na requisição de exclusão", e)
            }
        }
    }

    fun limparDados() {
        listaMeusItens = emptyList()
        errorMessage = null
    }
}