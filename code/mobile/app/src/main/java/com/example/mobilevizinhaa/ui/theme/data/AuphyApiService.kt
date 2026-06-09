package com.example.mobilevizinhaa.ui.theme.data

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    @SerializedName("status_code") val statusCode: Int,
    val developer: String?,
    @SerializedName("api_description") val apiDescription: String?,
    val version: String?,
    @SerializedName("request_date") val requestDate: String?,
    val message: String,
    @SerializedName("response") val response: ResidentResponse?
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
    val cpf: String? = null,
    @SerializedName("is_active") val is_active: Boolean? = true,
    val phone: String? = null,
    @SerializedName("photo", alternate = ["photoUrl", "avatar"])
    val photo: String? = null,
    val publications: List<PublicationResponse>? = null
)

// ====================================================================
// 🎯 CORREÇÃO COMPATÍVEL COM SPRING VALIDAÇÃO (SWAGGER)
// ====================================================================
data class UpdateResidentRequest(
    @SerializedName("photo")
    val photo: String? = "",

    @SerializedName("name")
    val name: String,

    @SerializedName("apartment")
    val apartment: String,

    // Mapeado de forma flexível para aceitar ambos os padrões exigidos pelo backend
    @SerializedName("idBlock", alternate = ["id_block"])
    val idBlock: Int?,

    @SerializedName("cpf")
    val cpf: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String,

    // Tipado como Nullable para o framework Spring validar corretamente
    @SerializedName("score")
    val score: Int?,

    // Tipado como Nullable para evitar falhas de coerção em estados primitivos falsos
    @SerializedName("is_active", alternate = ["isActive"])
    val is_active: Boolean?,

    @SerializedName("password")
    val password: String? = null
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
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("photo") val photoBase64: String,
    @SerializedName("estimated_time", alternate = ["estimatedTime", "estimatedtime"]) val estimatedTime: Int,
    @SerializedName("urgency") val urgency: String,
    @SerializedName("category_id", alternate = ["categoryId", "categoryid"]) val categoryId: Int,
    @SerializedName("status") val status: String = "PENDENTE"
)

data class CreateServiceResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code", alternate = ["statusCode"]) val statusCode: Int,
    @SerializedName("developer") val developer: String? = null,
    @SerializedName("api_description", alternate = ["apiDescription"]) val apiDescription: String? = null,
    @SerializedName("version") val version: String? = null,
    @SerializedName("request_date", alternate = ["requestDate"]) val requestDate: String? = null,
    val message: String,
    @SerializedName("response", alternate = ["data"]) val response: ServiceResponseData?
)

data class ServiceResponseData(
    @SerializedName("amountServices") val amountServices: Int? = null,
    @SerializedName("services") val services: List<ServiceDetail>? = null
)

data class ServiceDetail(
    val id: Int,
    @SerializedName("photo", alternate = ["photoUrl"]) val photo: String?,
    val title: String,
    @SerializedName("estimatedTime") val estimatedTime: Int,
    val urgency: String,
    val description: String,
    val status: String,
    val resident: ResidentDetail?,
    val category: CategoryDetail?
)

// ==========================================
// 4.1 MODELOS DE LISTAGEM PAGINADA REAL DO BACKEND (GET /service)
// ==========================================

data class ServicePagedResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("developer") val developer: String? = null,
    @SerializedName("api_description", alternate = ["apiDescription"]) val apiDescription: String? = null,
    @SerializedName("version") val version: String? = null,
    @SerializedName("request_date", alternate = ["requestDate"]) val requestDate: String? = null,
    val message: String?,
    @SerializedName("response") val responseData: ServicePageContainer
)

data class ServicePageContainer(
    val total_elements: Int,
    val total_pages: Int,
    val current_page: Int,
    val page_size: Int,
    val content: List<ServiceDetailBackend>
)

data class ServiceDetailBackend(
    val id: Int,
    @SerializedName("photo", alternate = ["photoBase64"]) val photoBase64: String?,
    val title: String,
    @SerializedName("estimated_time") val estimatedTime: Int,
    val urgency: String,
    val description: String,
    @SerializedName("creation_date") val creationDate: String?,
    val status: String,
    val resident: ResidentDetail?,
    val category: CategoryDetail?
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
    val id: Int?,
    val title: String?,
    val description: String?
)

data class ObjectPagedResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("message") val message: String?,
    @SerializedName("response") val responseData: ObjectPageContainer
)

data class ObjectPageContainer(
    @SerializedName("total_elements") val totalElements: Int? = 1,
    @SerializedName("total_pages") val totalPages: Int? = 1,
    @SerializedName("current_page") val currentPage: Int? = 1,
    @SerializedName("page_size") val pageSize: Int? = 20,
    @SerializedName("content", alternate = ["objects", "services"]) val content: List<ObjectDetailBackend>? = emptyList(),
    val id: Int? = null,
    val photo: String? = null,
    val title: String? = null,
    val deadline: String? = null,
    val description: String? = null,
    @SerializedName("creation_date") val creationDate: String? = null,
    val status: String? = null,
    val resident: ResidentDetail? = null,
    val category: CategoryDetail? = null
)

data class ObjectDetailBackend(
    val id: Int,
    @SerializedName("photo", alternate = ["photoBase64"]) val photo: String?,
    val title: String,
    val deadline: String?,
    val description: String,
    @SerializedName("creation_date") val creationDate: String?,
    val status: String,
    val resident: ResidentDetail?,
    val category: CategoryDetail?
)

// ==========================================
// SUB-MODELOS COMPARTILHADOS (Residente, Bloco, etc)
// ==========================================

data class ResidentDetail(
    val id: Int,
    val name: String?,
    val apartment: String?,
    val cpf: String?,
    val email: String,
    val phone: String?,
    val score: Int?,
    @SerializedName("creation_date", alternate = ["creationDate"]) val creationDate: String?,
    @SerializedName("is_active") val isActive: Boolean? = true,
    val block: BlockDetail?,
    @SerializedName("photo", alternate = ["photoUrl", "avatar"]) val photo: String? = null
)

data class BlockDetail(
    val id: Int,
    val block: String?
)

// ==========================================
// MODELOS ADAPTADOS DO SWAGGER ENDPOINT /api/v1/category
// ==========================================

data class CategoryDetail(
    val id: Int,
    val name: String?,
    val description: String?,
    @SerializedName("type_category", alternate = ["typeCategory"]) val typeCategory: String? = null
)

data class CategoryListResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("developer") val developer: String? = null,
    @SerializedName("api_description", alternate = ["apiDescription"]) val apiDescription: String? = null,
    @SerializedName("version") val version: String? = null,
    @SerializedName("request_date", alternate = ["requestDate"]) val requestDate: String? = null,
    val message: String,
    val response: CategoryResponseData?
)

data class CategoryResponseData(
    @SerializedName("amount_categories") val amountCategories: Int? = null,
    val categories: List<CategoryDetail>? = null
)

// ==========================================
// DTOs ADICIONADOS PARA ATUALIZAÇÃO DE STATUS (JSON)
// ==========================================
data class ServiceUpdateRequest(
    val status: String
)

data class ObjectUpdateRequest(
    val status: String
)

// ==========================================
// 6. MODELOS INTEGRADOS DO CHAT E CONVERSAS
// ==========================================

data class ResidentPagedResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    val message: String?,
    @SerializedName("response") val responseData: ResidentPageContainer
)

data class ResidentPageContainer(
    val total_elements: Int? = 0,
    val total_pages: Int? = 0,
    val current_page: Int? = 0,
    val page_size: Int? = 20,
    val content: List<ResidentDetail>? = emptyList()
)

data class ApiResponseListConversation(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    val message: String?,
    val response: List<ConversationSummaryResponse>?
)

data class ApiResponseConversationDetail(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    val message: String?,
    val response: ConversationDetailResponse?
)

data class ApiResponseListMessage(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    val message: String?,
    val response: List<MessageResponse>?
)

data class ConversationSummaryResponse(
    val id: Long,
    val createdDate: String?,
    val participants: List<ParticipantResponse>?
)

data class ConversationDetailResponse(
    val id: Long,
    val createdDate: String?,
    val participants: List<ParticipantResponse>?,
    val messages: List<MessageResponse>?
)

data class ParticipantResponse(
    val residentId: Long,
    val residentName: String?,
    val residentPhoto: String?
)

data class MessageResponse(
    val id: Long,
    val text: String?,
    val createdDate: String?,
    val status: String?,
    val conversationId: Long,
    val residentId: Long,
    val residentName: String?,
    val residentPhoto: String?
)

data class ConversationRequest(
    val targetResidentId: Long
)

data class SendMessageRequest(
    val text: String
)

// ====================================================================
// 7. INTERFACE DA API (ROTAS DE RETROFIT)
// ====================================================================
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

    @PUT("api/v1/resident/{id}")
    suspend fun updateResident(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateResidentRequest
    ): Response<SingleResidentResponse>

    // ------------------------------------------------------------------------
    // ROTAS DO MURAL DE PUBLICAÇÕES
    // ------------------------------------------------------------------------

    @POST("api/v1/publication")
    suspend fun criarPublicacao(
        @Header("Authorization") token: String,
        @Body request: CreatePostRequest
    ): Response<CreatePostResponse>

    @DELETE("api/v1/publication/{id}")
    suspend fun deletarPublicacao(
        @Header("Authorization") token: String,
        @Path("id") idPost: Int
    ): Response<Unit>

    // ------------------------------------------------------------------------
    // ROTAS DE SERVIÇOS DO CONDOMÍNIO
    // ------------------------------------------------------------------------

    @POST("api/v1/service")
    suspend fun criarServico(
        @Header("Authorization") token: String,
        @Body request: CreateServiceRequest
    ): Response<CreateServiceResponse>

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

    @Multipart
    @PUT("api/v1/object/{id}")
    suspend fun atualizarStatusObjeto(
        @Header("Authorization") token: String,
        @Path("id") idObjeto: Int,
        @Part("status") status: RequestBody
    ): Response<Unit>

    @DELETE("api/v1/object/{id}")
    suspend fun deletarObjeto(
        @Header("Authorization") token: String,
        @Path("id") idObjeto: Int
    ): Response<Unit>

    // ------------------------------------------------------------------------
    // ROTAS DE CHAT E CONVERSAS
    // ------------------------------------------------------------------------

    @GET("api/v1/resident")
    suspend fun obterTodosMoradoresPaginados(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<ResidentPagedResponse>

    @GET("api/v1/conversation")
    suspend fun listarMinhasConversas(
        @Header("Authorization") token: String
    ): Response<ApiResponseListConversation>

    @POST("api/v1/conversation")
    suspend fun criarConversa(
        @Header("Authorization") token: String,
        @Body request: ConversationRequest
    ): Response<ApiResponseConversationDetail>

    @GET("api/v1/conversation/{id}")
    suspend fun obterConversaPorId(
        @Header("Authorization") token: String,
        @Path("id") idConversa: Long
    ): Response<ApiResponseConversationDetail>

    @GET("api/v1/conversation/{id}/messages")
    suspend fun obterMensagensDaConversa(
        @Header("Authorization") token: String,
        @Path("id") idConversa: Long
    ): Response<ApiResponseListMessage>

    @POST("api/v1/conversation/{id}/messages")
    suspend fun enviarMensagem(
        @Header("Authorization") token: String,
        @Path("id") idConversa: Long,
        @Body request: SendMessageRequest
    ): Response<MessageResponse>
}