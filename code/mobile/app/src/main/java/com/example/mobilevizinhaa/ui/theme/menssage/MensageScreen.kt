package com.example.mobilevizinhaa.ui.theme.menssage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobilevizinhaa.ui.theme.notificacoes.MessageItem

@Composable
fun MessagesScreen(
    tokenUsuario: String,       // 🎯 Recebido direto da rota da MainActivity
    idUsuarioLogado: Int,       // 🎯 Recebido direto da rota da MainActivity
    navController: NavController? = null,
    messagesViewModel: MessagesViewModel // 🎯 Removido o "= viewModel()" para evitar instâncias sem Factory que travam o app
) {
    val uiState by messagesViewModel.uiState.collectAsState()

    // 🎯 Dispara a busca automática das conversas sempre que o token ou id mudar (e forem válidos)
    LaunchedEffect(tokenUsuario, idUsuarioLogado) {
        if (tokenUsuario.isNotEmpty()) {
            messagesViewModel.carregarConversasAtivas(tokenUsuario, idUsuarioLogado)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header com Gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF0047FF), Color(0xFF75E6FF))
                    )
                )
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column {
                Text(
                    text = "Mensagens",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Troque mensagens com seus vizinhos",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        // 🎯 Área de conteúdo com controle de estados (Loading, Erro, Lista Vazia)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (uiState.isLoading && uiState.conversations.isEmpty()) {
                // Indicador de progresso centralizado enquanto baixa as conversas da API
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF0047FF)
                )
            } else if (uiState.errorMessage != null) {
                // Mensagem caso haja alguma falha de conexão com o servidor
                Text(
                    text = uiState.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    fontSize = 14.sp
                )
            } else if (uiState.conversations.isEmpty()) {
                // Estado visual limpo se o morador ainda não começou nenhuma conversa
                Text(
                    text = "Nenhuma conversa ativa por aqui.\nInicie um chat pelo mural ou lista de contatos!",
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            } else {
                // Lista de Mensagens Realizada com Sucesso
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp) // Espaçamento inferior para não sumir atrás da BottomNavBar
                ) {
                    items(uiState.conversations) { chat ->
                        MessageItem(
                            chat = chat,
                            onClick = {
                                // Envia o ID numérico real da conversa na rota para abrir o chat detalhado
                                navController?.navigate("chat_detalhe/${chat.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}