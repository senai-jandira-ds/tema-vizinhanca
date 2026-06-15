package com.example.mobilevizinhaa.ui.theme.domain



object Validator {
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$".toRegex()
    private val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$".toRegex()

    fun isEmailValid(email: String): Boolean {
        return email.trim().matches(emailRegex)
    }

    fun isPasswordValid(password: String): Boolean {
        return password.matches(passwordRegex)
    }
}