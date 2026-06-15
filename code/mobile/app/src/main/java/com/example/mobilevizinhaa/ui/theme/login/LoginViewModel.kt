package com.example.mobilevizinhaa.ui.theme.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$".toRegex()

    // --- FUNÇÃO DE FILTRO PARA MENSAGENS DE SENHA ---
    private fun filtrarErroSenha(senha: String): String? {
        return when {
            senha.isEmpty() -> "A senha não pode estar vazia"
            senha.length < 8 -> "Mínimo de 8 caracteres"
            !senha.any { it.isUpperCase() } -> "Adicione pelo menos uma letra maiúscula"
            !senha.any { it.isLowerCase() } -> "Adicione pelo menos uma letra minúscula"
            !senha.any { it.isDigit() } -> "Adicione pelo menos um número"
            !senha.any { "@#$%^&+=!".contains(it) } -> "Adicione um caractere especial (@#$%^&+=!)"
            else -> null
        }
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, emailError = null, errorMessage = null) }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, passwordError = null, errorMessage = null) }
    }

    fun onLoginClicked(onSuccess: (String, String) -> Unit) {
        val currentState = _uiState.value

        val emailValidado = currentState.email.trim()
        val senhaParaValidar = currentState.password

        val isEmailOk = emailValidado.matches(emailRegex)

        // Chamamos o filtro para obter a mensagem específica
        val mensagemErroSenha = filtrarErroSenha(senhaParaValidar)
        val isPasswordOk = mensagemErroSenha == null

        if (isEmailOk && isPasswordOk) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            onSuccess(emailValidado, senhaParaValidar)
        } else {
            _uiState.update {
                it.copy(
                    emailError = if (isEmailOk) null else "E-mail inválido",
                    passwordError = mensagemErroSenha,
                    errorMessage = "Verifique os dados para prosseguir"
                )
            }
        }
    }
}