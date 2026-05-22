package com.example.mobilevizinhaa.ui.theme.`configuraçoes`

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    viewModel: HomeViewModel
) {
    // 1. Puxa o token que o login guardou no SharedPreferences
    val token = viewModel.obterTokenSalvo()

    // 2. Dispara a busca automática assim que a tela abre
    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            viewModel.carregarDadosPerfil(token)
        }
    }

    // 3. Coleta o estado de maneira estável
    val resident by viewModel.residentData.collectAsState()

    val listaDeOpcoes = listOf(
        Triple(Icons.Default.Settings, "Configurações") { navController.navigate("sub_configuracoes") },
        Triple(Icons.Default.Notifications, "Notificações") { navController.navigate("sub_notificacoes") },
        Triple(Icons.Default.Lock, "Privacidade") { navController.navigate("sub_privacidade") },
        Triple(Icons.Default.HelpOutline, "Ajuda") { navController.navigate("sub_ajuda") }
    )

    // CORREÇÃO: Removemos o Scaffold interno. A Box principal preenche o espaço
    // respeitando o recuo inferior controlado pela MainActivity.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
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
    }
}