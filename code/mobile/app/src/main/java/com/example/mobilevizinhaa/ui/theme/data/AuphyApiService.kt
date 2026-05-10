package com.example.mobilevizinhaa.ui.theme.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

// --- MODELOS DE ENVIO (REQUEST) ---
data class LoginRequest(
    val email: String,
    val password: String
)

// --- MODELOS DE RESPOSTA (RESPONSE) DO LOGIN ---
// Estrutura exata do JSON que veio no seu Logcat
data class LoginResponse(
    val status: Boolean,
    val status_code: Int,
    val message: String,
    val response: ResponseData // Objeto que contém os dados reais
)

data class ResponseData(
    val token: String,
    val user: UserData
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val apto: String? = null,
    val block: String? = null,
    val cpf: String? = null,
    val phone: String? = null
)

// --- MODELO PARA LISTAGEM DE MORADORES ---
data class ResidentResponse(
    val id: Long,
    val name: String,
    val email: String,
    val apartment: String?,
    val block: String?,
    val score: Int?
)

// --- INTERFACE DA API ---
interface AuthApiService {

    @POST("api/v1/auth/login/resident")
    suspend fun loginResident(@Body request: LoginRequest): Response<LoginResponse>

    // Endpoint para pegar a lista de moradores (Exige o Token JWT)
    @GET("api/v1/resident")
    suspend fun listResidents(
        @Header("Authorization") token: String
    ): Response<List<ResidentResponse>>
}