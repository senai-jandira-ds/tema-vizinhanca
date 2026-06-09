package com.example.mobilevizinhaa.ui.theme.home

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mobilevizinhaa.ui.theme.data.CreatePostRequest
import com.example.mobilevizinhaa.ui.theme.data.ResidentResponse
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.example.mobilevizinhaa.ui.theme.data.UpdateResidentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Modelo de dados para as postagens do mural pessoal do morador.
 */
data class Post(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val imagemRes: Int? = null,
    val imagemUri: Uri? = null,
    val imagemUrl: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = com.google.gson.Gson()

    // --- GERENCIAMENTO DE TEMA PERSISTENTE ---
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // --- ESTADO DO USUÁRIO ---
    private val _residentData = MutableStateFlow<ResidentResponse?>(getSavedUser())
    val residentData = _residentData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 🎯 ESTADOS REATIVOS: Contadores dinâmicos para a PerfilScreen
    private val _qtdPedidos = MutableStateFlow("0")
    val qtdPedidos: StateFlow<String> = _qtdPedidos.asStateFlow()

    private val _qtdObjetos = MutableStateFlow("0")
    val qtdObjetos: StateFlow<String> = _qtdObjetos.asStateFlow()

    // --- CONTROLE DE SUCESSO DO FORMULÁRIO ---
    private val _postCriadoComSucesso = MutableStateFlow(false)
    val postCriadoComSucesso = _postCriadoComSucesso.asStateFlow()

    // --- LISTA REATIVA DE FOTOS DA TELA HOME ---
    private val _posts = mutableStateListOf<Post>()
    val posts: List<Post> = _posts

    init {
        _isDarkMode.value = prefs.getBoolean("is_dark_mode", false)
        val token = obterTokenSalvo()
        if (token.isNotEmpty()) {
            carregarDadosPerfil(token)
        }
    }

    fun alternarTema(modoEscuro: Boolean) {
        _isDarkMode.value = modoEscuro
        prefs.edit().putBoolean("is_dark_mode", modoEscuro).apply()
    }

    fun obterTokenSalvo(): String = prefs.getString("auth_token", "") ?: ""

    // --- PERSISTÊNCIA E CACHE LOCAL ---

    private fun saveUserLocally(user: ResidentResponse) {
        try {
            val json = gson.toJson(user)
            prefs.edit().putString("saved_user", json).apply()
        } catch (e: Exception) {
            Log.e("PERSISTENCE", "Erro ao salvar usuário no SharedPreferences: ${e.message}")
        }
    }

    private fun getSavedUser(): ResidentResponse? {
        val json = prefs.getString("saved_user", null)
        return if (json != null) {
            try {
                gson.fromJson(json, ResidentResponse::class.java)
            } catch (e: Exception) {
                Log.e("PERSISTENCE", "Erro ao desserializar JSON do cache: ${e.message}")
                null
            }
        } else null
    }

    fun setResidentData(data: ResidentResponse) {
        _residentData.value = data
        saveUserLocally(data)
        atualizarListaDePosts(data)
    }

    // --- SINCRO COM BACKEND + BUSCA PARALELA DE PEDIDOS E OBJETOS ---
    fun carregarDadosPerfil(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // 1. Consome a rota do Swagger específica do perfil logado
                val response = RetrofitClient.authApi.getResidentById(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    // 🎯 CORRIGIDO: Mapeado para '.response' conforme a nova estrutura JSON do seu SingleResidentResponse
                    val dadosDoBanco = response.body()?.response
                    dadosDoBanco?.let {
                        Log.d("TESTE_API", "Usuário autenticado: ${it.name}")
                        _residentData.value = it
                        saveUserLocally(it)
                        atualizarListaDePosts(it)
                    }
                } else {
                    Log.e("TESTE_API", "Erro de resposta HTTP: ${response.code()}")
                }

                // 2. 📦 BUSCA DINÂMICA DE PEDIDOS (SERVIÇOS)
                val responseServices = RetrofitClient.authApi.listarServicos(authHeader)
                if (responseServices.isSuccessful) {
                    val listaServicos = responseServices.body()?.responseData?.content
                    val meusServicos = listaServicos?.filter { it.resident?.id == _residentData.value?.id } ?: emptyList()
                    _qtdPedidos.value = meusServicos.size.toString()
                }

                // 3. 🔑 BUSCA DINÂMICA DE OBJETOS COMPARTILHADOS
                val responseObjects = RetrofitClient.authApi.listarObjetosPaginados(authHeader)
                if (responseObjects.isSuccessful) {
                    val listaObjetos = responseObjects.body()?.responseData?.content
                    val meusObjetos = listaObjetos?.filter { it.resident?.id == _residentData.value?.id } ?: emptyList()
                    _qtdObjetos.value = meusObjetos.size.toString()
                }

            } catch (e: Exception) {
                Log.e("TESTE_API", "Falha de comunicação/Rede ao buscar dados: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun atualizarListaDePosts(resident: ResidentResponse) {
        _posts.clear()
        resident.publications?.forEach { pub ->
            _posts.add(
                Post(
                    id = pub.id,
                    titulo = pub.title,
                    descricao = pub.description,
                    imagemUrl = pub.photo
                )
            )
        }
    }

    // --- ATUALIZAR FOTO DE PERFIL COM SUCESSO ---
    fun atualizarFotoPerfil(context: Context, uriImagem: Uri) {
        val token = obterTokenSalvo()
        val usuarioAtual = _residentData.value

        if (token.isEmpty() || usuarioAtual == null) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val fotoBase64Pura = obterStringBase64Otimizada(uriImagem)

                if (fotoBase64Pura.isNotEmpty()) {
                    // 🎯 CORRIGIDO: Construtor adaptado para a nova estrutura do UpdateResidentRequest
                    val requestBody = UpdateResidentRequest(
                        photo = fotoBase64Pura,
                        name = usuarioAtual.name ?: "string",
                        apartment = usuarioAtual.apartment ?: "string",
                        idBlock = usuarioAtual.block?.id ?: 0,
                        cpf = usuarioAtual.cpf ?: "string",
                        email = usuarioAtual.email,
                        phone = usuarioAtual.phone ?: "string",
                        score = usuarioAtual.score ?: 0,
                        is_active = usuarioAtual.is_active ?: true,
                        password = null
                    )

                    // 🎯 CORRIGIDO: Passando o ID dinâmico do morador na assinatura do método PUT
                    val response = RetrofitClient.authApi.updateResident(
                        token = authHeader,
                        id = usuarioAtual.id,
                        request = requestBody
                    )

                    if (response.isSuccessful && response.body() != null) {
                        // Mapeia de forma limpa para .response
                        val moradorAtualizadoPeloBanco = response.body()?.response
                        moradorAtualizadoPeloBanco?.let { novoPerfil ->
                            _residentData.value = novoPerfil
                            saveUserLocally(novoPerfil)
                            atualizarListaDePosts(novoPerfil)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API_PROFILE", "❌ FALHA CRÍTICA: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun adicionarPostNoBanco(titulo: String, descricao: String, uriImagem: Uri?) {
        val token = obterTokenSalvo()
        if (token.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _postCriadoComSucesso.value = false
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val fotoBase64 = obterStringBase64Otimizada(uriImagem)

                val requestBody = CreatePostRequest(
                    title = titulo,
                    description = descricao,
                    photoBase64 = fotoBase64
                )

                val response = RetrofitClient.authApi.criarPublicacao(
                    token = authHeader,
                    request = requestBody
                )

                if (response.isSuccessful) {
                    carregarDadosPerfil(token)
                    _postCriadoComSucesso.value = true
                }
            } catch (e: Exception) {
                Log.e("API_HOME", "Falha crítica de rede: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletarPost(postId: Int, onSuccess: () -> Unit = {}) {
        val token = obterTokenSalvo()
        if (token.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authApi.deletarPublicacao(authHeader, postId)

                if (response.isSuccessful) {
                    _posts.removeAll { it.id == postId }
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                Log.e("API_DELETE", "❌ Erro ao deletar: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun obterStringBase64Otimizada(uri: Uri?): String = withContext(Dispatchers.IO) {
        if (uri == null) return@withContext ""
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmapOriginal != null) {
                val maxDimensao = 500
                val proporcao = bitmapOriginal.width.toFloat() / bitmapOriginal.height.toFloat()

                var larguraFinal = maxDimensao
                var alturaFinal = maxDimensao
                if (proporcao > 1) {
                    alturaFinal = (maxDimensao / proporcao).toInt()
                } else {
                    larguraFinal = (maxDimensao * proporcao).toInt()
                }

                val bitmapRedimensionado = Bitmap.createScaledBitmap(bitmapOriginal, larguraFinal, alturaFinal, true)
                val outputStream = ByteArrayOutputStream()
                bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
                val bytes = outputStream.toByteArray()
                outputStream.close()

                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    fun resetarEstadoSucesso() {
        _postCriadoComSucesso.value = false
    }

    fun carregarImagemBase64MuralLocal(base64String: String?): ImageBitmap? {
        if (base64String.isNullOrBlank() || base64String == "string") return null
        return try {
            val stringLimpa = if (base64String.contains(",")) {
                base64String.substring(base64String.indexOf(",") + 1)
            } else {
                base64String
            }.trim()

            val bytesDecodificados = Base64.decode(stringLimpa, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytesDecodificados, 0, bytesDecodificados.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    // --- FLUXO COMPLETO E SEGURO DE LOGOUT COERENTE ---
    fun deslogar(navController: NavController) {
        viewModelScope.launch {
            try {
                prefs.edit().clear().apply()

                _residentData.value = null
                _qtdPedidos.value = "0"
                _qtdObjetos.value = "0"
                _posts.clear()

                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            } catch (e: Exception) {
                Log.e("HOME_VIEWMODEL", "Erro no logout: ${e.message}")
            }
        }
    }

    // Métodos legados mantidos para compatibilidade com chamadas locais antigas
    fun logout() {
        _residentData.value = null
        _posts.clear()
        prefs.edit().remove("saved_user").apply()
    }

    fun adicionarPost(titulo: String, descricao: String, uri: Uri?) {
        val nextId = if (_posts.isEmpty()) 1 else _posts.maxOf { it.id } + 1
        _posts.add(
            0, Post(
                id = nextId,
                titulo = titulo,
                descricao = descricao,
                imagemUri = uri
            )
        )
    }
}