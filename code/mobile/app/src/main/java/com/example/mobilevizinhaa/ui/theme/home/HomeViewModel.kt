package com.example.mobilevizinhaa.ui.theme.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val userName: String = "Sarah!",
    val apartment: String = "Apartamento 100",
    val pedidosCount: Int = 3,
    val objetosCount: Int = 3
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
}