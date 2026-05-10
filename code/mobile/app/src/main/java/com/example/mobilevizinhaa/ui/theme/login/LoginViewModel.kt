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
import android.util.Log // Importante para debug

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val token: String? = null,
    val userName: String? = null // Adicionei para você mostrar o nome do Breno na Home
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

    fun onLoginClicked(onSuccess: () -> Unit) {
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

                        // AJUSTE CRÍTICO: Pegando o token de dentro do objet 'response' da API
                        val tokenRecebido = loginBody.response.token
                        val nomeUsuario = loginBody.response.user.name

                        Log.d("LOGIN_DEBUG", "Sucesso! Token: $tokenRecebido")

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                token = tokenRecebido,
                                userName = nomeUsuario
                            )
                        }

                        onSuccess() // Navega para a próxima tela
                    } else {
                        val erroApi = when (response.code()) {
                            404 -> "Caminho não encontrado (404)"
                            401 -> "E-mail ou senha incorretos"
                            else -> "Erro ${response.code()}: ${response.message()}"
                        }
                        _uiState.update { it.copy(isLoading = false, errorMessage = erroApi) }
                    }
                } catch (e: Exception) {
                    Log.e("LOGIN_ERROR", "Falha na conexão", e)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Falha de conexão: Verifique a internet")
                    }
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    emailError = if (isEmailOk) null else "E-mail inválido",
                    passwordError = mensagemErroSenha,
                    errorMessage = "Corrija os erros para continuar"
                )
            }
        }
    }
}