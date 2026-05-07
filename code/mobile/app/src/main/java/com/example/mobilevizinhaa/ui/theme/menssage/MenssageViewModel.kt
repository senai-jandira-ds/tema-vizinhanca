package com.example.mobilevizinhaa.ui.theme.menssage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.mobilevizinhaa.R

// 1. Definição do Modelo de Dados (Pode ficar em um arquivo separado ou aqui)
data class ChatConversation(
    val id: Int,
    val name: String,
    val lastMessage: String,
    val time: String,
    val profileImage: Int,
    val unreadCount: Int = 0 // Adicionei para ficar mais completo
)

data class MessagesUiState(
    val conversations: List<ChatConversation> = emptyList(),
    val isLoading: Boolean = false
)

class MessagesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MessagesUiState())

    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val list = listOf(
            ChatConversation(1, "Fernanda", "Ok, Combinado, passo ai mais tarde", "14:30", R.drawable.mulher, unreadCount = 2),
            ChatConversation(2, "Sofia", "Ok, Combinado, passo ai mais tarde", "14:30", R.drawable.mulher),
            ChatConversation(3, "Bruno", "Ok, Combinado, passo ai mais tarde", "14:30", R.drawable.wellington),
            ChatConversation(4, "Sarah", "Obrigada pelo serviço", "ontem", R.drawable.mulher),
            ChatConversation(5, "Marcos", "Sim, sou eletricista", "25/03/2026", R.drawable.wellington),
            ChatConversation(6, "tobias", "Sim, sou eletricista", "25/03/2026", R.drawable.wellington),
            ChatConversation(7, "amanda", "Sim, sou eletricista", "25/03/2026", R.drawable.wellington),
            ChatConversation(8, "carla", "Sim, sou eletricista", "25/03/2026", R.drawable.wellington),
            ChatConversation(9, "marcelo", "Sim, sou eletricista", "25/03/2026", R.drawable.wellington),
            ChatConversation(10, "scott", "Sim, sou eletricista", "25/03/2026", R.drawable.wellington)
        )
        _uiState.value = MessagesUiState(
            conversations = list,
            isLoading = false
        )
    }
    fun onRefresh() {
        loadMessages()
    }
}