package com.example.mobilevizinhaa.ui.theme.data

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

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
    val photo: String?,       // URL remota ou Base64 retornada pelo banco do Render
    val title: String,        // Título do post
    val description: String,  // Descrição do post
    val creationDate: String?
)

/**
 * Classe "Envelope" para capturar o objeto de perfil único do Swagger.
 * Usada tanto na busca (GET) quanto na atualização cadastral (PUT).
 */
data class SingleResidentResponse(
    val status: Boolean,
    val message: String,
    @SerializedName("response", alternate = ["data", "resident"]) // COMPLEMENTADO: Mapeia se o backend envelopar como 'resident' ou 'data'
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
    @SerializedName("photo", alternate = ["photoUrl", "avatar"]) // COMPLEMENTADO: Garante compatibilidade caso mude para avatar ou photoUrl
    val photo: String? = null,
    val publications: List<PublicationResponse>? = null // Lista de posts específicos deste usuário
)

/**
 * Modelo de Payload para Atualização do Residente.
 * Atualizado para incluir o campo 'photo' que transportará o Base64 da galeria no PUT oficial.
 */
data class UpdateResidentRequest(
    val id: Int,
    val name: String,
    val email: String,
    val apartment: String?,
    val block: BlockResponse?,
    val phone: String?,
    @SerializedName("photo", alternate = ["photoUrl", "avatar"]) val photo: String? = null // COMPLEMENTADO: Suporte a mapeamentos alternativos de foto no PUT
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
    @SerializedName("photo", alternate = ["photoBase64"]) val photoBase64: String // COMPLEMENTADO: Evita erro se a propriedade variar no JSON
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
    @SerializedName("response", alternate = ["data"]) val response: CreatePostDataResponse? // COMPLEMENTADO: Captura caso mude para 'data'
)

/**
 * Detalhes do registro inserido no Banco de Dados que o servidor retorna
 */
data class CreatePostDataResponse(
    val id: Int,
    @SerializedName("photo", alternate = ["photoUrl"]) val photo: String?, // COMPLEMENTADO: Mapeia o retorno da imagem com segurança
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

    // --- ATUALIZAR CONTA E FOTO DO PERFIL (PUT PRINCIPAL - CORRIGIDO) ---
    @PUT("api/v1/resident")
    suspend fun updateResident(
        @Header("Authorization") token: String,
        @Body request: UpdateResidentRequest
    ): Response<SingleResidentResponse>

    // --- SALVAR NOVA POSTAGEM NA CONTA DO RESIDENTE (JSON PURÍSSIMO) ---
    @POST("api/v1/publication")
    suspend fun criarPublicacao(
        @Header("Authorization") token: String,
        @Body request: CreatePostRequest
    ): Response<CreatePostResponse>
}