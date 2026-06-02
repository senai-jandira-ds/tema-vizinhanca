package com.example.mobilevizinhaa.ui.theme.mural

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilevizinhaa.ui.theme.*
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend
import com.example.mobilevizinhaa.ui.theme.mural.detalhes.MuralDetalheScreen

@Composable
fun MuralScreen(
    tokenUsuario: String, // Recebe o token do usuário logado para autorizar a requisição na API
    viewModel: MuralViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Controla qual serviço foi clicado para abrir a tela de detalhes de forma dinâmica (Usa o modelo Backend)
    var servicoSelecionadoParaDetalhes by remember { mutableStateOf<ServiceDetailBackend?>(null) }

    // Controla a exibição do popup de ajuda rápido direto pelo card do mural
    var servicoParaOferecerAjuda by remember { mutableStateOf<ServiceDetailBackend?>(null) }

    // Dispara a busca real na API do condomínio assim que a tela abre ou o token atualiza
    LaunchedEffect(tokenUsuario) {
        if (tokenUsuario.isNotEmpty()) {
            viewModel.carregarPostsReais(tokenUsuario)
        }
    }

    // --- ESTRUTURA DE NAVEGAÇÃO CONDICIONAL CORRIGIDA ---
    if (servicoSelecionadoParaDetalhes != null) {
        val item = servicoSelecionadoParaDetalhes!!

        // SOLUÇÃO DO COMPILADOR: Converte dinamicamente o ServiceDetailBackend para o ServiceDetail antigo
        // 🎯 AJUSTADO: Passando apenas a String direto no typeCategory para bater com o novo DataModels refatorado
        val servicoConvertido = com.example.mobilevizinhaa.ui.theme.data.ServiceDetail(
            id = item.id,
            title = item.title ?: "Sem título",
            description = item.description ?: "",
            estimatedTime = item.estimatedTime ?: 0,
            urgency = item.urgency ?: "MEDIUM",
            status = item.status ?: "ACTIVE",
            photo = null,
            resident = com.example.mobilevizinhaa.ui.theme.data.ResidentDetail(
                id = item.resident?.id ?: 0,
                name = item.resident?.name ?: "Morador",
                email = item.resident?.email ?: "",
                apartment = item.resident?.apartment ?: "",
                phone = item.resident?.phone ?: "",
                cpf = item.resident?.cpf,
                score = item.resident?.score ?: 0,
                creationDate = item.resident?.creationDate,
                block = item.resident?.block
            ),
            category = com.example.mobilevizinhaa.ui.theme.data.CategoryDetail(
                id = item.category?.id ?: 0,
                name = item.category?.name ?: "Geral",
                description = item.category?.description ?: "",
                // 🎯 CORREÇÃO CRÍTICA: Agora repassa a String direto, sem tentar criar o objeto que causava o crash
                typeCategory = item.category?.typeCategory ?: "SERVICO"
            )
        )

        // Abre a tela de detalhes passando o objeto convertido esperado por ela
        MuralDetalheScreen(
            servico = servicoConvertido,
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

                // Tratamento reativo visual de carregamento ou erro
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                } else if (uiState.errorMessage != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.errorMessage!!, color = Color.Red, fontSize = 14.sp)
                    }
                } else if (uiState.posts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Nenhum serviço em andamento no condomínio.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    // Lista de Postagens observando o estado reativo do seu ViewModel real
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