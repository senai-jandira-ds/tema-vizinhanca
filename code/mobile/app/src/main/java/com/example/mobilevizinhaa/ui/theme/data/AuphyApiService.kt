    package com.example.mobilevizinhaa.ui.theme.data

    import com.google.gson.annotations.SerializedName
    import retrofit2.Response
    import retrofit2.http.*

    // ==========================================
    // 1. MODELOS DE LOGIN (AUTH)
    // ==========================================
    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class LoginResponse(
        val status: Boolean,
        val status_code: Int,
        val message: String,
        val response: LoginData
    )

    data class LoginData(
        val token: String,
        val user: UserData
    )

    data class UserData(
        val id: Int,
        val name: String,
        val email: String,
        @SerializedName("apartment", alternate = ["apto", "unidade"])
        val apto: String? = null,
        val block: String? = null,
        val cpf: String? = null,
        val phone: String? = null
    )

    // ==========================================
    // 2. MODELOS DE RESIDENTE (CONTA GERAL)
    // ==========================================

    /**
     * Classe "Envelope" para capturar a resposta do objeto único.
     * Necessária porque a API coloca o morador dentro do campo "response".
     */
    data class SingleResidentResponse(
        val status: Boolean,
        val message: String,
        @SerializedName("response", alternate = ["data"])
        val resident: ResidentResponse
    )

    data class ResidentResponse(
        val id: Int,

        @SerializedName("name", alternate = ["nome", "username"])
        val name: String?,

        val email: String,

        @SerializedName("apartment", alternate = ["apto", "unidade"])
        val apartment: String?,

        val block: String?,
        val score: Int?,
        val phone: String? = null
    )

    data class UpdateResidentRequest(
        val id: Int, // Alterado para Int para manter consistência com ResidentResponse
        val name: String,
        val email: String,
        val apartment: String?,
        val block: String?,
        val phone: String?
    )

    // ==========================================
    // 3. INTERFACE DA API
    // ==========================================
    interface AuthApiService {

        // --- AUTENTICAÇÃO ---
        @POST("api/v1/auth/login/resident")
        suspend fun loginResident(@Body request: LoginRequest): Response<LoginResponse>

        // --- LISTAGEM GERAL ---
        @GET("api/v1/resident")
        suspend fun listResidents(
            @Header("Authorization") token: String
        ): Response<List<ResidentResponse>>

        // --- BUSCAR DADOS DA CONTA (PARA A HOME) ---
        // Retorna SingleResidentResponse para que o GSON consiga entrar no campo "response"
        @GET("api/v1/resident/{id}")
        suspend fun getResidentById(
            @Header("Authorization") token: String,
            @Path("id") id: Int
        ): Response<SingleResidentResponse>

        // --- ATUALIZAR DADOS ---
        @PUT("api/v1/resident")
        suspend fun updateResident(
            @Header("Authorization") token: String,
            @Body request: UpdateResidentRequest
        ): Response<ResidentResponse>
    }