package com.example.mobilevizinhaa.ui.theme.menssage

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobilevizinhaa.ui.theme.data.AuthApiService
import com.example.mobilevizinhaa.ui.theme.data.ConversationSummaryResponse
import com.example.mobilevizinhaa.ui.theme.data.MessageResponse
import com.example.mobilevizinhaa.ui.theme.data.ResidentDetail
import com.example.mobilevizinhaa.ui.theme.data.ConversationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 1. Modelos reais mapeados para as telas do Compose
data class Message(
    val id: Long,
    val text: String,
    val time: String,
    val isFromMe: Boolean,
    val senderName: String,
    val senderPhoto: String?
)

data class ChatConversation(
    val id: Long,
    val name: String,
    val lastMessage: String,
    val time: String,
    val profileImageUrl: String?,
    val targetResidentId: Long
)

data class MessagesUiState(
    val conversations: List<ChatConversation> = emptyList(),
    val currentChatMessages: List<Message> = emptyList(),
    val residents: List<ResidentDetail> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MessagesViewModel(
    private val apiService: AuthApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    var activeChatId by mutableStateOf<Long?>(null)
        private set

    // --- 🎯 CARREGAR TODOS OS VIZINHOS ---
    fun carregarVizinhos(token: String, idUsuarioLogado: Int) {
        Log.d("CHATS_DEBUG", "carregarVizinhos() chamado. ID Logado: $idUsuarioLogado")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = apiService.obterTodosMoradoresPaginados(token = "Bearer $token")
                if (response.isSuccessful && response.body()?.status == true) {
                    val listaCompleta = response.body()?.responseData?.content ?: emptyList()
                    val listaFiltrada = listaCompleta.filter { it.id != idUsuarioLogado }

                    Log.d("CHATS_DEBUG", "carregarVizinhos() SUCESSO. Encontrados: ${listaCompleta.size} moradores. Filtrados para exibição: ${listaFiltrada.size}")
                    _uiState.value = _uiState.value.copy(residents = listaFiltrada, isLoading = false)
                } else {
                    val erroCorpo = response.errorBody()?.string()
                    Log.e("CHATS_DEBUG", "carregarVizinhos() ERRO API. Código: ${response.code()} | Corpo: $erroCorpo")
                    _uiState.value = _uiState.value.copy(errorMessage = "Erro ao buscar moradores.", isLoading = false)
                }
            } catch (e: Exception) {
                Log.e("CHATS_DEBUG", "carregarVizinhos() EXCEPTION: ", e)
                _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage, isLoading = false)
            }
        }
    }

    // --- 🎯 CARREGAR ABAS DE CHATS JÁ ATIVOS ---
    fun carregarConversasAtivas(token: String, idUsuarioLogado: Int) {
        Log.d("CHATS_DEBUG", "carregarConversasAtivas() acionado. Token Enviado: Bearer ${token.take(15)}... | Meu ID: $idUsuarioLogado")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = apiService.listarMinhasConversas(token = "Bearer $token")
                if (response.isSuccessful && response.body()?.status == true) {
                    val apiConversations = response.body()?.response ?: emptyList()
                    Log.d("CHATS_DEBUG", "carregarConversasAtivas() API Retornou ${apiConversations.size} chats brutos do banco.")

                    val conversasMapeadas = apiConversations.map { conv ->
                        val vizinho = conv.participants?.firstOrNull { it.residentId != idUsuarioLogado.toLong() }
                        Log.d("CHATS_DEBUG", "Mapeando Chat ID: ${conv.id} -> Outro participante ID: ${vizinho?.residentId} Nome: ${vizinho?.residentName}")

                        ChatConversation(
                            id = conv.id,
                            name = vizinho?.residentName ?: "Vizinho do Condomínio",
                            lastMessage = "Clique para abrir a conversa",
                            time = formatarDataSwagger(conv.createdDate),
                            profileImageUrl = vizinho?.residentPhoto,
                            targetResidentId = vizinho?.residentId ?: 0L
                        )
                    }

                    _uiState.value = _uiState.value.copy(conversations = conversasMapeadas, isLoading = false)
                } else {
                    val erroCorpo = response.errorBody()?.string()
                    Log.e("CHATS_DEBUG", "carregarConversasAtivas() REJEITADO. Código HTTP: ${response.code()} | Detalhe: $erroCorpo")
                    _uiState.value = _uiState.value.copy(errorMessage = "Falha ao carregar chats.", isLoading = false)
                }
            } catch (e: Exception) {
                Log.e("CHATS_DEBUG", "carregarConversasAtivas() CATCH EXCEPTION: ", e)
                _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage, isLoading = false)
            }
        }
    }

    // --- 🎯 ABRIR CHAT E CARREGAR HISTÓRICO ---
    fun abrirConversaEspecifica(token: String, idConversa: Long, idUsuarioLogado: Int) {
        Log.d("CHATS_DEBUG", "abrirConversaEspecifica() -> Carregando histórico da conversa ID: $idConversa")
        activeChatId = idConversa
        viewModelScope.launch {
            try {
                val response = apiService.obterMensagensDaConversa(token = "Bearer $token", idConversa = idConversa)
                if (response.isSuccessful && response.body()?.status == true) {
                    val apiMessages = response.body()?.response ?: emptyList()
                    Log.d("CHATS_DEBUG", "abrirConversaEspecifica() -> ${apiMessages.size} mensagens recuperadas com sucesso.")

                    val mensagensMapeadas = apiMessages.map { msg ->
                        Message(
                            id = msg.id,
                            text = msg.text ?: "",
                            time = formatarDataSwagger(msg.createdDate),
                            isFromMe = msg.residentId == idUsuarioLogado.toLong(),
                            senderName = msg.residentName ?: "Morador",
                            senderPhoto = msg.residentPhoto
                        )
                    }
                    _uiState.value = _uiState.value.copy(currentChatMessages = mensagensMapeadas)
                } else {
                    Log.e("CHATS_DEBUG", "abrirConversaEspecifica() FALHOU -> Código: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CHATS_DEBUG", "abrirConversaEspecifica() EXCEPTION silenciosa: ", e)
            }
        }
    }

    // --- 🎯 INICIAR NOVO CHAT ---
    fun criarOuAbrirChatComVizinho(token: String, targetResidentId: Long, idUsuarioLogado: Int, onSucesso: (Long) -> Unit) {
        Log.d("CHATS_DEBUG", "criarOuAbrirChatComVizinho() disparado para o morador ID: $targetResidentId")
        viewModelScope.launch {
            try {
                val request = ConversationRequest(targetResidentId = targetResidentId)
                val response = apiService.criarConversa(token = "Bearer $token", request = request)
                if (response.isSuccessful && response.body()?.status == true) {
                    val idNovaConversa = response.body()?.response?.id ?: return@launch
                    Log.d("CHATS_DEBUG", "criarOuAbrirChatComVizinho() SUCESSO! Conversa ID gerada: $idNovaConversa")
                    carregarConversasAtivas(token, idUsuarioLogado)
                    onSucesso(idNovaConversa)
                } else {
                    Log.e("CHATS_DEBUG", "criarOuAbrirChatComVizinho() FALHOU. Erro do Servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CHATS_DEBUG", "criarOuAbrirChatComVizinho() EXCEPTION: ", e)
            }
        }
    }

    // --- 🎯 ENVIAR MENSAGEM ---
    fun sendMessage(chatId: Long, text: String) {
        if (text.trim().isBlank()) return
        Log.d("CHATS_DEBUG", "sendMessage() simulando envio visual local no chat ID: $chatId | Conteúdo: $text")

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val localNewMessage = Message(
            id = System.currentTimeMillis(),
            text = text,
            time = currentTime,
            isFromMe = true,
            senderName = "Eu",
            senderPhoto = null
        )

        _uiState.value = _uiState.value.copy(
            currentChatMessages = _uiState.value.currentChatMessages + localNewMessage,
            conversations = _uiState.value.conversations.map { chat ->
                if (chat.id == chatId) {
                    chat.copy(lastMessage = text, time = currentTime)
                } else chat
            }
        )
    }

    private fun formatarDataSwagger(dataRaw: String?): String {
        if (dataRaw.isNullOrBlank()) return "--:--"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dataRaw)
            if (date != null) outputFormat.format(date) else "--:--"
        } catch (e: Exception) {
            dataRaw.take(5)
        }
    }

    fun fecharChatAtivo() {
        Log.d("CHATS_DEBUG", "fecharChatAtivo() limpo.")
        activeChatId = null
        _uiState.value = _uiState.value.copy(currentChatMessages = emptyList())
    }

    companion object {
        fun provideFactory(apiService: AuthApiService): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MessagesViewModel::class.java)) {
                    return MessagesViewModel(apiService) as T
                }
                throw IllegalArgumentException("ViewModel desconhecido")
            }
        }
    }
}