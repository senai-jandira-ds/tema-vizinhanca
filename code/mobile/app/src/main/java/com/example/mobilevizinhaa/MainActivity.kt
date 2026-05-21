package com.example.mobilevizinhaa

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Imports de UI e Lógica
import com.example.mobilevizinhaa.ui.theme.home.HomeViewModel
import com.example.mobilevizinhaa.ui.theme.home.CustomBottomNavBar
import com.example.mobilevizinhaa.ui.theme.login.LoginScreen
import com.example.mobilevizinhaa.ui.theme.home.HomeScreen
import com.example.mobilevizinhaa.ui.theme.listaitens.PedidosObjetosScreen
import com.example.mobilevizinhaa.ui.theme.mural.MuralScreen
import com.example.mobilevizinhaa.ui.theme.rank.RankingScreen
import com.example.mobilevizinhaa.ui.theme.menssage.MessagesScreen
import com.example.mobilevizinhaa.ui.theme.menssage.chatdetails.ChatDetalheScreen
import com.example.mobilevizinhaa.ui.theme.notification.NotificationsScreen
import com.example.mobilevizinhaa.ui.theme.home.createpost.createpost.PublicacaoScreen
import com.example.mobilevizinhaa.ui.theme.home.detail.DetalhePostagemScreen
import com.example.mobilevizinhaa.ui.theme.listaitens.criar.CriarPedidoObjetoScreen

// ADICIONADO: Import da sua tela de Perfil / Configurações
import com.example.mobilevizinhaa.ui.theme.`configuraçoes`.PerfilScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ativa o comportamento transparente de ponta a ponta (Edge-to-Edge) nativo
        enableEdgeToEdge()

        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val homeViewModel: HomeViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define quais telas NÃO devem mostrar a BottomBar do app
    val esconderBottomBar = currentRoute == null ||
            currentRoute == "login" ||
            currentRoute == "publicacao" ||
            currentRoute == "criar_pedido" ||
            currentRoute.startsWith("chat_detalhe") ||
            currentRoute.startsWith("detalhe_post")

    Scaffold(
        // CORREÇÃO 1: Definimos os insets como vazios no Scaffold principal.
        // Isso permite que o conteúdo do NavHost decida se vai ou não invadir a StatusBar.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (!esconderBottomBar) {
                CustomBottomNavBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier
                // CORREÇÃO 2: Aplicamos apenas o padding da BottomBar de forma inteligente.
                // Evita que as telas de formulário fiquem com um buraco branco no topo.
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {

            // --- TELA DE LOGIN ---
            composable("login") {
                LoginScreen(
                    navController = { rota ->
                        navController.navigate(rota) {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    homeViewModel = homeViewModel
                )
            }

            // --- TELA HOME (Seu Header azul vai flutuar perfeitamente abaixo dos ícones da barra) ---
            composable("home") {
                HomeScreen(
                    navController = navController,
                    viewModel = homeViewModel
                )
            }

            // --- CRIAÇÃO DE POSTAGEM ---
            composable("publicacao") {
                PublicacaoScreen(navController, homeViewModel)
            }

            // --- NOVA TELA: CRIAÇÃO DE PEDIDO OU OBJETO ---
            composable("criar_pedido") {
                CriarPedidoObjetoScreen(navController = navController)
            }

            // --- DETALHE DA POSTAGEM ---
            composable(
                route = "detalhe_post/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("postId") ?: 0
                DetalhePostagemScreen(id, navController, homeViewModel)
            }

            // --- ADICIONADO: ROTA DA TELA DE CONFIGURAÇÕES (PERFIL) ---
            composable("configuracoes") {
                PerfilScreen(
                    navController = navController,
                    viewModel = homeViewModel
                )
            }

            // --- ROTAS SECUNDÁRIAS ---
            composable("notificacoes") { NotificationsScreen() }
            composable("mensagens") { MessagesScreen(navController) }
            composable("mural") { MuralScreen() }
            composable("ranking") { RankingScreen() }
            composable("pedido") { PedidosObjetosScreen() }
            composable("objeto") { PedidosObjetosScreen() }

            // --- CHAT DETALHADO ---
            composable(
                route = "chat_detalhe/{chatId}",
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("chatId")
                ChatDetalheScreen(id, navController)
            }
        }
    }
}