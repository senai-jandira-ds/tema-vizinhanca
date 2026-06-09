package com.example.mobilevizinhaa.ui.theme.configuraçoes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobilevizinhaa.ui.theme.GrayBackground
import com.example.mobilevizinhaa.ui.theme.home.HomeViewModel
import com.example.mobilevizinhaa.ui.theme.home.ProfileHeader
import com.example.mobilevizinhaa.ui.theme.home.ProfileOptionMenu

@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: HomeViewModel, // Mantida para atualizar os contadores da Home
    configViewModel: ConfiguracoesViewModel // Gerencia o estado do tema e deslogar de forma síncrona
) {
    val context = LocalContext.current
    val token = configViewModel.obterTokenSalvo()

    // Controle de Estados dos Diálogos Pop-up
    var exibirDialogoTema by remember { mutableStateOf(false) }
    var exibirDialogoAjuda by remember { mutableStateOf(false) }
    var textoAjuda by remember { mutableStateOf("") }

    // Coleta do estado real e persistente do Modo Escuro e dados do perfil vindos da ConfiguracoesViewModel
    val modoEscuroAtivo by configViewModel.isDarkMode.collectAsState()
    val resident by configViewModel.residentData.collectAsState()

    // COLETA DOS CONTADORES REAIS DO VIEWMODEL DA HOME
    val quantidadePedidosReal by viewModel.qtdPedidos.collectAsState()
    val quantidadeObjetosReal by viewModel.qtdObjetos.collectAsState()

    // Força a atualização dos dados do perfil e contadores assim que a tela abre
    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            configViewModel.carregarDadosIniciais(token)
            viewModel.carregarDadosPerfil(token)
        }
    }

    // Mapeamento das opções para o componente de menu
    val listaDeOpcoes = remember {
        listOf(
            Triple(Icons.Default.Palette, "Tema") { exibirDialogoTema = true },
            Triple(Icons.Default.Notifications, "Notificações") { navController.navigate("notificacoes") },
            Triple(Icons.Default.Lock, "Privacidade") {
                try {
                    navController.navigate("sub_privacidade")
                } catch (e: Exception) {
                    navController.navigate("sub_privacidade_screen")
                }
            },
            Triple(Icons.Default.HelpOutline, "Ajuda") { exibirDialogoAjuda = true },
        )
    }

    // Definição dinâmica de cores com base no Modo Escuro ativo
    val corFundoTela = if (modoEscuroAtivo) Color(0xFF121212) else GrayBackground
    val corFundoCards = if (modoEscuroAtivo) Color(0xFF1E1E1E) else Color.White
    val corTextoPrincipal = if (modoEscuroAtivo) Color.White else Color.Black
    val corTextoSecundario = if (modoEscuroAtivo) Color.LightGray else Color.Gray

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(corFundoTela)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // Monta a string do endereço juntando o apartamento e bloco dinamicamente
            val txtApartamento = remember(resident) {
                buildString {
                    append(resident?.apartment ?: "Apartamento")
                    if (resident?.block?.name != null) {
                        append(" - ${resident?.block?.name}")
                    }
                }
            }

            ProfileHeader(
                userName = resident?.name ?: "Vizinho(a)",
                userEmail = resident?.email ?: "usuario@email.com",
                apartment = txtApartamento,
                userPhotoUrl = resident?.photo,
                qtdPedidos = quantidadePedidosReal,
                qtdObjetos = quantidadeObjetosReal,
                qtdMural = resident?.publications?.size?.toString() ?: "0",
                isDarkMode = modoEscuroAtivo,
                onBackClick = { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Menu com as opções de navegação, abertura de pop-ups e Modo Escuro
            ProfileOptionMenu(
                options = listaDeOpcoes,
                isDarkMode = modoEscuroAtivo
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Sair da Conta adaptável ao Modo Escuro usando a configViewModel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = corFundoCards),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { configViewModel.deslogar(navController) }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sair",
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sair da Conta",
                            color = Color(0xFFC62828),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(110.dp))
        }

        // ====================================================================
        // --- DIÁLOGO POP-UP: SELETOR DE TEMA PERSISTENTE VIA CONFIG_VM ---
        // ====================================================================
        if (exibirDialogoTema) {
            AlertDialog(
                onDismissRequest = { exibirDialogoTema = false },
                title = {
                    Text(
                        text = "Escolha o Tema",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = corTextoPrincipal
                    )
                },
                text = {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    configViewModel.alternarTema(false)
                                    exibirDialogoTema = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !modoEscuroAtivo,
                                onClick = {
                                    configViewModel.alternarTema(false)
                                    exibirDialogoTema = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Modo Claro", fontSize = 16.sp, color = corTextoPrincipal)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    configViewModel.alternarTema(true)
                                    exibirDialogoTema = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = modoEscuroAtivo,
                                onClick = {
                                    configViewModel.alternarTema(true)
                                    exibirDialogoTema = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Modo Escuro", fontSize = 16.sp, color = corTextoPrincipal)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { exibirDialogoTema = false }) {
                        Text("Cancelar", color = Color(0xFF3867F5), fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = corFundoCards
            )
        }

        // ====================================================================
        // --- DIÁLOGO POP-UP: AJUDA E CONTATO ---
        // ====================================================================
        if (exibirDialogoAjuda) {
            AlertDialog(
                onDismissRequest = { exibirDialogoAjuda = false },
                title = {
                    Text(
                        text = "Entre em contato:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = corTextoPrincipal
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Condominio@gmail.com",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color(0xFF3867F5)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = textoAjuda,
                            onValueChange = { novoValor -> textoAjuda = novoValor },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 16.sp, color = corTextoPrincipal),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            placeholder = {
                                Text(
                                    text = "Escreva sua mensagem aqui...",
                                    fontSize = 16.sp,
                                    color = corTextoSecundario
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF3867F5),
                                unfocusedBorderColor = corTextoSecundario,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(size = 12.dp)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { exibirDialogoAjuda = false }) {
                        Text("Cancelar", color = corTextoSecundario)
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (textoAjuda.isNotBlank()) {
                                Toast.makeText(context, "Mensagem enviada com sucesso!", Toast.LENGTH_LONG).show()
                                textoAjuda = ""
                                exibirDialogoAjuda = false
                            } else {
                                Toast.makeText(context, "Por favor, escreva uma mensagem.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Enviar", color = Color(0xFF3867F5), fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(size = 20.dp),
                containerColor = corFundoCards
            )
        }
    }
}