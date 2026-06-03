package com.example.mobilevizinhaa.ui.theme.listaitens

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.* // Puxa GradientBlueStart, GradientBlueEnd e BluePrimary originais
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

    // 🕵️ CONSOLE.LOG 1: Monitoriza quando a lista bruta muda vinda da API/ViewModel
    LaunchedEffect(viewModel.listaMeusItens) {
        Log.d("BUG_HUNT", "=== [CONSOLE.LOG 1] Nova lista bruta recebida do ViewModel ===")
        Log.d("BUG_HUNT", "Total de itens brutos: ${viewModel.listaMeusItens.size}")
        viewModel.listaMeusItens.forEachIndexed { index, item ->
            Log.d("BUG_HUNT", "Item [$index] -> ID: ${item.id} | Titulo: ${item.title} | Status Bruto: '${item.status}' | Categoria: '${item.category?.name}'")
        }
    }

    // Dispara a busca de dados assim que a tela abre ou o Token valida
    LaunchedEffect(tokenUsuario, idUsuarioLogado) {
        if (tokenUsuario.isNotEmpty()) {
            Log.d("BUG_HUNT", "Chamando carregarItensDoUsuario... ID Logado: $idUsuarioLogado")
            viewModel.carregarItensDoUsuario(tokenUsuario, idUsuarioLogado)
        }
    }

    // Sincroniza o reset do filtro horizontal limpando espaços e forçando Caixa Alta sempre
    LaunchedEffect(selectedTab) {
        Log.d("BUG_HUNT", "Aba alterada para: $selectedTab -> Forçando filtro para TODOS")
        filtroSelecionado = "TODOS"
    }

    // Filtros padronizados em String pura de banco de dados
    val filtrosAtuais = if (selectedTab == 0) {
        listOf("TODOS", "PENDENTE", "EM_AND_AMENTO", "EM_ANDAMENTO", "COMPLETED")
    } else {
        listOf("TODOS", "INDISPONÍVEL", "DISPONÍVEL", "EMPRESTADO")
    }

    // --- 🎯 FILTRAGEM DEFINITIVA DE ABAS ---
    val dadosFiltradosPorAba = remember(viewModel.listaMeusItens, selectedTab) {
        val listaFiltrada = viewModel.listaMeusItens.filter { item ->
            val statusItem = item.status?.uppercase()?.trim() ?: ""
            val nomeCategoria = item.category?.name?.uppercase()?.trim() ?: ""
            val tipoCategoriaString = item.category?.typeCategory?.toString()?.uppercase()?.trim() ?: ""

            val ehObjeto = statusItem in listOf("INDISPONÍVEL", "DISPONÍVEL", "EMPRESTADO") ||
                    nomeCategoria.contains("OBJETO") ||
                    tipoCategoriaString.contains("OBJETO") ||
                    nomeCategoria.contains("ACHADOS") ||
                    nomeCategoria.contains("DOAÇ") ||
                    nomeCategoria.contains("DOAC")

            if (selectedTab == 0) !ehObjeto else ehObjeto
        }

        // 🕵️ CONSOLE.LOG 2: Vê o resultado da separação por Aba (Pedidos vs Objetos)
        Log.d("BUG_HUNT", "=== [CONSOLE.LOG 2] Itens separados para a Aba Atual ($selectedTab) ===")
        Log.d("BUG_HUNT", "Quantidade de itens nesta aba: ${listaFiltrada.size}")
        listaFiltrada.forEach { item ->
            Log.d("BUG_HUNT", " -> ID: ${item.id} | Status: '${item.status}' | Titulo: ${item.title}")
        }

        listaFiltrada
    }

    // --- 🎯 FILTRAGEM DOS CHIPS HORIZONTAIS CORRIGIDA ---
    val listaExibida = remember(dadosFiltradosPorAba, filtroSelecionado) {
        val filtroFormatado = filtroSelecionado.uppercase().trim()

        val resultadoFinal = if (filtroFormatado == "TODOS") {
            dadosFiltradosPorAba
        } else {
            dadosFiltradosPorAba.filter { item ->
                val statusItem = item.status?.uppercase()?.trim() ?: ""

                // Trata mapeamentos estendidos do backend para garantir consistência total
                val statusNormalizado = when (statusItem) {
                    "IN_PROGRESS", "EM_AND_AMENTO" -> "EM_ANDAMENTO"
                    "CONCLUIDO" -> "COMPLETED"
                    else -> statusItem
                }

                statusNormalizado == filtroFormatado
            }
        }

        // 🕵️ CONSOLE.LOG 3: O que realmente vai ser renderizado na tela após o clique do chip
        Log.d("BUG_HUNT", "=== [CONSOLE.LOG 3] Resultado Final da Filtragem ===")
        Log.d("BUG_HUNT", "Filtro ativo: '$filtroFormatado' | Itens visíveis finais na lista: ${resultadoFinal.size}")
        resultadoFinal.forEach { item ->
            Log.d("BUG_HUNT", " -> VISÍVEL NO CARD: ID: ${item.id} | Status Final: '${item.status}' | Titulo: ${item.title}")
        }

        resultadoFinal
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

        // --- TABROW (Alternância de Abas Verticais) ---
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
                    "INDISPONÍVEL" -> "Indisponível"
                    "DISPONÍVEL" -> "Disponível"
                    "EMPRESTADO" -> "Emprestado"
                    else -> "Todos"
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (estaSelecionado) BluePrimary else Color(0xFFF5F5F5))
                        .clickable {
                            // 🕵️ CONSOLE.LOG 4: Captura o clique seco do usuário
                            Log.d("BUG_HUNT", "=== [CONSOLE.LOG 4] CLIQUE DO USUÁRIO ===")
                            Log.d("BUG_HUNT", "Clicaste no chip: '$filtroFormatado'. Mudando filtro de '$filtroSelecionado' para '$filtroFormatado'")
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

        // --- LISTA DE ITENS REATIVA ---
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

    // --- POP-UP DETALHES ---
    itemSelecionadoParaDetalhe?.let { item ->
        AlertDialog(
            onDismissRequest = { itemSelecionadoParaDetalhe = null },
            confirmButton = {},
            dismissButton = {},
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White,
            title = {
                Text(text = item.title ?: "Sem título", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column {
                        Text(text = "Descrição:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
                        Text(text = item.description ?: "Nenhuma descrição informada.", color = Color.Gray, fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Status:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)

                            val statusFormatado = when (item.status?.uppercase()?.trim() ?: "") {
                                "INDISPONÍVEL" -> "Indisponível"
                                "DISPONÍVEL" -> "Disponível"
                                "EMPRESTADO" -> "Emprestado"
                                "EM_AND_AMENTO", "EM_ANDAMENTO", "IN_PROGRESS" -> "Em andamento"
                                "COMPLETED", "CONCLUIDO" -> "Concluído"
                                "PENDENTE" -> "Pendente"
                                else -> item.status ?: "Desconhecido"
                            }
                            Text(text = statusFormatado, color = BluePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Categoria:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
                            Text(text = item.category?.name ?: "Geral", color = Color.Gray, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val statusItem = item.status?.uppercase()?.trim() ?: ""
                    val nomeCategoria = item.category?.name?.uppercase()?.trim() ?: ""
                    val tipoCategoriaString = item.category?.typeCategory?.toString()?.uppercase()?.trim() ?: ""

                    val ehObjeto = statusItem in listOf("INDISPONÍVEL", "DISPONÍVEL", "EMPRESTADO") ||
                            nomeCategoria.contains("OBJETO") ||
                            tipoCategoriaString.contains("OBJETO") ||
                            nomeCategoria.contains("ACHADOS") ||
                            nomeCategoria.contains("DOAÇ") ||
                            nomeCategoria.contains("DOAC")

                    val textoBotaoMural = if (ehObjeto) "Disponibilizar no Mural" else "Colocar no Mural (Andamento)"

                    if (statusItem == "PENDENTE" || statusItem == "INDISPONÍVEL") {
                        Button(
                            onClick = {
                                val statusAlvo = if (ehObjeto) "DISPONÍVEL" else "EM_ANDAMENTO"
                                viewModel.atualizarStatusItem(tokenUsuario, item.id, statusAlvo)
                                itemSelecionadoParaDetalhe = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = textoBotaoMural, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.excluirItemDoUsuario(tokenUsuario, item.id)
                            itemSelecionadoParaDetalhe = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(Color(0xFFEF4444))
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Excluir", color = Color(0xFFEF4444), fontWeight = FontWeight.Medium)
                    }
                }
            }
        )
    }
}