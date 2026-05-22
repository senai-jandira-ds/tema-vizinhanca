package com.example.mobilevizinhaa.ui.theme.data

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

// ==========================================
// OBJETO BLOCO (SWAGGER)
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
    val block: BlockResponse? = null,
    val cpf: String? = null,
    val phone: String? = null
)

// ==========================================
// 2. MODELOS DE RESIDENTE E POSTAGENS (SWAGGER)
// ==========================================

/**
 * Modelo das publicações individuais vinculadas à conta do residente.
 * Alimenta diretamente a grade de fotos (Mural Pessoal) da HomeScreen.
 */
data class PublicationResponse(
    val id: Int,
    @SerializedName("photo", alternate = ["photoUrl", "imageUrl"])
    val photo: String?,       // URL remota ou Base64 vindo do banco do Render
    val title: String,        // Título do post
    val description: String,  // Descrição do post
    val creationDate: String?
)

/**
 * Classe "Envelope" para capturar o objeto de perfil único do Swagger.
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
    val block: BlockResponse?,
    val score: Int?,
    val phone: String? = null,
    @SerializedName("photo", alternate = ["photoUrl", "avatar"])
    val photo: String? = null,
    val publications: List<PublicationResponse>? = null // Lista de posts específicos deste usuário
)

data class UpdateResidentRequest(
    val id: Int,
    val name: String,
    val email: String,
    val apartment: String?,
    val block: BlockResponse?,
    val phone: String?
)

// ==========================================
// 3. MODELOS PARA CRIAR POSTAGEM DO RESIDENTE
// ==========================================

/**
 * Objeto enviado no corpo do POST para registrar uma nova publicação do usuário
 */
data class CreatePostRequest(
    val title: String,
    val description: String,
    val photo: String? = null // String da imagem (URL ou Base64) obtida no dispositivo
)

/**
 * Resposta padrão que a API retorna confirmando que a postagem foi salva
 */
data class CreatePostResponse(
    val status: Boolean,
    val message: String
)

// ==========================================
// 4. INTERFACE DA API (ROTAS DE RETROFIT)
// ==========================================
interface AuthApiService {

    // --- AUTENTICAÇÃO ---
    @POST("api/v1/auth/login/resident")
    suspend fun loginResident(@Body request: LoginRequest): Response<LoginResponse>

    // --- LISTAGEM GERAL DE RESIDENTES ---
    @GET("api/v1/resident")
    suspend fun listResidents(
        @Header("Authorization") token: String
    ): Response<List<ResidentResponse>>

    // --- BUSCAR DADOS DA CONTA AUTENTICADA ---
    // Puxa as informações de perfil (Área Verde) e a lista de posts do usuário (Área Vermelha)
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

    // --- SALVAR NOVA POSTAGEM NA CONTA DO RESIDENTE ---
    // Executa o disparo quando o morador preenche o formulário na tela de criação
    @POST("api/v1/publication")
    suspend fun criarPublicacao(
        @Header("Authorization") token: String,
        @Body request: CreatePostRequest
    ): Response<CreatePostResponse>
}