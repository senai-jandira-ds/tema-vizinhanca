package com.example.mobilevizinhaa.ui.theme.configuraçoes

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobilevizinhaa.ui.theme.GrayBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubPrivacidadeScreen(
    navController: NavController,
    viewModel: ConfiguracoesViewModel // Gerencia o estado do tema, dados e requisições de privacidade
) {
    val context = LocalContext.current
    val token = viewModel.obterTokenSalvo()
    val resident by viewModel.residentData.collectAsState()
    val modoEscuroAtivo by viewModel.isDarkMode.collectAsState()

    // Estados para alteração de Nome
    var novoNome by remember { mutableStateOf("") }

    // SINCRONIZAÇÃO EM TEMPO REAL: Inicializa o campo assim que os dados do servidor chegam
    LaunchedEffect(resident) {
        resident?.name?.let { nameFromServer ->
            if (novoNome.isEmpty()) {
                novoNome = nameFromServer
            }
        }
    }

    // Estados para alteração de Senha
    var senhaAtual by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var confirmarNovaSenha by remember { mutableStateOf("") }

    // Cores dinâmicas perfeitamente adaptadas ao Modo Escuro ativo
    val corFundoTela = if (modoEscuroAtivo) Color(0xFF121212) else GrayBackground
    val corFundoCards = if (modoEscuroAtivo) Color(0xFF1E1E1E) else Color.White
    val corTextoPrincipal = if (modoEscuroAtivo) Color.White else Color.Black
    val corTextoSecundario = if (modoEscuroAtivo) Color.LightGray else Color.Gray

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacidade e Dados", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (modoEscuroAtivo) Color(0xFF1E1E1E) else Color(0xFF3867F5)
                )
            )
        },
        containerColor = corFundoTela
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ====================================================================
            // SEÇÃO 1: ALTERAR NOME DO USUÁRIO (INTEGRADO À CONFIGURAÇÃO)
            // ====================================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = corFundoCards)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Alterar Nome de Usuário",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = corTextoPrincipal
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = novoNome,
                        onValueChange = { novoNome = it },
                        label = { Text("Seu Nome") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3867F5),
                            unfocusedBorderColor = corTextoSecundario,
                            focusedLabelColor = Color(0xFF3867F5),
                            unfocusedLabelColor = corTextoSecundario,
                            focusedTextColor = corTextoPrincipal,
                            unfocusedTextColor = corTextoPrincipal
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (novoNome.isNotBlank()) {
                                // Aciona o fluxo seguro de persistência e evita rejeição do Base64
                                viewModel.atualizarNome(novoNome, token, context)
                            } else {
                                Toast.makeText(context, "O nome não pode estar vazio.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3867F5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Salvar Novo Nome", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ====================================================================
            // SEÇÃO 2: ALTERAR SENHA DA CONTA (CONECTADO AO BACKEND VIA RETROFIT)
            // ====================================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = corFundoCards)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Alterar Senha",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = corTextoPrincipal
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo: Senha Atual
                    OutlinedTextField(
                        value = senhaAtual,
                        onValueChange = { senhaAtual = it },
                        label = { Text("Senha Atual") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3867F5),
                            unfocusedBorderColor = corTextoSecundario,
                            focusedLabelColor = Color(0xFF3867F5),
                            unfocusedLabelColor = corTextoSecundario,
                            focusedTextColor = corTextoPrincipal,
                            unfocusedTextColor = corTextoPrincipal
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo: Nova Senha
                    OutlinedTextField(
                        value = novaSenha,
                        onValueChange = { novaSenha = it },
                        label = { Text("Nova Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3867F5),
                            unfocusedBorderColor = corTextoSecundario,
                            focusedLabelColor = Color(0xFF3867F5),
                            unfocusedLabelColor = corTextoSecundario,
                            focusedTextColor = corTextoPrincipal,
                            unfocusedTextColor = corTextoPrincipal
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo: Confirmar Nova Senha
                    OutlinedTextField(
                        value = confirmarNovaSenha,
                        onValueChange = { confirmarNovaSenha = it },
                        label = { Text("Confirmar Nova Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3867F5),
                            unfocusedBorderColor = corTextoSecundario,
                            focusedLabelColor = Color(0xFF3867F5),
                            unfocusedLabelColor = corTextoSecundario,
                            focusedTextColor = corTextoPrincipal,
                            unfocusedTextColor = corTextoPrincipal
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            when {
                                senhaAtual.isBlank() || novaSenha.isBlank() -> {
                                    Toast.makeText(context, "Preencha todos os campos de senha.", Toast.LENGTH_SHORT).show()
                                }
                                novaSenha != confirmarNovaSenha -> {
                                    Toast.makeText(context, "A nova senha e a confirmação não batem.", Toast.LENGTH_SHORT).show()
                                }
                                novaSenha.length < 6 -> {
                                    Toast.makeText(context, "A nova senha deve ter pelo menos 6 dígitos.", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    // Envia para processamento assíncrono usando a rota unificada via ID do morador
                                    viewModel.alterarSenha(senhaAtual, novaSenha, token, context)
                                    senhaAtual = ""
                                    novaSenha = ""
                                    confirmarNovaSenha = ""
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3867F5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Atualizar Senha", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}