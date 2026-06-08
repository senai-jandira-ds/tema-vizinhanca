package com.example.mobilevizinhaa.ui.theme.listaitens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilevizinhaa.ui.theme.*
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosObjetosScreen(
    tokenUsuario: String,
    idUsuarioLogado: Int,
    viewModel: PedidosObjetosViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var filtroSelecionado by remember { mutableStateOf("TODOS") }
    var itemSelecionadoParaDetalhe by remember { mutableStateOf<ServiceDetailBackend?>(null) }

    // Dispara a busca de dados assim que a tela abre ou o Token valida
    LaunchedEffect(tokenUsuario, idUsuarioLogado) {
        if (tokenUsuario.isNotEmpty()) {
            viewModel.carregarItensDoUsuario(tokenUsuario, idUsuarioLogado)
        }
    }

    // Sincroniza o reset do filtro horizontal limpando espaços ao trocar de aba
    LaunchedEffect(selectedTab) {
        filtroSelecionado = "TODOS"
    }

    // Filtros padronizados SEM ACENTOS internos para mapeamento lógico uniforme
    val filtrosAtuais = if (selectedTab == 0) {
        listOf("TODOS", "PENDENTE", "EM_ANDAMENTO", "COMPLETED")
    } else {
        listOf("TODOS", "INDISPONIVEL", "DISPONIVEL", "EMPRESTADO")
    }

    // --- 🎯 FILTRAGEM DE ABAS TRATANDO ACENTOS ---
    val dadosFiltradosPorAba = remember(viewModel.listaMeusItens, selectedTab) {
        viewModel.listaMeusItens.filter { item ->
            val statusItem = item.status?.uppercase()?.trim()
                ?.replace("Í", "I")?.replace("Á", "A") ?: ""
            val nomeCat = item.category?.name?.uppercase()?.trim() ?: ""
            val tipoCategoriaString = item.category?.typeCategory?.toString()?.uppercase()?.trim() ?: ""

            val ehObjeto = statusItem in listOf("INDISPONIVEL", "DISPONIVEL", "EMPRESTADO") ||
                    nomeCat.contains("OBJETO") ||
                    tipoCategoriaString.contains("OBJETO") ||
                    nomeCat.contains("ACHADOS") ||
                    nomeCat.contains("FERRAMENTAS") ||
                    nomeCat.contains("DOAÇ") ||
                    nomeCat.contains("DOAC") ||
                    item.urgency == "OBJETO"

            if (selectedTab == 0) !ehObjeto else ehObjeto
        }
    }

    // --- 🎯 FILTRAGEM DOS CHIPS HORIZONTAIS ---
    val listaExibida = remember(dadosFiltradosPorAba, filtroSelecionado) {
        val filtroFormatado = filtroSelecionado.uppercase().trim()

        if (filtroFormatado == "TODOS") {
            dadosFiltradosPorAba
        } else {
            dadosFiltradosPorAba.filter { item ->
                val statusItem = item.status?.uppercase()?.trim()
                    ?.replace("Í", "I")?.replace("Á", "A") ?: ""

                val statusNormalizado = when (statusItem) {
                    "IN_PROGRESS", "EM_AND_AMENTO" -> "EM_ANDAMENTO"
                    "CONCLUIDO" -> "COMPLETED"
                    else -> statusItem
                }

                statusNormalizado == filtroFormatado
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientBlueStart, GradientBlueEnd)
                    )
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                Text(
                    text = "Pedidos e objetos",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gerencie seus pedidos e objetos",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 15.sp
                )
            }
        }

        // --- TABROW ---
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = BluePrimary,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BluePrimary,
                        height = 3.dp
                    )
                }
            },
            divider = {}
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Pedidos", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (selectedTab == 0) BluePrimary else Color.Gray)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Objetos", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (selectedTab == 1) BluePrimary else Color.Gray)
            }
        }

        // --- FILTROS HORIZONTAIS ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(filtrosAtuais) { filtro ->
                val filtroFormatado = filtro.uppercase().trim()
                val estaSelecionado = filtroSelecionado == filtroFormatado

                val labelExibicao = when (filtroFormatado) {
                    "PENDENTE" -> "Pendente"
                    "EM_AND_AMENTO", "EM_ANDAMENTO" -> "Em andamento"
                    "COMPLETED" -> "Concluído"
                    "INDISPONIVEL" -> "Indisponível"
                    "DISPONIVEL" -> "Disponível"
                    "EMPRESTADO" -> "Emprestado"
                    else -> "Todos"
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (estaSelecionado) BluePrimary else Color(0xFFF5F5F5))
                        .clickable {
                            filtroSelecionado = filtroFormatado
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = labelExibicao,
                        color = if (estaSelecionado) Color.White else Color.Black,
                        fontSize = 14.sp,
                        fontWeight = if (estaSelecionado) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // --- LISTA DE ITENS ---
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = BluePrimary)
            } else if (viewModel.errorMessage != null) {
                Text(text = viewModel.errorMessage!!, color = Color.Red, modifier = Modifier.align(Alignment.Center).padding(16.dp), fontSize = 14.sp)
            } else if (listaExibida.isEmpty()) {
                Text(
                    text = if (selectedTab == 0) "Você não possui solicitações de pedidos." else "Você não possui objetos cadastrados.",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 14.sp
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(listaExibida) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { itemSelecionadoParaDetalhe = item }
                        ) {
                            PedidoCard(servico = item)
                        }
                    }
                }
            }
        }
    }

    // --- POP-UP DETALHES INTEGRADO COM COMPONENTE EXTERNO ---
    itemSelecionadoParaDetalhe?.let { item ->
        DetalheItemDialog(
            item = item,
            onDismiss = { itemSelecionadoParaDetalhe = null },
            onExcluir = { id, ehObjeto ->
                // 🎯 CORRIGIDO: Repassa corretamente a flag booleana 'ehObjeto' para o ViewModel
                viewModel.excluirItemDoUsuario(tokenUsuario, id, ehObjeto)
                itemSelecionadoParaDetalhe = null
            },
            onMudarParaAndamento = { id ->
                viewModel.colocarItemNoMural(tokenUsuario, id)
                itemSelecionadoParaDetalhe = null
            }
        )
    }
}