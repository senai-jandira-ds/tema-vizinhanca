package com.example.mobilevizinhaa.ui.theme.mural

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilevizinhaa.ui.theme.*
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetail
import com.example.mobilevizinhaa.ui.theme.mural.detalhes.MuralDetalheScreen

@Composable
fun MuralScreen(viewModel: MuralViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // Controla qual serviço foi clicado para abrir a tela de detalhes de forma dinâmica
    var servicoSelecionadoParaDetalhes by remember { mutableStateOf<ServiceDetail?>(null) }

    // Controla a exibição do popup de ajuda rápido direto pelo card do mural
    var servicoParaOferecerAjuda by remember { mutableStateOf<ServiceDetail?>(null) }

    // --- ESTRUTURA DE NAVEGAÇÃO CONDICIONAL ---
    if (servicoSelecionadoParaDetalhes != null) {
        // Se houver um serviço selecionado, intercepta o desenho da tela e exibe a sua MuralDetalheScreen
        MuralDetalheScreen(
            servico = servicoSelecionadoParaDetalhes!!,
            onBackClick = { servicoSelecionadoParaDetalhes = null } // Volta para o mural limpando o estado
        )
    } else {
        // Layout principal do Mural caso nenhum detalhe esteja aberto
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {

                // Cabeçalho com o Gradiente Azul padrão do seu App
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(brush = Brush.verticalGradient(colors = listOf(GradientBlueStart, GradientBlueEnd)))
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                        Text("Mural", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("Veja as últimas atualizações", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }

                // Lista de Postagens observando o estado reativo do seu ViewModel
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(uiState.posts) { post ->
                        // Chamamos o PostItem passando o objeto real, mapeando os cliques com segurança
                        PostItem(
                            post = post,
                            onDetalhesClick = { itemClicado ->
                                servicoSelecionadoParaDetalhes = itemClicado
                            },
                            onOferecerAjudaClick = { itemClicado ->
                                servicoParaOferecerAjuda = itemClicado
                            }
                        )
                    }
                }
            }

            // --- POPUP DIALOG RÁPIDO DE OFERECER AJUDA (Direto do Feed) ---
            if (servicoParaOferecerAjuda != null) {
                AlertDialog(
                    onDismissRequest = { servicoParaOferecerAjuda = null },
                    title = {
                        Text(text = "Alerta", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    },
                    text = {
                        Text(
                            text = "Você deseja oferecer ajuda para o pedido \"${servicoParaOferecerAjuda?.title}\"?\nApós confirmar, vocês poderão combinar o serviço pelo chat.",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                servicoParaOferecerAjuda = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)) // Cor verde padrão de ajuda do app
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { servicoParaOferecerAjuda = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)) // Cor vermelha padrão
                        ) {
                            Text("Cancelar")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = Color.White
                )
            }
        }
    }
}