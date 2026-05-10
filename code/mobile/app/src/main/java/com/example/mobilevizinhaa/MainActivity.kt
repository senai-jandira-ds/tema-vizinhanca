package com.example.mobilevizinhaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Seus imports de UI
import com.example.mobilevizinhaa.ui.theme.home.CustomBottomNavBar
import com.example.mobilevizinhaa.ui.theme.login.LoginScreen
import com.example.mobilevizinhaa.ui.theme.home.HomeScreen
import com.example.mobilevizinhaa.ui.theme.listaitens.PedidosObjetosScreen
import com.example.mobilevizinhaa.ui.theme.mural.MuralScreen
import com.example.mobilevizinhaa.ui.theme.rank.RankingScreen
import com.example.mobilevizinhaa.ui.theme.menssage.MessagesScreen
import com.example.mobilevizinhaa.ui.theme.menssage.chatdetails.ChatDetalheScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//
//    // Observa a rota atual para decidir se mostra a BottomBar
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    Scaffold(
//        bottomBar = {
//            // A regra: Só mostra a barra se NÃO for login E NÃO for detalhe do chat
//            val showBottomBar = currentRoute != null &&
//                    currentRoute != "login" &&
//                    !currentRoute.startsWith("chat_detalhe")
//
//            if (showBottomBar) {
//                CustomBottomNavBar(navController)
//            }
//        }
//    ) { paddingValues ->
//        NavHost(
//            navController = navController,
//            startDestination = "login",
//            modifier = Modifier.padding(paddingValues)
//        ) {
//            // --- TELA DE LOGIN ---
//            composable("login") {
//                LoginScreen(navController = { rota ->
//                    navController.navigate(rota) {
//                        popUpTo("login") { inclusive = true }
//                    }
//                })
//            }
//
//            // --- TELA HOME ---
//            composable("home") {
//                HomeScreen(navController)
//            }
//
//            // --- TELA DE LISTA DE MENSAGENS ---
//            composable("mensagens") {
//                MessagesScreen(navController = navController)
//            }
//
//            // --- TELA DE DETALHE DO CHAT (A que você pediu) ---
//            composable(
//                route = "chat_detalhe/{chatId}",
//                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
//            ) { backStackEntry ->
//                val id = backStackEntry.arguments?.getString("chatId")
//                ChatDetalheScreen(id = id, navController = navController)
//            }
//
//            // --- OUTRAS TELAS ---
//            composable("mural") {
//                MuralScreen()
//            }
//
//            composable("ranking") {
//                RankingScreen()
//            }
//
//            composable("pedido") {
//                PedidosObjetosScreen()
//            }
//
//            composable("objeto") {
//                PedidosObjetosScreen()
//            }
//        }
//    }
//}










@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Observa em qual tela estamos
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Lógica para mostrar a barra inferior:
            // NÃO mostra no Login e NÃO mostra dentro da conversa (ChatDetalhe)
            val showBottomBar = currentRoute != null &&
                    currentRoute != "login" &&
                    !currentRoute.startsWith("chat_detalhe")

            if (showBottomBar) {
                CustomBottomNavBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login", // <--- ALTERADO PARA ABRIR DIRETO NA HOME
            modifier = Modifier.padding(paddingValues)
        ) {
            // --- TELA DE LOGIN ---
            composable("login") {
                LoginScreen(navController = { rota ->
                    navController.navigate(rota) {
                        popUpTo("login") { inclusive = true }
                    }
                })
            }

            // --- TELA HOME ---
            composable("home") {
                HomeScreen(navController)
            }

            // --- TELA DE LISTA DE MENSAGENS (CONTATOS) ---
            composable("mensagens") {
                MessagesScreen(navController = navController)
            }

            // --- TELA DE DETALHE DO CHAT (EX: CONVERSA COM A SARAH) ---
            composable(
                route = "chat_detalhe/{chatId}",
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("chatId")
                ChatDetalheScreen(id = id, navController = navController)
            }

            // --- OUTRAS TELAS ---
            composable("mural") {
                MuralScreen()
            }

            composable("ranking") {
                RankingScreen()
            }

            composable("pedido") {
                PedidosObjetosScreen()
            }

            composable("objeto") {
                PedidosObjetosScreen()
            }
        }
    }
}