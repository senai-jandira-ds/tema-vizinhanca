package com.example.mobilevizinhaa

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
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

// ATUALIZADO: Import da sua tela de criação de serviço/pedido descomentado com sucesso
import com.example.mobilevizinhaa.ui.theme.home.createservice.CriarPedidoObjetoScreen
import com.example.mobilevizinhaa.ui.theme.`configuraçoes`.PerfilScreen

// INTEGRADO: Import do seu tema para complementar a navegação global
import com.example.mobilevizinhaa.ui.theme.MobileVizinhaçaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current

            // Instancia o HomeViewModel centralizado aqui na MainActivity para escutar o SharedPreferences
            val homeViewModel: HomeViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    context.applicationContext as Application
                )
            )

            // 1. Observa reativamente o estado do tema salvo nas configurações
            val modoEscuroAtivo by homeViewModel.isDarkMode.collectAsState()

            // 2. Aplica o Tema customizado em TODAS as rotas e telas do App
            MobileVizinhaçaTheme(isDarkMode = modoEscuroAtivo) {
                // Surface força o contêiner base a assumir a cor de fundo correta (Claro/Escuro)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(homeViewModel = homeViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(homeViewModel: HomeViewModel) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val esconderBottomBar = currentRoute == null ||
            currentRoute == "login" ||
            currentRoute == "publicacao" ||
            currentRoute == "criar_pedido" ||
            currentRoute.startsWith("chat_detalhe") ||
            currentRoute.startsWith("detalhe_post")

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        // Garante que o Scaffold use o fundo do tema do MaterialTheme para não sobrepor telas brancas
        containerColor = MaterialTheme.colorScheme.background,
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

            // --- TELA HOME ---
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

            // ATUALIZADO: Rota reativada apontando corretamente para o CriarPedidoObjetoScreen
            composable("criar_pedido") {
                CriarPedidoObjetoScreen(
                    navController = navController,
                    homeViewModel = homeViewModel
                )
            }

            // --- DETALHE DA POSTAGEM ---
            composable(
                route = "detalhe_post/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("postId") ?: 0
                DetalhePostagemScreen(id, navController, homeViewModel)
            }

            // --- ROTA DA TELA DE CONFIGURAÇÕES (PERFIL) ---
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