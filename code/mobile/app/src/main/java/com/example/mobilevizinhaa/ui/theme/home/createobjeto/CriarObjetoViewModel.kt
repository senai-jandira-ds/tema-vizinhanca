package com.example.mobilevizinhaa.ui.theme.home.createobjeto

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.time.LocalDate

data class CriarObjetoUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CriarObjetoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CriarObjetoUiState())
    val uiState: StateFlow<CriarObjetoUiState> = _uiState

    @RequiresApi(Build.VERSION_CODES.O)
    fun cadastrarObjetoNoCondominio(
        token: String,
        titulo: String,
        descricao: String,
        fotoBase64: String,
        diasDisponiveis: Int,
        categoryId: Int
    ) {
        // Validação de segurança básica antes de iniciar o processo
        if (titulo.isBlank() || descricao.isBlank()) {
            _uiState.value = CriarObjetoUiState(errorMessage = "Preencha todos os campos obrigatórios.")
            return
        }

        viewModelScope.launch {
            _uiState.value = CriarObjetoUiState(isLoading = true)
            try {
                // 1. Calcula a data limite (deadline) somando os dias escolhidos na Screen
                val dataDeadline = LocalDate.now().plusDays(diasDisponiveis.toLong()).toString()

                // 2. Envelopa os parâmetros em MultipartBody.Part mapeando as chaves exatas do Spring
                val titlePart = MultipartBody.Part.createFormData("title", titulo)
                val descriptionPart = MultipartBody.Part.createFormData("description", descricao)

                // 🎯 CORRIGIDO: Chave alterada para "photo" para sanar o erro de validação
                val photoPart = MultipartBody.Part.createFormData("photo", fotoBase64)

                val deadlinePart = MultipartBody.Part.createFormData("deadline", dataDeadline)

                // 🎯 ADICIONADO: Campo obrigatório exigido pelo backend enviado como "DISPONIVEL"
                val statusPart = MultipartBody.Part.createFormData("status", "DISPONIVEL")

                val categoryIdPart = MultipartBody.Part.createFormData("categoryId", categoryId.toString())

                // 3. Dispara a requisição para o servidor passando o Token Bearer + as Partes Multipart
                val response = RetrofitClient.authApiService.criarObjeto(
                    token = "Bearer $token",
                    title = titlePart,
                    description = descriptionPart,
                    photo = photoPart,
                    deadline = deadlinePart,
                    status = statusPart,
                    categoryId = categoryIdPart
                )

                // 4. Analisa o retorno lógico do backend
                if (response.isSuccessful && response.body()?.status == true) {
                    _uiState.value = CriarObjetoUiState(isSuccess = true)
                } else {
                    // Captura as mensagens de validação detalhadas enviadas pela API (ex: erro 400)
                    val msgErro = response.body()?.message ?: "Erro ao cadastrar objeto no servidor."
                    _uiState.value = CriarObjetoUiState(errorMessage = msgErro)
                }
            } catch (e: Exception) {
                // Trata erros de infraestrutura ou rede (Sem internet, queda do servidor Render, etc.)
                _uiState.value = CriarObjetoUiState(errorMessage = "Falha na conexão: ${e.localizedMessage}")
            }
        }
    }

    fun limparEstado() {
        _uiState.value = CriarObjetoUiState()
    }
}