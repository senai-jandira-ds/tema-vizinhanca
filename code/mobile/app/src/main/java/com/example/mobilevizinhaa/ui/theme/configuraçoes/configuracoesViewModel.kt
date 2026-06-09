package com.example.mobilevizinhaa.ui.theme.configuraçoes

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mobilevizinhaa.ui.theme.data.ResidentResponse
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.example.mobilevizinhaa.ui.theme.data.UpdateResidentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfiguracoesViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = com.google.gson.Gson()

    // --- ESTADOS DA API (UI STATE) ---
    private val _residentData = MutableStateFlow<ResidentResponse?>(getSavedUserLocal())
    val residentData: StateFlow<ResidentResponse?> = _residentData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- CONFIGURAÇÃO: ESTADO DO MODO ESCURO ---
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        // Carrega as configurações de preferência de tema salvas pelo usuário ao inicializar
        _isDarkMode.value = sharedPreferences.getBoolean("is_dark_mode", false)

        val token = obterTokenSalvo()
        if (token.isNotEmpty()) {
            carregarDadosIniciais(token)
        }
    }

    // --- CACHE LOCAL: LER CREDENCIAIS ---
    fun obterTokenSalvo(): String {
        val token = sharedPreferences.getString("auth_token", "") ?: ""
        return if (token.isNotEmpty() && !token.startsWith("Bearer ")) "Bearer $token" else token
    }

    // --- PERSISTÊNCIA LOCAL: BUSCAR CACHE ---
    private fun getSavedUserLocal(): ResidentResponse? {
        val json = sharedPreferences.getString("saved_user", null)
        return if (!json.isNullOrBlank()) {
            try {
                gson.fromJson(json, ResidentResponse::class.java)
            } catch (e: Exception) {
                Log.e("CONFIG_VM", "Erro ao converter JSON do cache local: ${e.message}")
                null
            }
        } else null
    }

    // --- CARREGAR DADOS DO PERFIL ATUAL ---
    fun carregarDadosIniciais(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authApi.getResidentById(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val dados = response.body()?.response
                    dados?.let {
                        _residentData.value = it
                        // Atualiza o cache local
                        sharedPreferences.edit().putString("saved_user", gson.toJson(it)).apply()
                    }
                }
            } catch (e: Exception) {
                Log.e("CONFIG_VM", "Erro ao carregar dados iniciais: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- FLUXO CONFIGURAÇÃO: ALTERNAR TEMA (DARK/LIGHT) ---
    fun alternarTema(ativado: Boolean) {
        _isDarkMode.value = ativado
        sharedPreferences.edit().putBoolean("is_dark_mode", ativado).apply()
    }

    // ====================================================================
    // 🎯 FLUXO PRIVACIDADE: ATUALIZAR NOME (VIA PUT /api/v1/resident/{id})
    // ====================================================================
    fun atualizarNome(novoNome: String, token: String, context: Context) {
        if (token.isEmpty()) {
            Toast.makeText(context, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // Garante dados atualizados resgatando do estado ou lendo o cache em último caso
                var dadosAtuais = _residentData.value ?: getSavedUserLocal()
                if (dadosAtuais == null) {
                    Log.d("UPDATE_NAME_DEBUG", "Dados locais nulos, buscando do servidor...")
                    val profileRes = RetrofitClient.authApi.getResidentById(authHeader)
                    if (profileRes.isSuccessful) {
                        dadosAtuais = profileRes.body()?.response
                    }
                }

                if (dadosAtuais == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Erro: Não foi possível obter os dados atuais do morador.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Normalização da string da foto para evitar falhas no backend
                val fotoValida = if (!dadosAtuais.photo.isNullOrEmpty() && dadosAtuais.photo != "string") {
                    dadosAtuais.photo
                } else {
                    "string"
                }

                // Tratamento seguro do ID do Bloco para não repassar valores inválidos
                val blocoIdValido = dadosAtuais.block?.id
                val idBlockFinal = if (blocoIdValido != null && blocoIdValido > 0) blocoIdValido else null

                // 🎯 MONTANTE DE PAYLOAD SEGURO DE ACORDO COM AS PROPRIEDADES NULLABLES DO AUTH_API_SERVICE
                val request = UpdateResidentRequest(
                    photo = fotoValida,
                    name = novoNome.ifBlank { dadosAtuais.name ?: "Morador" },
                    apartment = dadosAtuais.apartment?.ifBlank { "0" } ?: "0",
                    idBlock = idBlockFinal,
                    cpf = dadosAtuais.cpf?.ifBlank { "00000000000" } ?: "00000000000",
                    email = dadosAtuais.email,
                    phone = dadosAtuais.phone?.ifBlank { "000000000" } ?: "000000000",
                    score = dadosAtuais.score,
                    is_active = dadosAtuais.is_active,
                    password = null
                )

                Log.d("UPDATE_NAME_DEBUG", "Disparando API updateResident para o ID: ${dadosAtuais.id}")
                val response = RetrofitClient.authApi.updateResident(authHeader, dadosAtuais.id, request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val novoMorador = response.body()?.response
                        _residentData.value = novoMorador

                        // Sincroniza o cache local
                        sharedPreferences.edit().putString("saved_user", gson.toJson(novoMorador)).apply()
                        Toast.makeText(context, "Nome atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        val erroCorpo = response.errorBody()?.string() ?: ""
                        Log.e("UPDATE_NAME_DEBUG", "Erro HTTP: ${response.code()} - $erroCorpo")
                        Toast.makeText(context, "Servidor recusou a alteração. Verifique os dados.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UPDATE_NAME_DEBUG", "Falha de rede/exceção: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro de conexão ao atualizar nome.", Toast.LENGTH_SHORT).show()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ====================================================================
    // 🎯 FLUXO PRIVACIDADE: ALTERAR SENHA REENCAMINHADO PARA O ENDPOINT CORRETO
    // ====================================================================
    fun alterarSenha(senhaAtual: String, novaSenha: String, token: String, context: Context) {
        if (token.isEmpty()) {
            Toast.makeText(context, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

                var dadosAtuais = _residentData.value ?: getSavedUserLocal()
                if (dadosAtuais == null) {
                    val profileRes = RetrofitClient.authApi.getResidentById(authHeader)
                    if (profileRes.isSuccessful) {
                        dadosAtuais = profileRes.body()?.response
                    }
                }

                if (dadosAtuais == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Dados do usuário não carregados localmente.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val fotoValida = if (!dadosAtuais.photo.isNullOrEmpty() && dadosAtuais.photo != "string") {
                    dadosAtuais.photo
                } else {
                    "string"
                }

                val blocoIdValido = dadosAtuais.block?.id
                val idBlockFinal = if (blocoIdValido != null && blocoIdValido > 0) blocoIdValido else null

                // 🎯 ATUALIZADO: Cria o payload injetando a nova senha dentro do UpdateResidentRequest usando tipos flexíveis
                val request = UpdateResidentRequest(
                    photo = fotoValida,
                    name = dadosAtuais.name?.ifBlank { "Morador" } ?: "Morador",
                    apartment = dadosAtuais.apartment?.ifBlank { "0" } ?: "0",
                    idBlock = idBlockFinal,
                    cpf = dadosAtuais.cpf?.ifBlank { "00000000000" } ?: "00000000000",
                    email = dadosAtuais.email,
                    phone = dadosAtuais.phone?.ifBlank { "000000000" } ?: "000000000",
                    score = dadosAtuais.score,
                    is_active = dadosAtuais.is_active,
                    password = novaSenha
                )

                val response = RetrofitClient.authApi.updateResident(authHeader, dadosAtuais.id, request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        val erroCorpo = response.errorBody()?.string() ?: ""
                        Log.e("ALTERAR_SENHA_DEBUG", "Erro HTTP: ${response.code()} - $erroCorpo")
                        Toast.makeText(context, "Erro do servidor ao atualizar senha.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("CONFIG_VM", "Erro ao alterar senha: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro de conexão ao alterar senha.", Toast.LENGTH_SHORT).show()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- FLUXO CONFIGURAÇÃO: LOGOUT SEGURO DA CONTA ---
    fun deslogar(navController: NavController) {
        viewModelScope.launch {
            _isLoading.value = true
            sharedPreferences.edit().clear().apply()
            _residentData.value = null
            _isLoading.value = false

            withContext(Dispatchers.Main) {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}