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
    @SerializedName("response", alternate = ["data", "resident"])
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

/**
 * Modelo de Payload para textos e fotos na Atualização do Residente.
 */
data class UpdateResidentRequest(
    val id: Int,
    val name: String,
    val email: String,
    val apartment: String?,
    val block: BlockResponse?,
    val phone: String?,
    @SerializedName("photo", alternate = ["photoUrl", "avatar"]) val photo: String? = null
)

// ==========================================
// 3. MODELOS PARA CRIAR POSTAGEM DO RESIDENTE (JSON / BASE64)
// ==========================================

data class CreatePostRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("photo", alternate = ["photoBase64"]) val photoBase64: String
)

data class CreatePostResponse(
    val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    val developer: String?,
    @SerializedName("api_description") val apiDescription: String?,
    val version: String?,
    @SerializedName("request_date") val requestDate: String?,
    val message: String,
    @SerializedName("response", alternate = ["data"]) val response: CreatePostDataResponse?
)

data class CreatePostDataResponse(
    val id: Int,
    @SerializedName("photo", alternate = ["photoUrl"]) val photo: String?,
    val title: String,
    val description: String,
    val creationDate: String?,
    val residentId: Int?,
    val residentName: String?
)

// ==========================================
// 4. MODELOS PARA CRIAÇÃO E LISTAGEM DE SERVIÇO (SWAGGER / JSON)
// ==========================================

/**
 * Payload JSON enviado no corpo (Body) para criar um novo serviço ou objeto.
 * TOTALMENTE BLINDADO: Envia as chaves numéricas em snake_case para o validador
 * e utiliza o status "ACTIVE" para evitar truncamento na tabela do banco de dados.
 */
data class CreateServiceRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("photo", alternate = ["photoBase64", "photo_base64"])
    val photoBase64: String,

    @SerializedName("estimated_time", alternate = ["estimatedTime", "estimatedtime"])
    val estimatedTime: Int,

    @SerializedName("urgency")
    val urgency: String,

    @SerializedName("category_id", alternate = ["categoryId", "categoryid"])
    val categoryId: Int,

    // CORREÇÃO: Alterado de "ATIVO" para "ACTIVE" para sanar o erro 500 de truncamento de coluna
    @SerializedName("status")
    val status: String = "ACTIVE"
)

/**
 * Resposta de sucesso do servidor do Render para a rota de serviços (GET e POST)
 * Mapeada de acordo com o JSON fornecido pelo Swagger.
 */
data class CreateServiceResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code", alternate = ["statusCode"]) val statusCode: Int,
    @SerializedName("developer") val developer: String? = null,
    @SerializedName("api_description", alternate = ["apiDescription"]) val apiDescription: String? = null,
    @SerializedName("version") val version: String? = null,
    @SerializedName("request_date", alternate = ["requestDate"]) val requestDate: String? = null,
    @SerializedName("message") val message: String,
    @SerializedName("response", alternate = ["data"]) val response: ServiceResponseData?
)

data class ServiceResponseData(
    @SerializedName("amountServices") val amountServices: Int? = null,
    @SerializedName("services") val services: List<ServiceDetail>? = null
)

data class ServiceDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("photo", alternate = ["photoUrl"]) val photo: String?,
    @SerializedName("title") val title: String,
    @SerializedName("estimatedTime") val estimatedTime: Int,
    @SerializedName("urgency") val urgency: String,
    @SerializedName("description") val description: String,
    @SerializedName("status") val status: String,
    @SerializedName("resident") val resident: ResidentDetail?,
    @SerializedName("category") val category: CategoryDetail?
)

data class ResidentDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("apartment") val apartment: String?,
    @SerializedName("cpf") val cpf: String?,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("score") val score: Int?,
    @SerializedName("creationDate") val creationDate: String?,
    @SerializedName("block") val block: BlockDetail?
)

data class BlockDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("block") val block: String?
)

data class CategoryDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("typeCategory") val typeCategory: String?
)

// ==========================================
// 5. INTERFACE DA API (ROTAS DE RETROFIT)
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

    // --- ATUALIZAR CONTA E FOTO DO PERFIL ---
    @PUT("api/v1/resident")
    suspend fun updateResident(
        @Header("Authorization") token: String,
        @Body request: UpdateResidentRequest
    ): Response<SingleResidentResponse>

    // --- SALVAR NOVA POSTAGEM NA CONTA DO RESIDENTE ---
    @POST("api/v1/publication")
    suspend fun criarPublicacao(
        @Header("Authorization") token: String,
        @Body request: CreatePostRequest
    ): Response<CreatePostResponse>

    // --- SALVAR NOVO SERVIÇO/OBJETO NO BANCO (POST) ---
    @POST("api/v1/service")
    suspend fun criarServico(
        @Header("Authorization") token: String,
        @Body request: CreateServiceRequest
    ): Response<CreateServiceResponse>

    // --- BUSCA TODOS OS SERVIÇOS DO CONDOMÍNIO (GET) ---
    @GET("api/v1/service")
    suspend fun listarServicos(
        @Header("Authorization") token: String
    ): Response<CreateServiceResponse>
}