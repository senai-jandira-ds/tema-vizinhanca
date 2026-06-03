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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PedidosObjetosViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var listaMeusItens by mutableStateOf<List<ServiceDetailBackend>>(emptyList())
        private set

    fun carregarItensDoUsuario(token: String, idUsuarioLogado: Int) {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // Busca serviços e objetos em paralelo para maior desempenho de rede
                val chamadaServicos = async { RetrofitClient.authApiParaServico.listarServicosPaginados(authHeader) }
                val chamadaObjetos = async { RetrofitClient.authApiParaServico.listarObjetosPaginados(authHeader) }

                val responseServicos = chamadaServicos.await()
                val responseObjetos = chamadaObjetos.await()

                val listaFinalUnificada = mutableListOf<ServiceDetailBackend>()

                // 1. Processamento de Serviços (Garante remoção de espaços e caixa alta estável)
                if (responseServicos.isSuccessful && responseServicos.body() != null) {
                    val envelopeServicos = responseServicos.body()!!
                    val listaGeralServicos = envelopeServicos.responseData.content

                    val servicosFiltrados = listaGeralServicos.filter { item ->
                        item.resident?.id == idUsuarioLogado
                    }.map { item ->
                        item.copy(status = item.status?.uppercase()?.trim() ?: "PENDENTE")
                    }
                    listaFinalUnificada.addAll(servicosFiltrados)
                } else {
                    Log.e("PEDIDOS_OBJETOS_VM", "Erro ao carregar serviços: ${responseServicos.code()}")
                }

                // 2. Processamento e Normalização de Objetos
                if (responseObjetos.isSuccessful && responseObjetos.body() != null) {
                    val envelopeObjetos = responseObjetos.body()!!
                    val listaGeralObjetos = envelopeObjetos.responseData.content

                    val objetosFiltradosEConvertidos = listaGeralObjetos
                        .filter { obj -> obj.resident?.id == idUsuarioLogado }
                        .map { obj ->
                            // Garante uma string limpa sem acentos ou espaços invisíveis vindos da API
                            val statusTratado = obj.status?.uppercase()?.trim() ?: "DISPONIVEL"

                            ServiceDetailBackend(
                                id = obj.id,
                                photoBase64 = obj.photo,
                                title = obj.title ?: "Sem título",
                                estimatedTime = 0,
                                urgency = "NORMAL",
                                description = obj.description ?: "Nenhuma descrição informada.",
                                creationDate = obj.creationDate ?: "Recentemente",
                                status = statusTratado,
                                resident = obj.resident,
                                category = obj.category
                            )
                        }
                    listaFinalUnificada.addAll(objetosFiltradosEConvertidos)
                } else {
                    Log.e("PEDIDOS_OBJETOS_VM", "Erro ao carregar objetos: ${responseObjetos.code()}")
                }

                // Substitui a lista inteira de uma vez para notificar o Compose
                listaMeusItens = listaFinalUnificada
                Log.d("PEDIDOS_OBJETOS_VM", "Mesclagem concluída. Total de itens carregados: ${listaFinalUnificada.size}")

                if (!responseServicos.isSuccessful && !responseObjetos.isSuccessful) {
                    errorMessage = "Não foi possível sincronizar suas solicitações."
                }

            } catch (e: Exception) {
                Log.e("PEDIDOS_OBJETOS_VM", "Falha de rede ao unificar listas: ${e.message}", e)
                errorMessage = "Não foi possível conectar ao servidor."
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Atualiza o status do item enviando a requisição DTO correta para o servidor.
     * Atualiza o estado reativo local imediatamente para redesenhar a tela e aplicar os filtros.
     */
    fun atualizarStatusItem(token: String, idItem: Int, novoStatus: String) {
        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val statusCaixaAlta = novoStatus.uppercase().trim()
                val corpoRequest = ServiceUpdateRequest(status = statusCaixaAlta)

                val response = RetrofitClient.authApiParaServico.atualizarStatusServico(authHeader, idItem, corpoRequest)

                if (response.isSuccessful) {
                    // Força a atualização do estado criando uma nova lista mapeada.
                    // Isso aciona instantaneamente a filtragem horizontal e vertical na Screen.
                    listaMeusItens = listaMeusItens.map { item ->
                        if (item.id == idItem) item.copy(status = statusCaixaAlta) else item
                    }
                    Log.d("INTEGRACAO_VM", "Status updated successfully on API and locally to $statusCaixaAlta.")
                } else {
                    Log.e("INTEGRACAO_VM", "Falha HTTP ao mudar status: ${response.code()} | Erro: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("INTEGRACAO_VM", "Erro na requisição de alteração de status", e)
            }
        }
    }

    /**
     * Deleta permanentemente o item selecionado e limpa o estado reativo local.
     */
    fun excluirItemDoUsuario(token: String, idItem: Int) {
        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authApiParaServico.deletarServico(authHeader, idItem)

                if (response.isSuccessful) {
                    // Remove localmente provocando a animação de saída na Screen
                    listaMeusItens = listaMeusItens.filter { item -> item.id != idItem }
                    Log.d("INTEGRACAO_VM", "Item $idItem deletado com sucesso na API e localmente.")
                } else {
                    Log.e("INTEGRACAO_VM", "Falha ao deletar na API: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("INTEGRACAO_VM", "Erro ao deletar", e)
            }
        }
    }

    /**
     * Limpa as referências de memória ao deslogar ou sair da tela.
     */
    fun limparDados() {
        listaMeusItens = emptyList()
        errorMessage = null
        Log.d("PEDIDOS_OBJETOS_VM", "Dados limpos com sucesso.")
    }
}