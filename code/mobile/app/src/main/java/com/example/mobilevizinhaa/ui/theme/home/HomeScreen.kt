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
            // HEADER INSTANTÂNEO:
            //userName e apartment pegos diretamente do Flow que lê o cache local
            HomeHeader(
                userName = resident?.name ?: "Vizinho(a)",
                apartment = resident?.apartment?.let { "Apto $it" } ?: "Condomínio"
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Seção de Cards de Atalho
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(
                    titulo = "Meus pedidos",
                    quantidade = "3",
                    iconeRes = R.drawable.pedido
                )
                InfoCard(
                    titulo = "Meus objetos",
                    quantidade = "3",
                    iconeRes = R.drawable.objeto
                )
            }

            Text(
                text = "Postagens",
                modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Grade de fotos do Mural (Lê a lista do ViewModel)
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