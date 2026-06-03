package com.example.mobilevizinhaa.ui.theme.data

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
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

data class PublicationResponse(
    val id: Int,
    @SerializedName("photo", alternate = ["photoUrl", "imageUrl"])
    val photo: String?,
    val title: String,
    val description: String,
    val creationDate: String?
)

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
    val publications: List<PublicationResponse>? = null
)

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
// 4. MODELOS PARA CRIAÇÃO DE SERVIÇO (JSON)
// ==========================================

data class CreateServiceRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("photo")
    val photoBase64: String,

    @SerializedName("estimated_time", alternate = ["estimatedTime", "estimatedtime"])
    val estimatedTime: Int,

    @SerializedName("urgency")
    val urgency: String,

    @SerializedName("category_id", alternate = ["categoryId", "categoryid"])
    val categoryId: Int,

    @SerializedName("status")
    val status: String = "PENDENTE"
)

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

// ==========================================
// 4.1 MODELOS DE LISTAGEM PAGINADA REAL DO BACKEND (GET /service)
// ==========================================

data class ServicePagedResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("developer") val developer: String? = null,
    @SerializedName("api_description") val apiDescription: String? = null,
    @SerializedName("version") val version: String? = null,
    @SerializedName("request_date") val requestDate: String? = null,
    @SerializedName("message") val message: String?,
    @SerializedName("response") val responseData: ServicePageContainer
)

data class ServicePageContainer(
    @SerializedName("total_elements") val totalElements: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("page_size") val pageSize: Int,
    @SerializedName("content") val content: List<ServiceDetailBackend> // <- O array mapeado com sucesso do Log!
)

data class ServiceDetailBackend(
    @SerializedName("id") val id: Int,
    @SerializedName("photo") val photoBase64: String?,
    @SerializedName("title") val title: String,
    @SerializedName("estimated_time") val estimatedTime: Int,
    @SerializedName("urgency") val urgency: String,
    @SerializedName("description") val description: String,
    @SerializedName("creation_date") val creationDate: String?,
    @SerializedName("status") val status: String,
    @SerializedName("resident") val resident: ResidentDetail?,
    @SerializedName("category") val category: CategoryDetail?
)

// ==========================================
// 4.2 MODELOS AJUSTADOS PARA COMPARTILHAMENTO DE OBJETOS
// ==========================================

data class CreateObjectRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("photoBase64") val photoBase64: String,
    @SerializedName("deadline") val deadline: String,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("status") val status: String = "INDISPONIVEL"
)

data class CreateObjectResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("message") val message: String?,
    @SerializedName("response") val response: ObjectDataContainer?
)

data class ObjectDataContainer(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?
)

data class ObjectPagedResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("message") val message: String?,
    @SerializedName("response") val responseData: ObjectPageContainer
)

data class ObjectPageContainer(
    @SerializedName("total_elements") val totalElements: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("page_size") val pageSize: Int,
    @SerializedName("content") val content: List<ObjectDetailBackend>
)

data class ObjectDetailBackend(
    @SerializedName("id") val id: Int,
    @SerializedName("photo") val photo: String?,
    @SerializedName("title") val title: String,
    @SerializedName("deadline") val deadline: String?,
    @SerializedName("description") val description: String,
    @SerializedName("creation_date") val creationDate: String?,
    @SerializedName("status") val status: String,
    @SerializedName("resident") val resident: ResidentDetail?,
    @SerializedName("category") val category: CategoryDetail?
)

// ==========================================
// SUB-MODELOS COMPARTILHADOS (Residente, Bloco, etc)
// ==========================================

data class ResidentDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("apartment") val apartment: String?,
    @SerializedName("cpf") val cpf: String?,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("score") val score: Int?,
    @SerializedName("creationDate", alternate = ["creation_date"]) val creationDate: String?,
    @SerializedName("block") val block: BlockDetail?
)

data class BlockDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("block") val block: String?
)

// ==========================================
// MODELOS ADAPTADOS DO SWAGGER ENDPOINT /api/v1/category
// ==========================================

data class CategoryDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("type_category", alternate = ["typeCategory"]) val typeCategory: String? = null
)

data class CategoryListResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("developer") val developer: String? = null,
    @SerializedName("api_description", alternate = ["apiDescription"]) val apiDescription: String? = null,
    @SerializedName("version") val version: String? = null,
    @SerializedName("request_date", alternate = ["requestDate"]) val requestDate: String? = null,
    @SerializedName("message") val message: String,
    @SerializedName("response") val response: CategoryResponseData?
)

data class CategoryResponseData(
    @SerializedName("amount_categories") val amountCategories: Int? = null,
    @SerializedName("categories") val categories: List<CategoryDetail>? = null
)

// ==========================================
// DTO ADICIONADO PARA ENVIAR O STATUS NO CORPO (JSON)
// ==========================================
data class ServiceUpdateRequest(
    @SerializedName("status") val status: String
)

// ==========================================
// 5. INTERFACE DA API (ROTAS DE RETROFIT) - CORRIGIDA!
// ==========================================
interface AuthApiService {

    @POST("api/v1/auth/login/resident")
    suspend fun loginResident(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/v1/resident")
    suspend fun listResidents(
        @Header("Authorization") token: String
    ): Response<List<ResidentResponse>>

    @GET("api/v1/auth/me/resident")
    suspend fun getResidentById(
        @Header("Authorization") token: String
    ): Response<SingleResidentResponse>

    @PUT("api/v1/resident")
    suspend fun updateResident(
        @Header("Authorization") token: String,
        @Body request: UpdateResidentRequest
    ): Response<SingleResidentResponse>

    @POST("api/v1/publication")
    suspend fun criarPublicacao(
        @Header("Authorization") token: String,
        @Body request: CreatePostRequest
    ): Response<CreatePostResponse>

    @POST("api/v1/service")
    suspend fun criarServico(
        @Header("Authorization") token: String,
        @Body request: CreateServiceRequest
    ): Response<CreateServiceResponse>

    // 🎯 AJUSTADO: Ambos os métodos de listagem agora retornam ServicePagedResponse
    // garantindo que o Gson consiga ler o objeto de paginação retornado pelo Render.
    @GET("api/v1/service")
    suspend fun listarServicos(
        @Header("Authorization") token: String
    ): Response<ServicePagedResponse>

    @GET("api/v1/service")
    suspend fun listarServicosPaginados(
        @Header("Authorization") token: String
    ): Response<ServicePagedResponse>

    @GET("api/v1/category")
    suspend fun obterTodasCategorias(
        @Header("Authorization") token: String
    ): Response<CategoryListResponse>

    @DELETE("api/v1/service/{id}")
    suspend fun deletarServico(
        @Header("Authorization") token: String,
        @Path("id") idServico: Int
    ): Response<Unit>

    @PUT("api/v1/service/{id}")
    suspend fun atualizarStatusServico(
        @Header("Authorization") token: String,
        @Path("id") idServico: Int,
        @Body request: ServiceUpdateRequest
    ): Response<Unit>

    // ------------------------------------------------------------------------
    // ROTAS DE OBJETOS DO CONDOMÍNIO
    // ------------------------------------------------------------------------

    @Multipart
    @POST("api/v1/object")
    suspend fun criarObjeto(
        @Header("Authorization") token: String,
        @Part title: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part photo: MultipartBody.Part,
        @Part deadline: MultipartBody.Part,
        @Part status: MultipartBody.Part,
        @Part categoryId: MultipartBody.Part
    ): Response<CreateObjectResponse>

    @GET("api/v1/object")
    suspend fun listarObjetosPaginados(
        @Header("Authorization") token: String
    ): Response<ObjectPagedResponse>
