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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    // Coleta o estado do usuário (nome, apartamento, contadores)
    val uiState by viewModel.uiState.collectAsState()

    // Lista de posts que vem do ViewModel (agora dinâmica)
    val posts = viewModel.posts

    Box(modifier = Modifier.fillMaxSize().background(GrayBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header (Mantido original)
            HomeHeader(uiState.userName, uiState.apartment)

            Spacer(modifier = Modifier.height(30.dp))

            // 2. Cards de Resumo (Exatamente com o padding de 20dp e espaço de 16dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(
                    title = "Meus pedidos",
                    count = uiState.pedidosCount.toString(),
                    iconRes = R.drawable.pedido
                )
                InfoCard(
                    title = "Meus objetos",
                    count = uiState.objetosCount.toString(),
                    iconRes = R.drawable.objeto
                )
            }

            // Título da Seção
            Text(
                text = "Postagens",
                modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // 3. Grade de fotos (Agora ligada à lista real do ViewModel)
            PostGridSection(posts = posts)

            // Espaçamento para o scroll não bater na BottomBar
            Spacer(modifier = Modifier.height(100.dp))
        }

        // 4. Botão Flutuante (FAB) - Redireciona para PublicacaoScreen
        ExtendedFloatingActionButton(
            onClick = {
                navController.navigate("publicacao")
            },
            containerColor = BluePrimary,
            contentColor = White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 16.dp) // Ajuste fino na posição
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Criar postagem", fontWeight = FontWeight.Bold)
        }
    }
}