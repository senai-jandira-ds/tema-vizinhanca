package com.example.mobilevizinhaa.ui.theme.data

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

// ==========================================
// NOVO MODELO PARA O OBJETO BLOCO (SWAGGER)
// ==========================================
data class BlockResponse(
    val id: Int? = null,
    val name: String? = null
)

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
    // CORRIGIDO: Alterado de String? para BlockResponse? para receber o objeto JSON do backend
    val block: BlockResponse? = null,
    val cpf: String? = null,
    val phone: String? = null
)

// ==========================================
// 2. MODELOS DE RESIDENTE (CONTA GERAL COM AS PUBLICAÇÕES)
// ==========================================

/**
 * Modelo das publicações individuais do próprio usuário, vindo do JSON.
 */
data class PublicationResponse(
    val id: Int,
    @SerializedName("photo", alternate = ["photoUrl", "imageUrl"])
    val photo: String?,       // Mapeamento flexível para capturar a URL da foto do post do Swagger
    val title: String,        // Título do post
    val description: String,  // Descrição do post
    val creationDate: String?
)

/**
 * Classe "Envelope" para capturar a resposta do objeto único.
 * Garante que o objeto do morador seja extraído corretamente de dentro da chave "response".
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
    // CORRIGIDO: Alterado de String? para BlockResponse? para aceitar a estrutura de objeto do condomínio
    val block: BlockResponse?,
    val score: Int?,
    val phone: String? = null,
    @SerializedName("photo", alternate = ["photoUrl", "avatar"])
    val photo: String? = null, // Mapeamento flexível da foto de perfil (Sincroniza com a Área Verde)
    val publications: List<PublicationResponse>? = null // Lista de posts do usuário (Sincroniza com a Área Vermelha)
)

data class UpdateResidentRequest(
    val id: Int,
    val name: String,
    val email: String,
    val apartment: String?,
    // CORRIGIDO: Modificado para BlockResponse? para manter consistência nas atualizações de perfil
    val block: BlockResponse?,
    val phone: String?
)

// ==========================================
// MODELOS PARA CRIAR POSTAGEM NO BANCO
// ==========================================
data class CreatePostRequest(
    val title: String,
    val description: String,
    val photo: String? = null // URL da imagem ou string em Base64 enviado para a API
)

data class CreatePostResponse(
    val status: Boolean,
    val message: String
)

// ==========================================
// 3. INTERFACE DA API (ROTAS DE RETROFIT)
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

    // --- BUSCAR DADOS DA CONTA AUTENTICADA (PARA A HOME) ---
    // Sincroniza os dados do perfil e a lista de posts do usuário logado usando o Token
    @GET("api/v1/auth/me/resident")
    suspend fun getResidentById(
        @Header("Authorization") token: String
    ): Response<SingleResidentResponse>

    // --- ATUALIZAR DADOS DO PERFIL ---
    @PUT("api/v1/resident")
    suspend fun updateResident(
        @Header("Authorization") token: String,
        @Body request: UpdateResidentRequest
    ): Response<ResidentResponse>

    // --- SALVAR NOVA POSTAGEM NO MURAL ---
    @POST("api/v1/publication")
    suspend fun criarPublicacao(
        @Header("Authorization") token: String,
        @Body request: CreatePostRequest
    ): Response<CreatePostResponse>
}