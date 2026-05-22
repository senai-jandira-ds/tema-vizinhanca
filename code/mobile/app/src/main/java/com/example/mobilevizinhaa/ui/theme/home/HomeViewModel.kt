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
import com.example.mobilevizinhaa.ui.theme.data.ResidentResponse
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.example.mobilevizinhaa.ui.theme.data.CreatePostRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    val imagemUrl: String? = null // Recebe o link final em nuvem gerado pela API
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Instância do Contexto obtida de forma segura através da herança de AndroidViewModel
    private val context = application.applicationContext

    // Gerenciador de armazenamento local para persistência de tokens e perfil em cache
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // --- ESTADO DO USUÁRIO ---
    // Recupera os dados locais instantaneamente para evitar tela em branco ao abrir o App
    private val _residentData = MutableStateFlow<ResidentResponse?>(getSavedUser())
    val residentData = _residentData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // --- CONTROLE DE SUCESSO DO FORMULÁRIO ---
    private val _postCriadoComSucesso = MutableStateFlow(false)
    val postCriadoComSucesso = _postCriadoComSucesso.asStateFlow()

    // --- LISTA REATIVA DE FOTOS DA TELA HOME ---
    private val _posts = mutableStateListOf<Post>()
    val posts: List<Post> = _posts

    // --- RECUPERAR TOKEN DE SEGURANÇA ---
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

    // --- INTEGRAÇÃO COM BACKEND ---

    /**
     * Vincula as informações do residente vindas da tela de login e inicia os posts.
     */
    fun setResidentData(data: ResidentResponse) {
        _residentData.value = data
        saveUserLocally(data)
        atualizarListaDePosts(data)
    }

    /**
     * Sincroniza em background os dados mais recentes do perfil e a grade de postagens.
     */
    fun carregarDadosPerfil(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // Consome a rota do Swagger específica do perfil logado
                val response = RetrofitClient.authApi.getResidentById(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val dadosDoBanco = response.body()?.resident
                    dadosDoBanco?.let {
                        Log.d("TESTE_API", "Usuário autenticado: ${it.name}")
                        Log.d("TESTE_API", "Quantidade de postagens no perfil: ${it.publications?.size ?: 0}")

                        _residentData.value = it
                        saveUserLocally(it)
                        atualizarListaDePosts(it)
                    }
                } else {
                    Log.e("TESTE_API", "Erro de resposta HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("TESTE_API", "Falha de comunicação/Rede ao buscar dados: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Converte as publicações brutas do backend para a estrutura de dados renderizada na UI.
     */
    private fun atualizarListaDePosts(resident: ResidentResponse) {
        _posts.clear() // Limpa referências antigas para prevenir travamento ou itens duplicados
        resident.publications?.forEach { pub ->
            _posts.add(
                Post(
                    id = pub.id,
                    titulo = pub.title,
                    descricao = pub.description, // Atribuição corrigida com base nos modelos de dados
                    imagemUrl = pub.photo
                )
            )
        }
    }

    /**
     * SALVAR NOVA POSTAGEM NA CONTA DO RESIDENTE (SWAGGER INTERFACE)
     * Compacta a foto local, monta o request JSON e dispara para o servidor.
     */
    fun adicionarPostNoBanco(titulo: String, descricao: String, uriImagem: Uri?) {
        val token = obterTokenSalvo()

        if (token.isEmpty()) {
            Log.e("API_HOME", "Operação abortada: Token de autenticação inexistente.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _postCriadoComSucesso.value = false
            try {
                // Executa a compressão e codificação Base64 da imagem de forma assíncrona
                val fotoBase64 = if (uriImagem != null) {
                    otimizarEConverterParaBase64(uriImagem)
                } else null

                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val request = CreatePostRequest(
                    title = titulo,
                    description = descricao,
                    photo = fotoBase64
                )

                // Envia a publicação para a API do Render
                val response = RetrofitClient.authApi.criarPublicacao(authHeader, request)

                if (response.isSuccessful) {
                    Log.d("API_HOME", "Postagem criada com sucesso no banco de dados!")

                    // Força o aplicativo a buscar a nova lista atualizada e atualizar a Home instantaneamente
                    carregarDadosPerfil(token)
                    _postCriadoComSucesso.value = true
                } else {
                    Log.e("API_HOME", "Servidor rejeitou a publicação: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API_HOME", "Falha crítica de rede ao tentar salvar postagem: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * COMPRESSÃO INTERNA E REDIMENSIONAMENTO DE IMAGENS
     * Protege contra estouros de limite de payload HTTP (Error 413) redimensionando a foto
     * para no máximo 800px e gerando uma String Base64 limpa sem cabeçalhos corrompidos.
     */
    private fun otimizarEConverterParaBase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmapOriginal != null) {
                val maxDimensao = 800
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

                // Aplica compressão JPEG equilibrando perfeitamente qualidade e tamanho físico
                bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
                val bytesComprimidos = outputStream.toByteArray()

                // Gera string Base64 plana perfeitamente compatível com campos de String do Swagger
                Base64.encodeToString(bytesComprimidos, Base64.DEFAULT)
                    .trim()
                    .replace("\n", "")
                    .replace("\r", "")
            } else null
        } catch (e: Exception) {
            Log.e("IMAGE_OPTIMIZER", "Falha interna no processamento de bytes da imagem: ${e.message}")
            null
        }
    }

    fun resetarEstadoSucesso() {
        _postCriadoComSucesso.value = false
    }

    fun logout() {
        _residentData.value = null
        _posts.clear()
        prefs.edit().remove("saved_user").apply()
    }

    // --- MÉTODOS DE CONTROLE LOCAL MANTIDOS PARA COMPATIBILIDADE ---

    fun adicionarPost(titulo: String, descricao: String, uri: Uri?) {
        val nextId = if (_posts.isEmpty()) 1 else _posts.maxOf { it.id } + 1
        _posts.add(0, Post(
            id = nextId,
            titulo = titulo,
            descricao = descricao,
            imagemUri = uri
        ))
    }

    fun deletarPost(postId: Int) {
        _posts.removeAll { it.id == postId }
    }

    private fun carregarImagemBase64MuralLocal(base64String: String?): ImageBitmap? {
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

    // --- FLUXO COMPLETO E SEGURO DE LOGOUT ---
    fun deslogar(navController: NavController) {
        viewModelScope.launch {
            try {
                // 1. Limpa chaves, tokens e strings serializadas armazenadas no cache local
                prefs.edit().clear().apply()

                // 2. Apaga estados reativos e limpa o vetor de imagens da UI
                _residentData.value = null
                _posts.clear()

                // 3. Modifica a navegação para a tela de Login resetando o histórico
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            } catch (e: Exception) {
                Log.e("HOME_VIEWMODEL", "Erro na execução do encerramento de sessão: ${e.message}")
            }
        }
    }
}