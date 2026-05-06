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
    val uiState by viewModel.uiState.collectAsState()

    // Trocamos o Scaffold por uma Box.
    // A barra agora é controlada APENAS pela MainActivity.
    Box(modifier = Modifier.fillMaxSize().background(GrayBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header
            HomeHeader(uiState.userName, uiState.apartment)

            Spacer(modifier = Modifier.height(30.dp))

            // 2. Cards
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

            Text(
                text = "Postagens",
                modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // 3. Grade de fotos
            PostGridSection()

            // Espaço final para o scroll
            Spacer(modifier = Modifier.height(100.dp))
        }

        // 4. O Botão flutuante (FAB) agora fica aqui, alinhado ao canto
        ExtendedFloatingActionButton(
            onClick = { /* Ação */ },
            containerColor = BluePrimary,
            contentColor = White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Criar postagem", fontWeight = FontWeight.Bold)
        }
    }
}