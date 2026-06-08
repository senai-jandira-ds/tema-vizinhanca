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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

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

                // 1. Processamento de Serviços (Mantido Paginado com .content)
                if (responseServicos.isSuccessful && responseServicos.body() != null) {
                    val envelopeServicos = responseServicos.body()!!
                    val listaGeralServicos = envelopeServicos.responseData.content

                    val servicosFiltrados = listaGeralServicos.filter { item ->
                        item.resident?.id == idUsuarioLogado
                    }.map { item ->
                        val fotoLimpa = if (item.photoBase64?.lowercase()?.trim() == "string" || item.photoBase64.isNullOrBlank()) {
                            null
                        } else {
                            item.photoBase64.trim()
                        }

                        // 🎯 Tag para diferenciar internamente que este item é originalmente do tipo SERVICO
                        item.copy(
                            status = item.status?.uppercase()?.trim() ?: "PENDENTE",
                            photoBase64 = fotoLimpa,
                            urgency = "SERVICO"
                        )
                    }
                    listaFinalUnificada.addAll(servicosFiltrados)
                    Log.d("PEDIDOS_OBJETOS_VM", "Serviços do usuário filtrados: ${servicosFiltrados.size}")
                } else {
                    Log.e("PEDIDOS_OBJETOS_VM", "Erro ao carregar serviços: ${responseServicos.code()}")
                }

                // 2. Processamento de Objetos
                if (responseObjetos.isSuccessful && responseObjetos.body() != null) {
                    val envelopeObjetos = responseObjetos.body()!!
                    val container = envelopeObjetos.responseData
                    val listaGeralObjetos = container.content ?: emptyList()

                    val objetosFiltradosEConvertidos = listaGeralObjetos
                        .filter { obj -> obj.resident?.id == idUsuarioLogado }
                        .map { obj ->
                            val statusTratado = obj.status?.uppercase()?.trim() ?: "DISPONIVEL"
                            val fotoBruta = obj.photo?.trim()

                            val fotoLimpa = if (fotoBruta?.lowercase() == "string" || fotoBruta.isNullOrBlank()) {
                                null
                            } else {
                                fotoBruta
                            }

                            Log.d("PEDIDOS_OBJETOS_VM", "Objeto ID: ${obj.id} | Status: $statusTratado")

                            ServiceDetailBackend(
                                id = obj.id,
                                photoBase64 = fotoLimpa,
                                title = obj.title ?: "Sem título",
                                estimatedTime = 0,
                                urgency = "OBJETO", // 🎯 Tag para diferenciar internamente que este item é um OBJETO
                                description = obj.description ?: "Nenhuma descrição informada.",
                                creationDate = obj.creationDate ?: "Recentemente",
                                status = statusTratado,
                                resident = obj.resident,
                                category = obj.category
                            )
                        }
                    listaFinalUnificada.addAll(objetosFiltradosEConvertidos)
                    Log.d("PEDIDOS_OBJETOS_VM", "Objetos do usuário convertidos com sucesso: ${objetosFiltradosEConvertidos.size}")
                } else {
                    Log.e("PEDIDOS_OBJETOS_VM", "Erro ao carregar objetos: ${responseObjetos.code()}")
                }

                listaMeusItens = listaFinalUnificada
                Log.d("PEDIDOS_OBJETOS_VM", "Mesclagem concluída com sucesso. Total unificado: ${listaFinalUnificada.size} itens.")

                if (!responseServicos.isSuccessful && !responseObjetos.isSuccessful) {
                    errorMessage = "Não foi possível sincronizar suas solicitações."
                }

            } catch (e: Exception) {
                Log.e("PEDIDOS_OBJETOS_VM", "Falha crítica de rede ao unificar listas: ${e.message}", e)
                errorMessage = "Não foi possível conectar ao servidor."
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * 🎯 REGRA DE NEGÓCIO DO MURAL: Detecta dinamicamente se o item selecionado é um
     * Serviço ou Objeto, altera para o status correto na API respectiva e engatilha o post no Mural.
     */
    fun colocarItemNoMural(token: String, idItem: Int) {
        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // Localiza o item atual na lista reativa para extrair suas propriedades
                val itemAlvo = listaMeusItens.firstOrNull { it.id == idItem } ?: return@launch

                // Determina o tipo real do item com base na tag que mapeamos no carregamento
                val ehObjeto = itemAlvo.urgency == "OBJETO" || itemAlvo.category?.name?.contains("Objeto", ignoreCase = true) == true

                // Define o novo status baseado na regra do TCC
                val statusDefinido = if (ehObjeto) "DISPONIVEL" else "EM_ANDAMENTO"

                Log.d("MURAL_INTEGRACAO", "Modificando Item ID: $idItem | Tipo Objeto: $ehObjeto -> Novo Status: $statusDefinido")

                // Executa a chamada do endpoint CORRETO com base no tipo do item e suas especificações no Swagger
                val response = if (ehObjeto) {
                    // 🎯 SOLUÇÃO COMPATÍVEL COM SWAGGER MULTIPART:
                    // Converte a String para um RequestBody text/plain puro para ser consumido como @Part pelo Retrofit
                    val statusPart = statusDefinido.toRequestBody("text/plain".toMediaType())
                    RetrofitClient.authApiParaServico.atualizarStatusObjeto(authHeader, idItem, statusPart)
                } else {
                    // Mantém a rota de Serviços enviando ServiceUpdateRequest (JSON padrão)
                    val corpoServico = ServiceUpdateRequest(status = statusDefinido)
                    RetrofitClient.authApiParaServico.atualizarStatusServico(authHeader, idItem, corpoServico)
                }

                if (response.isSuccessful) {
                    // 1. Atualiza o estado da lista reativa local de forma atômica
                    listaMeusItens = listaMeusItens.map { item ->
                        if (item.id == idItem) item.copy(status = statusDefinido) else item
                    }
                    Log.d("MURAL_INTEGRACAO", "Status alterado com sucesso na API para: $statusDefinido")

                    // 2. DISPARA O POST AUTOMÁTICO PARA O MURAL DO CONDOMÍNIO
                    publicarAvisoNoMural(
                        authHeader = authHeader,
                        titulo = "📢 Novo Item no Mural: ${itemAlvo.title}",
                        descricao = "O morador disponibilizou este item para a comunidade!\n\nDescrição: ${itemAlvo.description}\nStatus: $statusDefinido"
                    )

                } else {
                    Log.e("MURAL_INTEGRACAO", "Falha HTTP ao mudar status: ${response.code()} | ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("MURAL_INTEGRACAO", "Erro crítico na transição de status para o mural", e)
            }
        }
    }

    /**
     * Função auxiliar privada que faz a inserção do aviso diretamente no Mural do Condomínio
     */
    private suspend fun publicarAvisoNoMural(authHeader: String, titulo: String, descricao: String) {
        try {
            Log.d("MURAL_INTEGRACAO", "Enviando publicação automática para o Feed do Mural...")

            // Descomente abaixo para linkar com o feed do condomínio quando necessário:
            // val requestMural = com.example.mobilevizinhaa.ui.theme.data.CreatePostRequest(
            //     title = titulo,
            //     description = descricao,
            //     photoBase64 = ""
            // )
            // RetrofitClient.authApiParaServico.criarPublicacao(authHeader, requestMural)

        } catch (e: Exception) {
            Log.e("MURAL_INTEGRACAO", "Não foi possível gerar a publicação visual no feed do mural", e)
        }
    }

    /**
     * Deleta permanentemente o item selecionado e limpa o estado reativo local.
     * 🎯 AJUSTADO: Agora discrimina se deve excluir via rota de Objeto ou de Serviço.
     */
    fun excluirItemDoUsuario(token: String, idItem: Int, ehObjeto: Boolean) {
        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // Executa a deleção chamando o endpoint correspondente correto mapeado na interface
                val response = if (ehObjeto) {
                    RetrofitClient.authApiParaServico.deletarObjeto(authHeader, idItem)
                } else {
                    RetrofitClient.authApiParaServico.deletarServico(authHeader, idItem)
                }

                if (response.isSuccessful) {
                    listaMeusItens = listaMeusItens.filter { item -> item.id != idItem }
                    Log.d("INTEGRACAO_VM", "Item $idItem de tipo ${if (ehObjeto) "OBJETO" else "SERVIÇO"} removido permanentemente da API.")
                } else {
                    Log.e("INTEGRACAO_VM", "Falha ao deletar item na API. Código do erro: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("INTEGRACAO_VM", "Erro de rede inesperado ao deletar o item: $idItem", e)
            }
        }
    }

    fun limparDados() {
        listaMeusItens = emptyList()
        errorMessage = null
        Log.d("PEDIDOS_OBJETOS_VM", "Estado reativo reiniciado com sucesso.")
    }
}