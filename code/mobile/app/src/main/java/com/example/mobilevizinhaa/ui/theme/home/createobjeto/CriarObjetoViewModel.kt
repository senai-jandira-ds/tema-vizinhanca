package com.example.mobilevizinhaa.ui.theme.home.createobjeto

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
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
        fotoBytes: ByteArray?, // 🎯 ALTERADO: Agora recebe os bytes brutos da imagem compactada
        diasDisponiveis: Int,
        categoryId: Int
    ) {
        // Validação de segurança básica antes de iniciar o processo
        if (titulo.isBlank() || descricao.isBlank()) {
            _uiState.value = CriarObjetoUiState(errorMessage = "Preencha todos os campos obrigatórios.")
            return
        }
        if (fotoBytes == null || fotoBytes.isEmpty()) {
            _uiState.value = CriarObjetoUiState(errorMessage = "Selecione uma foto para o objeto.")
            return
        }

        viewModelScope.launch {
            _uiState.value = CriarObjetoUiState(isLoading = true)
            try {
                // 1. Calcula a data limite (deadline) somando os dias escolhidos na Screen
                val dataDeadline = LocalDate.now().plusDays(diasDisponiveis.toLong()).toString()

                // 2. Envelopa os parâmetros textuais em MultipartBody.Part mapeando as chaves exatas do Spring
                val titlePart = MultipartBody.Part.createFormData("title", titulo.trim())
                val descriptionPart = MultipartBody.Part.createFormData("description", descricao.trim())
                val deadlinePart = MultipartBody.Part.createFormData("deadline", dataDeadline)
                val statusPart = MultipartBody.Part.createFormData("status", "DISPONIVEL")
                val categoryIdPart = MultipartBody.Part.createFormData("categoryId", categoryId.toString())

                // 🎯 REVOLUÇÃO DO COMPILADOR:
                // Transforma o array de bytes em um RequestBody binário do tipo MultipartFile real,
                // simulando um arquivo físico chamado "objeto_foto.jpg" para satisfazer as restrições do backend.
                val requestFile = fotoBytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, fotoBytes.size)
                val photoPart = MultipartBody.Part.createFormData("photo", "objeto_foto.jpg", requestFile)

                // 3. Dispara a requisição para o servidor passando o Token Bearer + as Partes Multipart
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authApiService.criarObjeto(
                    token = authHeader,
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
                    // Extrai com segurança a mensagem interna de validação se houver falha (ex: HTTP 400 ou 422)
                    val erroTexto = response.errorBody()?.string()
                    var msgErro = response.body()?.message

                    if (msgErro == null && !erroTexto.isNullOrEmpty()) {
                        try {
                            val jsonErro = JSONObject(erroTexto)
                            msgErro = jsonErro.optString("message", "Erro na validação dos campos.")
                        } catch (e: Exception) {
                            msgErro = "Erro ao processar dados no servidor."
                        }
                    }

                    Log.e("API_OBJETO", "Falha no cadastro do objeto: $erroTexto")
                    _uiState.value = CriarObjetoUiState(errorMessage = msgErro ?: "Erro desconhecido ao cadastrar objeto.")
                }
            } catch (e: Exception) {
                Log.e("API_OBJETO_CRITICAL", "Falha de infraestrutura/conexão física: ${e.message}")
                _uiState.value = CriarObjetoUiState(errorMessage = "Falha na conexão: Não foi possível alcançar o servidor.")
            }
        }
    }

    fun limparEstado() {
        _uiState.value = CriarObjetoUiState()
    }
}