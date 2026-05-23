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
    val photo: String?,       // URL remota retornada pelo banco do Render
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
// 3. MODELOS PARA CRIAR POSTAGEM DO RESIDENTE (JSON / BASE64)
// ==========================================

/**
 * O que o aplicativo ENVIA no corpo (Body) da requisição para criar a publicação.
 * Mapeado exatamente igual ao modelo JSON exigido pelo Swagger do Scott.
 */
data class CreatePostRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("photo") val photoBase64: String // A foto convertida da galeria em texto Base64
)

/**
 * Resposta padrão expandida que a API retorna confirmando que a postagem foi salva,
 * contendo metadados completos do servidor e os detalhes do objeto criado.
 */
data class CreatePostResponse(
    val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    val developer: String?,
    @SerializedName("api_description") val apiDescription: String?,
    val version: String?,
    @SerializedName("request_date") val requestDate: String?,
    val message: String,
    val response: CreatePostDataResponse?
)

/**
 * Detalhes do registro inserido no Banco de Dados que o servidor retorna
 */
data class CreatePostDataResponse(
    val id: Int,
    val photo: String?, // Link final gerado pelo servidor da imagem (.jpg)
    val title: String,
    val description: String,
    val creationDate: String?,
    val residentId: Int?,
    val residentName: String?
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

    // --- SALVAR NOVA POSTAGEM NA CONTA DO RESIDENTE (JSON PURÍSSIMO) ---
    // Alterado de @Multipart para envio via @Body em JSON. Isso resolve nativamente o erro 500
    // de charset incompatível que o OkHttp inseria antes.
    @POST("api/v1/publication")
    suspend fun criarPublicacao(
        @Header("Authorization") token: String,
        @Body request: CreatePostRequest
    ): Response<CreatePostResponse>
}