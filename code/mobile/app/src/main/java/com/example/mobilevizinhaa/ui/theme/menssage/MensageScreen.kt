package com.example.mobilevizinhaa.ui.theme.menssage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MessagesScreen(viewModel: MessagesViewModel = viewModel()) {
    // Coletando o estado da UI da ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header com Gradiente Profissional
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

        // Lista de Mensagens
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            // bottom = 80.dp garante que a última mensagem não fique escondida atrás da BottomBar
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            // Usamos uiState.conversations que vem da nossa ViewModel estruturada
            items(uiState.conversations) { chat ->
                MessageItem(
                    chat = chat,
                    onClick = {
                        // Logica para abrir a conversa específica futuramente
                        println("Clicou na conversa de ${chat.name}")
                    }
                )
            }
        }
    }
}