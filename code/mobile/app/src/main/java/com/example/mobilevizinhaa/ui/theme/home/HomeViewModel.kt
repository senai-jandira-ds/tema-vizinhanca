package com.example.mobilevizinhaa.ui.theme.home

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilevizinhaa.ui.theme.data.ResidentResponse
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.example.mobilevizinhaa.ui.theme.data.CreatePostRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Modelo de dados para as postagens do mural (Atualizado de forma segura).
 */
data class Post(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val imagemRes: Int? = null,
    val imagemUri: Uri? = null,
    val imagemUrl: String? = null // Recebe o link mapeado vindo do banco
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Gerenciador de armazenamento local (SharedPreferences)
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // --- ESTADO DO USUÁRIO ---
    // Inicia carregando do disco. Se o usuário já logou antes, os dados aparecem no 1º frame.
    private val _residentData = MutableStateFlow<ResidentResponse?>(getSavedUser())
    val residentData = _residentData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // --- LISTA DE POSTAGENS REATIVAS ---
    private val _posts = mutableStateListOf<Post>()
    val posts: List<Post> = _posts

    // --- FUNÇÃO AUXILIAR PARA PEGAR O TOKEN SALVO ---
    fun obterTokenSalvo(): String = prefs.getString("auth_token", "") ?: ""

    // --- PERSISTÊNCIA LOCAL (PRIVATE) ---

    private fun saveUserLocally(user: ResidentResponse) {
        try {
            val json = gson.toJson(user)
            prefs.edit().putString("saved_user", json).apply()
        } catch (e: Exception) {
            Log.e("PERSISTENCE", "Erro ao salvar usuário: ${e.message}")
        }
    }

    private fun getSavedUser(): ResidentResponse? {
        val json = prefs.getString("saved_user", null)
        return if (json != null) {
            try {
                // Converte o JSON de volta para o objeto ResidentResponse
                gson.fromJson(json, ResidentResponse::class.java)
            } catch (e: Exception) {
                Log.e("PERSISTENCE", "Erro ao ler usuário salvo: ${e.message}")
                null
            }
        } else null
    }

    // --- AÇÕES PÚBLICAS ---

    /**
     * INJEÇÃO MANUAL (Velocidade Máxima):
     * Chamada pela LoginScreen logo após o sucesso da API de Login.
     */
    fun setResidentData(data: ResidentResponse) {
        _residentData.value = data
        saveUserLocally(data)

        // Se no momento do login já vierem publicações anexadas, renderiza-as imediatamente
        atualizarListaDePosts(data)
    }

    /**
     * SINCRONIZAÇÃO EM BACKGROUND (COM DIAGNÓSTICO DE LOGS):
     * Busca os dados atualizados usando o Token do Swagger e insere monitoramento no Logcat.
     */
    fun carregarDadosPerfil(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // Chamada de API para obter os dados do morador logado (/me/resident)
                val response = RetrofitClient.authApi.getResidentById(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val dadosDoBanco = response.body()?.resident
                    dadosDoBanco?.let {
                        // LOGS DE DEBUG COMPLETO PARA INVESTIGAÇÃO:
                        Log.d("TESTE_API", "NOME DO BANCO: ${it.name}")
                        Log.d("TESTE_API", "URL DA FOTO DO BANCO: ${it.photo}")
                        Log.d("TESTE_API", "QUANTIDADE DE POSTS: ${it.publications?.size}")

                        _residentData.value = it
                        saveUserLocally(it) // Atualiza o cache local

                        // MAPEAMENTO DOS POSTS DA API PARA A TELA:
                        atualizarListaDePosts(it)
                    }
                } else {
                    // LOG PARA EXIBIR ERROS CASO NÃO CONSIGA CONECTAR (Ex: Token Expirado 401 ou Erro de Objeto)
                    Log.e("TESTE_API", "ERRO HTTP: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("TESTE_API", "Falha catastrófica na conexão/rede: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Pega a lista "publications" trazida pela API e converte para a lista observável da UI.
     */
    private fun atualizarListaDePosts(resident: ResidentResponse) {
        _posts.clear() // Limpa os elementos antigos da UI para evitar duplicados
        resident.publications?.forEach { pub ->
            _posts.add(
                Post(
                    id = pub.id,
                    titulo = pub.title,
                    descricao = pub.description,
                    imagemUrl = pub.photo // Puxa a string da foto da publicação mapeada no JSON
                )
            )
        }
    }

    /**
     * SALVAR NO BANCO DE DADOS DA API:
     * Envia os dados para o servidor remoto e força a atualização do perfil para atualizar a tela.
     */
    fun adicionarPostNoBanco(titulo: String, descricao: String, fotoUrlOuBase64: String? = null) {
        val token = obterTokenSalvo()

        if (token.isEmpty()) {
            Log.e("API_HOME", "Erro: Token ausente. Usuário não autenticado para postar.")
            return
        }

        viewModelScope.launch {
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val request = CreatePostRequest(title = titulo, description = descricao, photo = fotoUrlOuBase64)

                val response = RetrofitClient.authApi.criarPublicacao(authHeader, request)

                if (response.isSuccessful) {
                    Log.d("API_HOME", "Post inserido no banco com sucesso!")
                    // Recarrega o perfil para sincronizar as publicações na hora
                    carregarDadosPerfil(token)
                } else {
                    Log.e("API_HOME", "Servidor recusou a postagem. Código: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_HOME", "Falha de rede ao tentar salvar publicação: ${e.message}")
            }
        }
    }

    /**
     * LOGOUT:
     * Limpa a memória e o disco para deslogar com segurança.
     */
    fun logout() {
        _residentData.value = null
        _posts.clear()
        prefs.edit().remove("saved_user").apply()
    }

    // --- GESTÃO LOCAL MANTIDA PARA COMPATIBILIDADE ---

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
}