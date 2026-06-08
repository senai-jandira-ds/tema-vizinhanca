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
import com.example.mobilevizinhaa.ui.theme.data.SingleResidentResponse
import com.google.gson.Gson
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
    val imagemUrl: String? = null // Recebe o link final em nuvem gerado pela API ou Base64
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Instância do Contexto obtida de forma segura através da herança de AndroidViewModel
    private val context = application.applicationContext

    // Gerenciador de armazenamento local para persistência de tokens e perfil em cache
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // --- NOVO: GERENCIAMENTO DE TEMA PERSISTENTE ---
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

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

    // Inicializador da classe - Carrega os dados persistidos logo ao instanciar o ViewModel
    init {
        // Carrega o tema salvo no mesmo arquivo de preferências do usuário
        _isDarkMode.value = prefs.getBoolean("is_dark_mode", false)
    }

    // --- NOVO: MÉTODO PARA ALTERNAR E SALVAR O TEMA LOCALMENTE ---
    fun alternarTema(modoEscuro: Boolean) {
        _isDarkMode.value = modoEscuro
        prefs.edit().putBoolean("is_dark_mode", modoEscuro).apply()
    }

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
                        Log.d(
                            "TESTE_API",
                            "Quantidade de postagens no perfil: ${it.publications?.size ?: 0}"
                        )

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
                    descricao = pub.description,
                    imagemUrl = pub.photo
                )
            )
        }
    }

    /**
     * ATUALIZAR FOTO DE PERFIL DO USUÁRIO (AJUSTADO PARA FORMATO COMPATÍVEL COM O BACKEND)
     * Converte a imagem capturada da galeria local para uma String Base64 pura,
     * monta o objeto UpdateResidentRequest mesclando os dados locais existentes e envia
     * para a rota oficial de atualização cadastral via PUT.
     */
    fun atualizarFotoPerfil(context: Context, uriImagem: Uri) {
        val token = obterTokenSalvo()
        val usuarioAtual = _residentData.value

        if (token.isEmpty()) {
            Log.e("API_PROFILE", "Operação abortada: Token de autenticação inexistente.")
            return
        }

        if (usuarioAtual == null) {
            Log.e("API_PROFILE", "Operação abortada: Dados cadastrais locais do residente não carregados.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // 1. Processa e otimiza a foto de perfil em background de forma segura
                val fotoBase64Pura = obterStringBase64Otimizada(uriImagem)

                if (fotoBase64Pura.isNotEmpty()) {
                    // 2. Cria o Payload completo usando a String Pura (sem o cabeçalho data:image)
                    val requestBody = UpdateResidentRequest(
                        id = usuarioAtual.id,
                        name = usuarioAtual.name ?: "",
                        email = usuarioAtual.email,
                        apartment = usuarioAtual.apartment,
                        block = usuarioAtual.block,
                        phone = usuarioAtual.phone,
                        photo = fotoBase64Pura
                    )

                    Log.d("API_PROFILE", "Iniciando requisição PUT. Tamanho da string de imagem: ${fotoBase64Pura.length}")

                    // 3. Dispara a requisição para o método oficial de atualização (updateResident)
                    val response = RetrofitClient.authApi.updateResident(
                        token = authHeader,
                        request = requestBody
                    )

                    if (response.isSuccessful && response.body() != null) {
                        Log.d("API_PROFILE", "✅ SUCESSO: Foto de perfil persistida e salva no banco de dados!")

                        // Extrai o morador atualizado diretamente de dentro do envelope de resposta
                        val moradorAtualizadoPeloBanco = response.body()?.resident

                        moradorAtualizadoPeloBanco?.let { novoPerfil ->
                            // Atualiza o estado da tela em tempo real
                            _residentData.value = novoPerfil

                            // Salva no SharedPreferences na hora para que o dado persista ao recarregar a página
                            saveUserLocally(novoPerfil)

                            // Atualiza os posts se necessário
                            atualizarListaDePosts(novoPerfil)
                        }
                    } else {
                        val erroCorpo = response.errorBody()?.string()
                        Log.e("API_PROFILE", "❌ ERRO AO SALVAR IMAGEM [Código ${response.code()}]: $erroCorpo")
                    }
                }
            } catch (e: Exception) {
                Log.e("API_PROFILE", "❌ FALHA CRÍTICA DE COMUNICAÇÃO COM O SERVIDOR: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * SALVAR NOVA POSTAGEM NA CONTA DO RESIDENTE (JSON / BASE64)
     * Pega os dados estruturados da galeria, converte e reduz para uma String Base64 limpa
     * e dispara diretamente como Body em JSON para a API.
     */
    fun adicionarPostNoBanco(titulo: String, descricao: String, uriImagem: Uri?) {
        val token = obterTokenSalvo()

        if (token.isEmpty()) {
            Log.e(
                "API_HOME",
                "Operação abortada: Token de autenticação inexistente no SharedPreferences."
            )
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _postCriadoComSucesso.value = false
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // 1. Processa a imagem da galeria em background e transforma em string Base64 compacta
                val fotoBase64 = obterStringBase64Otimizada(uriImagem)

                // 2. Cria o Objeto de Request compatível com o JSON exigido no Swagger
                val requestBody = CreatePostRequest(
                    title = titulo,
                    description = descricao,
                    photoBase64 = fotoBase64
                )

                // 3. Dispara a requisição POST limpa (sem anotações Multipart)
                val response = RetrofitClient.authApi.criarPublicacao(
                    token = authHeader,
                    request = requestBody
                )

                if (response.isSuccessful) {
                    Log.d("API_HOME", "Postagem criada com sucesso via JSON no banco de dados!")

                    // Força o aplicativo a buscar a nova lista atualizada e atualizar a Home instantaneamente
                    carregarDadosPerfil(token)
                    _postCriadoComSucesso.value = true
                } else {
                    Log.e(
                        "API_HOME",
                        "Servidor rejeitou a publicação: ${response.code()} - ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    "API_HOME",
                    "Falha crítica de rede ao tentar salvar postagem via JSON: ${e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * EXCLUIR POSTAGEM INTEGRADA COM O BANCO DE DADOS (DELETE)
     * Dispara a deleção para o servidor usando a API do Retrofit com tratamento assíncrono.
     * Caso o backend retorne sucesso, remove localmente da UI e executa o callback de retorno.
     */
    fun deletarPost(postId: Int, onSuccess: () -> Unit = {}) {
        val token = obterTokenSalvo()
        if (token.isEmpty()) {
            Log.e("API_DELETE", "Erro: Token de autenticação não encontrado.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // Dispara a chamada para o endpoint de exclusão configurado no seu RetrofitClient
                val response = RetrofitClient.authApi.deletarPublicacao(authHeader, postId)

                if (response.isSuccessful) {
                    Log.d("API_DELETE", "✅ Sucesso: Postagem de ID $postId deletada do banco de dados.")

                    // Remove do estado reativo do Compose de forma síncrona na Main Thread
                    _posts.removeAll { it.id == postId }

                    // Retorna para a tela anterior através da navegação
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    Log.e("API_DELETE", "❌ Servidor retornou erro ao deletar: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API_DELETE", "❌ Erro de rede ou comunicação ao deletar publicação: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * OTIMIZAÇÃO DA IMAGEM DA GALERIA E CONVERSÃO PARA BASE64
     * Forçado a rodar estritamente sob a thread Dispatchers.IO para evitar congelamento de UI
     * e jank de frames detectados no Logcat do aparelho. Teto fixado em 500px.
     */
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

                val bitmapRedimensionado =
                    Bitmap.createScaledBitmap(bitmapOriginal, larguraFinal, alturaFinal, true)

                val outputStream = ByteArrayOutputStream()
                bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
                val bytes = outputStream.toByteArray()
                outputStream.close()

                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else ""
        } catch (e: Exception) {
            Log.e(
                "IMAGE_PROCESSOR",
                "Falha ao processar e extrair Base64 da imagem da galeria: ${e.message}"
            )
            ""
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
        _posts.add(
            0, Post(
                id = nextId,
                titulo = titulo,
                descricao = descricao,
                imagemUri = uri
            )
        )
    }

    /**
     * DECODIFICAÇÃO DE BASE64 PARA RENDERIZAÇÃO LOCAL NA UI
     * Esta mesma função serve de forma gêmea tanto para as publicações do mural quanto para a foto de perfil.
     */
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
            Log.e("IMAGE_DECODER", "Erro ao decodificar string Base64: ${e.message}")
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