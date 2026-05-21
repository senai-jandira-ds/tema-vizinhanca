package com.example.mobilevizinhaa.ui.theme.menssage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.mobilevizinhaa.R

// 1. Modelo para cada balão de mensagem
// No arquivo MessagesViewModel.kt
data class Message(
    val id: Int,
    val text: String = "",
    val time: String,
    val isFromMe: Boolean,
    val imageUri: String? = null // <--- Novo campo para a foto
)


// 2. Modelo da conversa (Upgrade: agora contém a lista de mensagens)
data class ChatConversation(
    val id: Int,
    val name: String,
    val lastMessage: String,
    val time: String,
    val profileImage: Int,
    val unreadCount: Int = 0,
    val messages: List<Message> = emptyList() // <--- Lista real de mensagens
)

data class MessagesUiState(
    val conversations: List<ChatConversation> = emptyList(),
    val isLoading: Boolean = false
)

class MessagesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    init { loadMessages() }

    private fun loadMessages() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val list = listOf(
            ChatConversation(
                id = 1,
                name = "Fernanda",
                lastMessage = "Ok, Combinado, passo ai mais tarde",
                time = "14:30",
                profileImage = R.drawable.mulher,
                unreadCount = 2,
                messages = listOf(
                    Message(1, "Oi Fernanda, tudo certo para hoje?", "14:20", true),
                    Message(2, "Ok, Combinado, passo ai mais tarde", "14:30", false)
                )
            ),
            ChatConversation(
                id = 4,
                name = "Sarah",
                lastMessage = "Obrigada pelo serviço",
                time = "ontem",
                profileImage = R.drawable.mulher,
                messages = listOf(
                    Message(1, "O serviço de limpeza foi finalizado!", "17:00", true),
                    Message(2, "Obrigada pelo serviço", "17:05", false)
                )
            ),
            ChatConversation(
                id = 10,
                name = "Scott",
                lastMessage = "Sim, sou eletricista",
                time = "25/03/2026",
                profileImage = R.drawable.wellington,
                messages = listOf(
                    Message(1, "Você faz instalação de chuveiro?", "09:00", true),
                    Message(2, "Sim, sou eletricista", "09:15", false)
                )
            )
            // Adicione os outros contatos aqui seguindo o mesmo padrão...
        )

        _uiState.value = MessagesUiState(conversations = list, isLoading = false)
    }
    fun sendMessage(chatId: Int, text: String) {
        if (text.isBlank()) return // Não envia se estiver vazio

        val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())

        val newMessage = Message(
            id = (System.currentTimeMillis() / 1000).toInt(), // ID único temporário
            text = text,
            time = currentTime,
            isFromMe = true
        )

        _uiState.value = _uiState.value.copy(
            conversations = _uiState.value.conversations.map { chat ->
                if (chat.id == chatId) {
                    chat.copy(
                        messages = chat.messages + newMessage,
                        lastMessage = text,
                        time = currentTime
                    )
                } else chat
            }
        )
    }

    fun sendMessage(chatId: Int, text: String = "", imageUri: String? = null) {
        // ESTA LINHA ABAIXO É A QUE FALTA NO SEU CÓDIGO:
        val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())

        val newMessage = Message(
            id = (System.currentTimeMillis() / 1000).toInt(),
            text = text,
            time = currentTime, // Agora o erro aqui vai sumir!
            isFromMe = true,
            imageUri = imageUri
        )

        // Atualiza o estado da lista de conversas
        _uiState.value = _uiState.value.copy(
            conversations = _uiState.value.conversations.map { chat ->
                if (chat.id == chatId) {
                    chat.copy(
                        messages = chat.messages + newMessage,
                        lastMessage = if (imageUri != null) "📷 Foto" else text,
                        time = currentTime
                    )
                } else chat
            }
        )
    }
}