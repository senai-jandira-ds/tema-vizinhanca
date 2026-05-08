package com.example.mobilevizinhaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.mobilevizinhaa.ui.theme.home.CustomBottomNavBar
import com.example.mobilevizinhaa.ui.theme.login.LoginScreen
import com.example.mobilevizinhaa.ui.theme.home.HomeScreen
import com.example.mobilevizinhaa.ui.theme.listaitens.PedidosObjetosScreen
import com.example.mobilevizinhaa.ui.theme.mural.MuralScreen
import com.example.mobilevizinhaa.ui.theme.rank.RankingScreen
import com.example.mobilevizinhaa.ui.theme.menssage.MessagesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // A barra inferior só aparece se não estivermos na tela de login
            if (currentRoute != "login") {
                CustomBottomNavBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login", // O app sempre começa pedindo login
            modifier = Modifier.padding(paddingValues)
        ) {
            // TELA DE LOGIN
            composable("login") {
                LoginScreen(navController = { rota ->
                    navController.navigate(rota) {
                        // Limpa o histórico para o usuário não conseguir "voltar" pro login logado
                        popUpTo("login") { inclusive = true }
                    }
                })
            }

            // TELA HOME (Após sucesso da API)
            composable("home") {
                HomeScreen(navController)
            }

            // TELA DE MENSAGENS (Onde você vai listar quem está registrado)
            composable("mensagens") {
                MessagesScreen(navController = navController)
            }

            composable("pedido") {
                PedidosObjetosScreen()
            }

            composable("objeto") {
                PedidosObjetosScreen()
            }

            composable("mural") {
                MuralScreen()
            }

            composable("ranking") {
                RankingScreen()
            }
        }
    }
}