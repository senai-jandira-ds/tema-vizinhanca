package com.example.mobilevizinhaa.ui.theme.home

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.data.ResidentResponse
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Modelo de dados para as postagens do mural.
 */
data class Post(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val imagemRes: Int? = null,
    val imagemUri: Uri? = null
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

    // --- LISTA DE POSTAGENS ---
    // Usamos mutableStateListOf para que o Compose detecte mudanças na lista automaticamente
    private val _posts = mutableStateListOf<Post>(
        Post(1, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(2, "Mural da Vizinhaa", "Integração concluída com sucesso!", R.drawable.mulher)
    )
    val posts: List<Post> = _posts

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
     * Isso preenche a Home antes mesmo da navegação terminar.
     */
    fun setResidentData(data: ResidentResponse) {
        _residentData.value = data
        saveUserLocally(data)
    }

    /**
     * SINCRONIZAÇÃO EM BACKGROUND:
     * Atualiza os dados (como o Score) sem travar a navegação do usuário.
     */
    fun carregarDadosPerfil(token: String, id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authApi.getResidentById(authHeader, id)

                if (response.isSuccessful && response.body() != null) {
                    val dadosDoBanco = response.body()?.resident
                    dadosDoBanco?.let {
                        _residentData.value = it
                        saveUserLocally(it) // Atualiza o cache local com dados novos da API
                        Log.d("API_HOME", "Sincronizado: ${it.name}")
                    }
                }
            } catch (e: Exception) {
                Log.e("API_HOME", "Falha na conexão: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * LOGOUT:
     * Limpa a memória e o disco para que o próximo login não veja dados antigos.
     */
    fun logout() {
        _residentData.value = null
        prefs.edit().remove("saved_user").apply()
    }

    // --- GESTÃO DO MURAL (LOGICA DE POSTS) ---

    fun adicionarPost(titulo: String, descricao: String, uri: Uri?) {
        // Gera um ID simples incremental
        val nextId = if (_posts.isEmpty()) 1 else _posts.maxOf { it.id } + 1

        // Adiciona no topo da lista (índice 0)
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