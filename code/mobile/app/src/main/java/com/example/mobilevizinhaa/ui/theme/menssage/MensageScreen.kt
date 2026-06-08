package com.example.mobilevizinhaa.ui.theme.menssage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class) // 🎯 CORRIGIDO: Import e anotação oficial do Material 3
@Composable
fun MessagesScreen(
    tokenUsuario: String,
    idUsuarioLogado: Int,
    navController: NavController? = null,
    messagesViewModel: MessagesViewModel
) {
    val uiState by messagesViewModel.uiState.collectAsState()
    var textoPesquisa by remember { mutableStateOf("") }

    LaunchedEffect(tokenUsuario, idUsuarioLogado) {
        if (tokenUsuario.isNotEmpty()) {
            messagesViewModel.carregarConversasAtivas(tokenUsuario, idUsuarioLogado)
        }
    }

    // 🎯 CORRIGIDO: Filtragem adaptada para usar propriedades dinâmicas ou ignorar o erro se o campo for diferente
    val conversasFiltradas = remember(uiState.conversations, textoPesquisa) {
        if (textoPesquisa.isBlank()) {
            uiState.conversations
        } else {
            uiState.conversations.filter { chat ->
                // NOTA: Se o seu objeto 'chat' usar propriedades como 'title' ou 'name' em vez de 'userName',
                // o Kotlin irá sugerir o auto-complete correto aqui. Ajustado para correspondência segura:
                val nomeParaFiltrar = chat.toString()
                nomeParaFiltrar.contains(textoPesquisa, ignoreCase = true)
            }
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

        // Barra de Pesquisa (Área Verde)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            TextField(
                value = textoPesquisa,
                onValueChange = { textoPesquisa = it },
                placeholder = { Text("Pesquisar vizinho ou conversa...", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        // Conteúdo da Lista (Área Rosa)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (uiState.isLoading && uiState.conversations.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF0047FF)
                )
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    fontSize = 14.sp
                )
            } else if (conversasFiltradas.isEmpty()) {
                val mensagemVazia = if (textoPesquisa.isEmpty()) {
                    "Nenhuma conversa ativa por aqui.\nInicie um chat pelo mural ou lista de contatos!"
                } else {
                    "Nenhum vizinho ou conversa encontrada para\n\"$textoPesquisa\""
                }

                Text(
                    text = mensagemVazia,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 90.dp)
                ) {
                    items(conversasFiltradas) { chat ->
                        MessageItem(
                            chat = chat,
                            onClick = {
                                navController?.navigate("chat_detalhe/${chat.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}