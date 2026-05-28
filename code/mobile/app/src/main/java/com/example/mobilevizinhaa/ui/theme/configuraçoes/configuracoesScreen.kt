package com.example.mobilevizinhaa.ui.theme.configuraçoes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.GrayBackground
import com.example.mobilevizinhaa.ui.theme.home.HomeViewModel
import com.example.mobilevizinhaa.ui.theme.home.ProfileHeader
import com.example.mobilevizinhaa.ui.theme.home.ProfileOptionMenu

@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    // 1. Puxa o token que o login guardou no SharedPreferences
    val token = viewModel.obterTokenSalvo()

    // Controle do Estado para abrir/fechar o Diálogo de Seleção de Tema
    var exibirDialogoTema by remember { mutableStateOf(false) }

    var exibirDiagoloAjuda by remember {mutableStateOf(false) }

    // INTEGRADO: Coleta o estado real e persistente do modo escuro diretamente do seu ViewModel
    val modoEscuroAtivo by viewModel.isDarkMode.collectAsState()

    // 2. Dispara a busca automática assim que a tela abre
    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            viewModel.carregarDadosPerfil(token)
        }
    }

    // 3. Coleta o estado de maneira estável
    val resident by viewModel.residentData.collectAsState()

    // Ao clicar em "Tema", abre o pop-up interno gerenciado pelo estado acima
    val listaDeOpcoes = listOf(
        Triple(Icons.Default.Palette, "Tema") { exibirDialogoTema = true },
        Triple(Icons.Default.Notifications, "Notificações") { navController.navigate("notificacoes") },
        Triple(Icons.Default.Lock, "Privacidade") { navController.navigate("sub_privacidade") },
        Triple(Icons.Default.HelpOutline, "Ajuda") { exibirDiagoloAjuda = true },
    )

    // A Box altera sua cor de fundo dinamicamente baseada no tema salvo no ViewModel
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (modoEscuroAtivo) Color(0xFF121212) else GrayBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // Monta com segurança a String juntando o Apto e o Bloco vindo do banco
            val txtApartamento = buildString {
                append(resident?.apartment ?: "Apartamento")
                if (resident?.block?.name != null) {
                    append(" - ${resident?.block?.name}")
                }
            }

            // Header Dinâmico mapeado perfeitamente para as suas variáveis
            ProfileHeader(
                userName = resident?.name ?: "Vizinho(a)",
                userEmail = resident?.email ?: "usuario@email.com",
                apartment = txtApartamento
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileOptionMenu(options = listaDeOpcoes)

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Sair da Conta conectado ao deslogar nativo do seu código
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (modoEscuroAtivo) Color(0xFF1E1E1E) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.deslogar(navController) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
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
            // Um spacer ligeiramente maior no fim para o conteúdo não morrer colado na barra inferior
            Spacer(modifier = Modifier.height(110.dp))
        }

        // --- DIÁLOGO POP-UP SELETOR DE TEMA PERSISTENTE ---
        if (exibirDialogoTema) {
            AlertDialog(
                onDismissRequest = { exibirDialogoTema = false },
                title = {
                    Text(
                        text = "Escolha o Tema",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                text = {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !modoEscuroAtivo,
                                onClick = {
                                    viewModel.alternarTema(false) // Grava permanentemente "false" (Modo Claro) nas preferências do App
                                    exibirDialogoTema = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Modo Claro", fontSize = 16.sp, color = Color.Black)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = modoEscuroAtivo,
                                onClick = {
                                    viewModel.alternarTema(true) // Grava permanentemente "true" (Modo Escuro) nas preferências do App
                                    exibirDialogoTema = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Modo Escuro", fontSize = 16.sp, color = Color.Black)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { exibirDialogoTema = false }) {
                        Text("Cancelar", color = Color(0xFF42A5F5), fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }

        var ajuda by remember {
            mutableStateOf("")
        }

        var context = LocalContext.current


        if (exibirDiagoloAjuda){
            AlertDialog(
                onDismissRequest = {exibirDiagoloAjuda = true},
                title = {
                    Column(modifier = Modifier .fillMaxWidth() ,

                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Entre em contato:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier .height(20.dp))

                        Text(
                            text = "Condominio@gmail.com",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier .height(10.dp))

                        OutlinedTextField( modifier = Modifier ,
                            value = ajuda,
                            onValueChange = { novoValor ->
                                ajuda = novoValor
                            },
                            textStyle  = TextStyle (
                                fontSize = 16.sp
                            ),

                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            placeholder = {Text(text = "Escreva aqui:",
                                fontSize = 16.sp

                            )},

                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Blue
                            ),
                            shape = RoundedCornerShape(size = 16.dp)


                        )

                    }


                },
                text = {
                    Column(modifier = Modifier
                        .padding(16.dp)
                        .background(Color(102, 59, 176, 255))
                    ) {

                    }
                },
                dismissButton = {
                    TextButton( onClick = {exibirDiagoloAjuda = false}) {
                        Text("Cancelar")
                    }
                },

                confirmButton = {
                    TextButton( onClick = {
                        if (ajuda.isNotBlank()) {
                            Toast.makeText(context, "Mensagem enviada!", Toast.LENGTH_LONG).show()
                            ajuda = ""
                        }
                    }
                    ) {
                        Text("Enviar")
                    }
                },
                shape = RoundedCornerShape(size = 16.dp),
                containerColor = Color.White


            )
        }
    }
}