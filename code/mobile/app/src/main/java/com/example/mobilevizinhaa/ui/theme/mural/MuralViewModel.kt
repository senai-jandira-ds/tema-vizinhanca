package com.example.mobilevizinhaa.ui.theme.mural

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.mobilevizinhaa.ui.theme.data.CategoryDetail
import com.example.mobilevizinhaa.ui.theme.data.ResidentDetail
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetail

// Estado da UI atualizado para usar o modelo oficial do banco de dados (API)
data class MuralUiState(
    val posts: List<ServiceDetail> = emptyList(),
    val isLoading: Boolean = false
)

class MuralViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MuralUiState())
    val uiState: StateFlow<MuralUiState> = _uiState.asStateFlow()

    init {
        carregarPosts()
    }

    private fun carregarPosts() {
        _uiState.value = MuralUiState(isLoading = true)

        // Simulando carga de dados usando a estrutura de objetos da API do Leonardo Scotti
        val dadosDoBackend = listOf(
            ServiceDetail(
                id = 1,
                title = "Vazamento embaixo da pia",
                description = "Gente, alguém mais já passou por isso? 😞 Tô com um vazamento embaixo da pia da cozinha que está molhando o armário todo. Preciso de ajuda urgente com encanamento.",
                estimatedTime = 2,
                urgency = "HIGH", // Mantido em inglês para conversar com o banco
                status = "ACTIVE",
                photo = "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?q=80&w=500", // URL de teste ou Base64
                resident = ResidentDetail(
                    id = 10,
                    name = "Wellington",
                    email = "wellington@email.com",
                    apartment = "102",
                    phone = "11999999999",
                    cpf = null,
                    score = 0,
                    creationDate = null,
                    block = null
                ),
                category = CategoryDetail(
                    id = 1,
                    name = "Reparos",
                    description = "Serviços hidráulicos e gerais",
                    typeCategory = "MANUTENCAO"
                )
            ),
            ServiceDetail(
                id = 2,
                title = "Disjuntor desarmando",
                description = "O disjuntor aqui de casa não para de desarmar... toda hora cai a energia quando ligo o chuveiro. Alguém conhece um eletricista?",
                estimatedTime = 1,
                urgency = "MEDIUM",
                status = "ACTIVE",
                photo = "https://images.unsplash.com/photo-1621905252507-b354bc25edac?q=80&w=500", // URL de teste ou Base64
                resident = ResidentDetail(
                    id = 11,
                    name = "Rosana",
                    email = "rosana@email.com",
                    apartment = "45B",
                    phone = "11988888888",
                    cpf = null,
                    score = 0,
                    creationDate = null,
                    block = null
                ),
                category = CategoryDetail(
                    id = 2,
                    name = "Elétrica",
                    description = "Reparos elétricos residenciais",
                    typeCategory = "MANUTENCAO"
                )
            )
        )

        // Atualiza o estado da UI com os dados formatados do backend
        _uiState.value = MuralUiState(
            posts = dadosDoBackend,
            isLoading = false
        )
    }
}