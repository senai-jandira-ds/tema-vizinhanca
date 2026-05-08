package com.example.mobilevizinhaa.ui.theme.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

// Modelo de envio para o Login
data class LoginRequest(
    val email: String,
    val password: String
)

// Modelo de resposta do Login (recebe o Token)
data class LoginResponse(
    val token: String? = null
)

// Modelo de como os moradores chegam do banco (baseado no seu .http)
data class ResidentResponse(
    val id: Int,
    val name: String,
    val email: String,
    val apartment: String,
    val block: String,
    val score: Int
)

interface AuthApiService {
    // Endpoint de Login de Residente
    @POST("api/v1/auth/login/resident")
    suspend fun loginResident(@Body request: LoginRequest): Response<LoginResponse>

    // Endpoint para pegar a lista de moradores (precisa do token no Header)
    @GET("api/v1/resident")
    suspend fun listResidents(@Header("Authorization") token: String): Response<List<ResidentResponse>>
}