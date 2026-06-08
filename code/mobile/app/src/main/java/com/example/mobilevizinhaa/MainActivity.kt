package com.example.mobilevizinhaa

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.mobilevizinhaa.ui.theme.menssage.MessagesViewModel
import com.example.mobilevizinhaa.ui.theme.menssage.chatdetails.ChatDetalheScreen
import com.example.mobilevizinhaa.ui.theme.notification.NotificationsScreen
import com.example.mobilevizinhaa.ui.theme.home.createpost.createpost.PublicacaoScreen
import com.example.mobilevizinhaa.ui.theme.home.detail.DetalhePostagemScreen

// Imports de criação e configurações
import com.example.mobilevizinhaa.ui.theme.home.createservice.CriarPedidoObjetoScreen
import com.example.mobilevizinhaa.ui.theme.home.createobjeto.CriarObjetoScreen
import com.example.mobilevizinhaa.ui.theme.`configuraçoes`.PerfilScreen

// Import do tema global e dados
import com.example.mobilevizinhaa.ui.theme.MobileVizinhaçaTheme
import com.example.mobilevizinhaa.ui.theme.data.ResidentResponse
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient // 🎯 Aponta corretamente para o seu objeto de rede
import com.google.gson.Gson

// IMPORTS ESSENCIAIS PARA TRATAR O TOKEN COM SEGURANÇA
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current

            val homeViewModel: HomeViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    context.applicationContext as Application
                )
            )

            val modoEscuroAtivo by homeViewModel.isDarkMode.collectAsState()

            MobileVizinhaçaTheme(isDarkMode = modoEscuroAtivo) {
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
    val context = LocalContext.current

    // Esconde a BottomBar nas telas secundárias de fluxo de cadastro, login e detalhes internos
    val esconderBottomBar = currentRoute == null ||
            currentRoute == "login" ||
            currentRoute == "publicacao" ||
            currentRoute.startsWith("criar_servico") ||
            currentRoute.startsWith("criar_objeto") ||
            currentRoute.startsWith("chat_detalhe") ||
            currentRoute.startsWith("detalhe_post")

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
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
                HomeScreen(navController = navController, viewModel = homeViewModel)
            }

            // --- CRIAÇÃO DE POSTAGEM ---
            composable("publicacao") {
                PublicacaoScreen(navController, homeViewModel)
            }

            // --- CRIAÇÃO DE SERVIÇO ---
            composable(
                route = "criar_servico/{tokenUsuario}/{idUsuarioLogado}",
                arguments = listOf(
                    navArgument("tokenUsuario") { type = NavType.StringType },
                    navArgument("idUsuarioLogado") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val tokenBruto = backStackEntry.arguments?.getString("tokenUsuario") ?: ""
                val idUsuario = backStackEntry.arguments?.getInt("idUsuarioLogado") ?: 0
                val tokenDecodificado = try { URLDecoder.decode(tokenBruto, StandardCharsets.UTF_8.toString()) } catch (e: Exception) { tokenBruto }

                CriarPedidoObjetoScreen(navController, tokenDecodificado, idUsuario)
            }

            // --- CRIAÇÃO DE OBJETO ---
            composable(
                route = "criar_objeto/{tokenUsuario}/{idUsuarioLogado}",
                arguments = listOf(
                    navArgument("tokenUsuario") { type = NavType.StringType },
                    navArgument("idUsuarioLogado") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val tokenBruto = backStackEntry.arguments?.getString("tokenUsuario") ?: ""
                val tokenDecodificado = try { URLDecoder.decode(tokenBruto, StandardCharsets.UTF_8.toString()) } catch (e: Exception) { tokenBruto }

                CriarObjetoScreen(tokenUsuario = tokenDecodificado, onVoltarClick = { navController.popBackStack() })
            }

            // --- DETALHE DA POSTAGEM ---
            composable(
                route = "detalhe_post/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("postId") ?: 0
                DetalhePostagemScreen(id, navController, homeViewModel)
            }

            // --- CONFIGURAÇÕES ---
            composable("configuracoes") {
                PerfilScreen(navController = navController, viewModel = homeViewModel)
            }

            composable("notificacoes") { NotificationsScreen() }

            // --- TELA DE MENSAGENS / CHAT (SINCRONIZADO E COM VIEWMODEL INJETADO VIA FACTORY) ---
            composable(
                route = "mensagens/{tokenUsuario}/{idUsuarioLogado}",
                arguments = listOf(
                    navArgument("tokenUsuario") { type = NavType.StringType },
                    navArgument("idUsuarioLogado") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val tokenBruto = backStackEntry.arguments?.getString("tokenUsuario") ?: ""
                val idUsuario = backStackEntry.arguments?.getInt("idUsuarioLogado") ?: 0

                val tokenDecodificado = try {
                    URLDecoder.decode(tokenBruto, StandardCharsets.UTF_8.toString())
                } catch (e: Exception) {
                    tokenBruto
                }

                // 🎯 SUCESSO: Apontando exatamente para 'RetrofitClient.authApi' mapeado no seu arquivo!
                val messagesViewModel: MessagesViewModel = viewModel(
                    factory = MessagesViewModel.provideFactory(
                        apiService = RetrofitClient.authApi
                    )
                )

                Log.d("MAIN_ACTIVITY", "Abrindo MessagesScreen -> ID: $idUsuario | ViewModel Configurado com Sucesso")

                MessagesScreen(
                    tokenUsuario = tokenDecodificado,
                    idUsuarioLogado = idUsuario,
                    navController = navController,
                    messagesViewModel = messagesViewModel
                )
            }

            // --- MURAL ---
            composable(
                route = "mural/{tokenUsuario}",
                arguments = listOf(navArgument("tokenUsuario") { type = NavType.StringType })
            ) { backStackEntry ->
                val tokenBruto = backStackEntry.arguments?.getString("tokenUsuario") ?: ""
                val tokenDecodificado = try { URLDecoder.decode(tokenBruto, StandardCharsets.UTF_8.toString()) } catch (e: Exception) { tokenBruto }

                MuralScreen(tokenUsuario = tokenDecodificado)
            }

            composable("ranking") { RankingScreen() }

            // --- PEDIDOS ---
            composable(
                route = "pedido/{tokenUsuario}/{idUsuarioLogado}",
                arguments = listOf(
                    navArgument("tokenUsuario") { type = NavType.StringType },
                    navArgument("idUsuarioLogado") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val tokenBruto = backStackEntry.arguments?.getString("tokenUsuario") ?: ""
                val idUsuario = backStackEntry.arguments?.getInt("idUsuarioLogado") ?: 0
                val tokenDecodificado = try { URLDecoder.decode(tokenBruto, StandardCharsets.UTF_8.toString()) } catch (e: Exception) { tokenBruto }

                PedidosObjetosScreen(tokenUsuario = tokenDecodificado, idUsuarioLogado = idUsuario)
            }

            // --- OBJETOS ---
            composable(
                route = "objeto/{tokenUsuario}/{idUsuarioLogado}",
                arguments = listOf(
                    navArgument("tokenUsuario") { type = NavType.StringType },
                    navArgument("idUsuarioLogado") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val tokenBruto = backStackEntry.arguments?.getString("tokenUsuario") ?: ""
                val idUsuario = backStackEntry.arguments?.getInt("idUsuarioLogado") ?: 0
                val tokenDecodificado = try { URLDecoder.decode(tokenBruto, StandardCharsets.UTF_8.toString()) } catch (e: Exception) { tokenBruto }

                PedidosObjetosScreen(tokenUsuario = tokenDecodificado, idUsuarioLogado = idUsuario)
            }

            // --- CHAT DETALHE ---
            composable(
                route = "chat_detalhe/{chatId}",
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("chatId")
                val tokenBruto = homeViewModel.obterTokenSalvo()
                val tokenDecodificado = try { URLDecoder.decode(tokenBruto, StandardCharsets.UTF_8.toString()) } catch (e: Exception) { tokenBruto }

                val usuarioMemoria = homeViewModel.residentData.collectAsState().value
                val idUsuarioSalvo = if (usuarioMemoria != null) {
                    usuarioMemoria.id
                } else {
                    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val json = prefs.getString("saved_user", null)
                    if (!json.isNullOrBlank()) {
                        try { Gson().fromJson(json, ResidentResponse::class.java).id } catch (e: Exception) { 0 }
                    } else { 0 }
                }

                Log.d("MAIN_ACTIVITY", "Abrindo Chat Detalhe -> Chat: $id | ID Usuário: $idUsuarioSalvo")

                ChatDetalheScreen(
                    id = id,
                    tokenUsuario = tokenDecodificado,
                    idUsuarioLogado = idUsuarioSalvo,
                    navController = navController
                )
            }
        }
    }
}