package com.example.mobilevizinhaa.ui.theme.rank


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.mobilevizinhaa.R

data class UsuarioRanking(
    val posicao: Int,
    val nome: String,
    val pontuacao: Int,
    val fotoRes: Int
)

data class RankingUiState(
    val topDez: List<UsuarioRanking> = emptyList(),
    val usuarioLogado: UsuarioRanking? = null,
    val filtroSelecionado: String = "Hoje"
)

class RankingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    init {
        carregarRanking()
    }

    fun onFiltroChanged(novoFiltro: String) {
        _uiState.update { it.copy(filtroSelecionado = novoFiltro) }
        carregarRanking()
    }

    private fun carregarRanking() {
        val lista = listOf(
            UsuarioRanking(1, "Wellington", 80, R.drawable.wellington),
            UsuarioRanking(2, "Rosana", 55, R.drawable.mulher),
            UsuarioRanking(3, "Ricardo", 50, R.drawable.wellington),
            UsuarioRanking(4, "Beatriz", 45, R.drawable.mulher),
            UsuarioRanking(5, "André", 40, R.drawable.wellington),
            UsuarioRanking(6, "João", 38, R.drawable.wellington),
            UsuarioRanking(7, "Maria", 36, R.drawable.mulher),
            UsuarioRanking(8, "Carlos", 34, R.drawable.wellington),
            UsuarioRanking(9, "Ana", 33, R.drawable.mulher),
            UsuarioRanking(10, "Sarah", 32, R.drawable.mulher)
        )

        _uiState.update {
            it.copy(
                topDez = lista.take(10),
                usuarioLogado = lista.find { user -> user.nome == "Sarah" }
            )
        }
    }
}