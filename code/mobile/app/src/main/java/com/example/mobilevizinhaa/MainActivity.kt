package com.example.mobilevizinhaa

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Importação para habilitar a StatusBar transparente
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
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

// IMPORT DA NOVA TELA DE CADASTRO
import com.example.mobilevizinhaa.ui.theme.listaitens.criar.CriarPedidoObjetoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ativa o comportamento transparente de ponta a ponta (Edge-to-Edge)
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

    /** * CENTRALIZAÇÃO DO VIEWMODEL:
     * Criamos o HomeViewModel aqui para que ele sobreviva às trocas de tela.
     * Usamos a Factory para permitir que ele acesse o SharedPreferences (contexto).
     */
    val homeViewModel: HomeViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        // Impede que os componentes da base entrem por baixo das barras virtuais de navegação nativas do Android
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            // Lógica para esconder a barra em telas de foco (Login, Criação, Chat, Criar Pedido)
            val showBottomBar = currentRoute != null &&
                    currentRoute != "login" &&
                    currentRoute != "publicacao" &&
                    currentRoute != "criar_pedido" &&
                    !currentRoute.startsWith("chat_detalhe") &&
                    !currentRoute.startsWith("detalhe_post")

            if (showBottomBar) {
                CustomBottomNavBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {

            // --- TELA DE LOGIN ---
            composable("login") {
                LoginScreen(
                    navController = { rota ->
                        navController.navigate(rota) {
                            // Limpa a pilha para o usuário não voltar para o login com o botão "back"
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