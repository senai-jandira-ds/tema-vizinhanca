package com.example.mobilevizinhaa.ui.theme.idea

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobilevizinhaa.ui.theme.BluePrimary
import com.example.mobilevizinhaa.ui.theme.home.HomeViewModel

@Composable
fun LoadingScreen(
    token: String,
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    // Observa quando os dados do banco chegarem
    val resident by homeViewModel.residentData.collectAsState()

    // Dispara a busca assim que a tela abre passando apenas o Token (Sincronizado com o Swagger)
    LaunchedEffect(Unit) {
        homeViewModel.carregarDadosPerfil(token)
    }

    // Monitora o dado: quando deixar de ser nulo (banco respondeu), vai para a Home
    LaunchedEffect(resident) {
        if (resident != null) {
            navController.navigate("home") {
                popUpTo("loading") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = BluePrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Carregando perfil...", color = Color.Gray, fontSize = 14.sp)
        }
    }
}