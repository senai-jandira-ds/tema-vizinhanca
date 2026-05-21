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

    // Instância do Contexto obtida com segurança através do AndroidViewModel
    private val context = application.applicationContext

    // Gerenciador de armazenamento local (SharedPreferences)
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // --- ESTADO DO USUÁRIO ---
    // Inicia carregando do disco. Se o usuário já logou antes, os dados aparecem no 1º frame.
    private val _residentData = MutableStateFlow<ResidentResponse?>(getSavedUser())
    val residentData = _residentData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // --- CONTROLE DE FLUXO EM TEMPO REAL ---
    private val _postCriadoComSucesso = MutableStateFlow(false)
    val postCriadoComSucesso = _postCriadoComSucesso.asStateFlow()

    // --- LISTA DE POSTAGENS REATIVAS ---
    private val _posts = mutableStateListOf<Post>()
    val posts: List<Post> = _posts

    // --- FUNÇÃO AUXILIAR PARA PEGAR O TOKEN SALVO ---
    fun obterTokenSalvo(): String = prefs.getString("auth_token", "") ?: ""

    // --- PERSISTÊNCIA LOCAL (PRIVATE) ---

    private fun saveUserLocally(user: ResidentResponse) {
        try {
            // Converte todo o objeto (incluindo o novo formato do Bloco e fotos) em JSON String
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
                // Converte o JSON de volta para o objeto ResidentResponse nativo
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
                        // LOGS DE DEBUG COMPLETO PARA INVESTIGAÇÃO NO LOGCAT:
                        Log.d("TESTE_API", "NOME DO BANCO: ${it.name}")
                        Log.d("TESTE_API", "QUANTIDADE DE POSTS RETORNADOS: ${it.publications?.size}")

                        _residentData.value = it
                        saveUserLocally(it) // Atualiza o cache local em SharedPreferences

                        // MAPEAMENTO DOS POSTS DA API PARA A TELA:
                        atualizarListaDePosts(it)
                    }
                } else {
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
        _posts.clear() // Limpa os elementos antigos da UI para evitar duplicados ao recarregar
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
     * SALVAR NO BANCO DE DADOS DA API (INTEGRADO AO SWAGGER E COM ATUALIZAÇÃO EM TEMPO REAL):
     * Recebe os dados brutos e a Uri local da imagem. Comprime a foto em background para evitar erros
     * de payload no Render, envia para a API e força a atualização do feed da Home instantaneamente.
     */
    fun adicionarPostNoBanco(titulo: String, descricao: String, uriImagem: Uri?) {
        val token = obterTokenSalvo()

        if (token.isEmpty()) {
            Log.e("API_HOME", "Erro: Token ausente. Usuário não autenticado para postar.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true // Ativa o loading durante a inserção
            _postCriadoComSucesso.value = false // Reseta o estado anterior de sucesso
            try {
                // 1. Processa e otimiza a imagem em Base64 de maneira assíncrona
                val fotoBase64 = if (uriImagem != null) {
                    otimizarEConverterParaBase64(uriImagem)
                } else null

                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // 2. Cria o request estruturado idêntico ao exigido pelo Swagger
                val request = CreatePostRequest(
                    title = titulo,
                    description = descricao,
                    photo = fotoBase64
                )

                // 3. Envia os dados para a API
                val response = RetrofitClient.authApi.criarPublicacao(authHeader, request)

                if (response.isSuccessful) {
                    Log.d("API_HOME", "Post inserido no banco com sucesso!")

                    // 4. Sincronização em tempo real: Força o feed a atualizar as publicações na hora
                    carregarDadosPerfil(token)

                    // Ativa a flag de sucesso para a View fechar no momento correto
                    _postCriadoComSucesso.value = true
                } else {
                    Log.e("API_HOME", "Servidor recusou a postagem. Código: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API_HOME", "Falha de rede ao tentar salvar publicação: ${e.message}")
            } finally {
                _isLoading.value = false // Desativa o loading independente do resultado
            }
        }
    }

    /**
     * COMPRESSÃO E REDIMENSIONAMENTO INTERNO DE IMAGENS:
     * Reduz a escala da imagem para no máximo 800px e aplica 75% de compressão JPEG.
     * Retorna os bytes limpos em string Base64 sem prefixo HTTP (compatibilidade total com Swagger).
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

                // Comprime a imagem reduzindo drasticamente o peso físico em bytes
                bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
                val bytesComprimidos = outputStream.toByteArray()

                // Retorna apenas a String limpa que a maioria dos Swaggers exige (sem metadados)
                Base64.encodeToString(bytesComprimidos, Base64.DEFAULT)
                    .trim()
                    .replace("\n", "")
                    .replace("\r", "")
            } else null
        } catch (e: Exception) {
            Log.e("IMAGE_OPTIMIZER", "Erro ao otimizar imagem para Base64: ${e.message}")
            null
        }
    }

    /**
     * Reseta o estado de sucesso para permitir novas postagens sem fechar a tela direto.
     */
    fun resetarEstadoSucesso() {
        _postCriadoComSucesso.value = false
    }

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

    // --- FUNÇÃO DE LOGOUT CORRIGIDA NO LUGAR CERTO ---
    fun deslogar(navController: NavController) {
        viewModelScope.launch {
            try {
                // 1. Limpa o token e dados salvos no SharedPreferences
                prefs.edit().clear().apply()

                // 2. Reseta o estado do usuário e os posts locais
                _residentData.value = null
                _posts.clear()

                // 3. Redireciona para o login e limpa a pilha de telas
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            } catch (e: Exception) {
                Log.e("HOME_VIEWMODEL", "Erro ao deslogar: ${e.message}")
            }
        }
    }
} // <- FIM DA CLASSE (Esta chave fecha o arquivo corretamente)