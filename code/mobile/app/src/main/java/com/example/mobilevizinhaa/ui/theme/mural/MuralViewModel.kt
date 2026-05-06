package com.example.mobilevizinhaa.ui.theme.mural

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.mobilevizinhaa.R

// Modelo de dados
data class PostMural(
    val autor: String,
    val horario: String,
    val mensagem: String,
    val imagemPostRes: Int?,
    val fotoPerfilRes: Int
)

// Estado da UI
data class MuralUiState(
    val posts: List<PostMural> = emptyList(),
    val isLoading: Boolean = false
)

class MuralViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MuralUiState())
    val uiState: StateFlow<MuralUiState> = _uiState.asStateFlow()

    init {
        carregarPosts()
    }

    private fun carregarPosts() {
        // Simulando carga de dados
        val dados = listOf(
            PostMural(
                autor = "Wellington",
                horario = "Às 16:35",
                mensagem = "Gente, alguém mais já passou por isso? 😞 Tô com um vazamento embaixo da pia...",
                imagemPostRes = R.drawable.vazamento, // Troque pelo seu drawable
                fotoPerfilRes = R.drawable.wellington
            ),
            PostMural(
                autor = "Rosana",
                horario = "Às 16:35",
                mensagem = "O disjuntor aqui de casa não para de desarmar... toda hora cai a energia...",
                imagemPostRes = R.drawable.djuntor, // Troque pelo seu drawable
                fotoPerfilRes = R.drawable.mulher
            )
        )
        _uiState.value = MuralUiState(posts = dados)
    }
}