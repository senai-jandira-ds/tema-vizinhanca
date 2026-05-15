package com.example.mobilevizinhaa.ui.theme.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.mobilevizinhaa.ui.theme.data.LoginRequest
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.example.mobilevizinhaa.ui.theme.data.LoginResponse // Certifique-se de importar
import android.util.Log

/**
 * Estado da UI de Login ajustado para suportar a injeção de dados na Home.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    // ADICIONADO: Campo para armazenar a resposta completa da API
    val loginResponse: LoginResponse? = null
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$".toRegex()

    private fun filtrarErroSenha(senha: String): String? {
        return when {
            senha.isEmpty() -> "A senha não pode estar vazia"
            senha.length < 8 -> "Mínimo de 8 caracteres"
            !senha.any { it.isUpperCase() } -> "Adicione pelo menos uma letra maiúscula"
            !senha.any { it.isLowerCase() } -> "Adicione pelo menos uma letra minúscula"
            !senha.any { it.isDigit() } -> "Adicione pelo menos um número"
            !senha.contains("[!@#$%^&*(),.?\":{}|<>]".toRegex()) -> "Adicione um caractere especial (!@#$%)"
            else -> null
        }
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, emailError = null, errorMessage = null) }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, passwordError = null, errorMessage = null) }
    }

    /**
     * Tenta realizar o login e armazena a resposta completa no estado.
     */
    fun onLoginClicked(onSuccess: (String) -> Unit) {
        val currentState = _uiState.value
        val emailValidado = currentState.email.trim()
        val senhaParaValidar = currentState.password

        val isEmailOk = emailValidado.matches(emailRegex)
        val mensagemErroSenha = filtrarErroSenha(senhaParaValidar)
        val isPasswordOk = mensagemErroSenha == null

        if (isEmailOk && isPasswordOk) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            viewModelScope.launch {
                try {
                    val request = LoginRequest(email = emailValidado, password = senhaParaValidar)
                    val response = RetrofitClient.authApi.loginResident(request)

                    if (response.isSuccessful && response.body() != null) {
                        val loginBody = response.body()!!

                        // ATUALIZAÇÃO IMPORTANTE:
                        // Salvamos o 'loginBody' inteiro para a LoginScreen poder ler
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loginResponse = loginBody // Isso resolve o erro do print!
                            )
                        }

                        val token = loginBody.response.token
                        val userId = loginBody.response.user.id

                        val user = loginBody.response.user


                        Log.d("LOGIN_DEBUG", "Sucesso! Usuário: ${loginBody.response.user.name}")

                        // MANDA A ROTA COMPLETA
                        onSuccess("home/$token/$userId")

                    } else {
                        val erroApi = when (response.code()) {
                            401 -> "E-mail ou senha incorretos"
                            404 -> "Usuário não encontrado"
                            else -> "Erro ${response.code()}: Verifique suas credenciais"
                        }
                        _uiState.update { it.copy(isLoading = false, errorMessage = erroApi) }
                    }
                } catch (e: Exception) {
                    Log.e("LOGIN_ERROR", "Falha na conexão", e)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Servidor indisponível.")
                    }
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    emailError = if (isEmailOk) null else "E-mail inválido",
                    passwordError = mensagemErroSenha,
                    errorMessage = "Corrija os erros acima"
                )
            }
        }
    }
}