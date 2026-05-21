package com.example.mobilevizinhaa.ui.theme.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel // Recebe a instância única vinda da MainActivity
) {
    // Observa os dados. Como o ViewModel inicia lendo o disco,
    // 'resident' já terá valor se o usuário estiver logado.
    val resident by viewModel.residentData.collectAsState()
    val posts = viewModel.posts

    // GATILHO DE ATUALIZAÇÃO AUTOMÁTICA (CORRIGIDO PARA O ENDPOINT /me/resident):
    // Dispara em background assim que a tela abre, buscando tudo baseado puramente no Token ativo
    LaunchedEffect(Unit) {
        val tokenSalvo = viewModel.obterTokenSalvo()
        if (tokenSalvo.isNotEmpty()) {
            viewModel.carregarDadosPerfil(tokenSalvo)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground) // Certifique-se que GrayBackground está definido em seu Color.kt
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // HEADER INSTANTÂNEO CORRIGIDO:
            // Passamos 'userPhotoUrl' recebendo a string crua. Sumirá o erro do Android Studio!
            HomeHeader(
                userName = resident?.name ?: "Vizinho(a)",
                apartment = resident?.apartment?.let { "Apto $it" } ?: "Condomínio",
                userPhotoUrl = resident?.photo
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Seção de Cards de Atalho com o parâmetro de clique configurado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(
                    titulo = "Meus pedidos",
                    quantity = "3", // Nome do parâmetro ajustado para bater com o HomeComponents
                    iconeRes = R.drawable.pedido,
                    onAddClick = {
                        navController.navigate("criar_pedido") // <--- Abre a tela de cadastro para pedidos
                    }
                )
                InfoCard(
                    titulo = "Meus objetos",
                    quantity = "3", // Nome do parâmetro ajustado para bater com o HomeComponents
                    iconeRes = R.drawable.objeto,
                    onAddClick = {
                        navController.navigate("criar_pedido") // <--- Abre a tela de cadastro para objetos
                    }
                )
            }

            Text(
                text = "Postagens",
                modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Grade de fotos do Mural (Chama o componente que agora renderiza as URLs do Render)
            PostGridSection(posts = posts) { postId ->
                navController.navigate("detalhe_post/$postId")
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Botão Flutuante (FAB) para criar postagem
        ExtendedFloatingActionButton(
            onClick = { navController.navigate("publicacao") },
            containerColor = BluePrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 16.dp),
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Criar postagem", fontWeight = FontWeight.Bold)
        }
    }
}