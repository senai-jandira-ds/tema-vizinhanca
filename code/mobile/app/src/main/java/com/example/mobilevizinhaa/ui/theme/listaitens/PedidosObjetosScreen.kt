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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.*
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosObjetosScreen(
    tokenUsuario: String,         // Recebe o Token do usuário autenticado
    idUsuarioLogado: Int,         // Recebe o ID do residente logado para fazer o filtro pessoal
    viewModel: PedidosObjetosViewModel = viewModel() // Instancia a sua ViewModel real
) {
    // Controla o estado da aba atual: 0 para Pedidos, 1 para Objetos
    var selectedTab by remember { mutableIntStateOf(0) }
    var filtroSelecionado by remember { mutableStateOf("Todos") }

    // Estado que gerencia qual item foi clicado para abrir e preencher a pop-up
    var itemSelecionadoParaDetalhe by remember { mutableStateOf<ServiceDetailBackend?>(null) }

    // Dispara a busca e o filtro no banco de dados assim que a tela abre ou as credenciais mudam
    LaunchedEffect(tokenUsuario, idUsuarioLogado) {
        if (tokenUsuario.isNotEmpty()) {
            viewModel.carregarItensDoUsuario(tokenUsuario, idUsuarioLogado)
        }
    }

    // Reseta o filtro horizontal para "Todos" toda vez que o valor da Tab mudar
    LaunchedEffect(selectedTab) {
        filtroSelecionado = "Todos"
    }

    // --- CORRIGIDO: Alinhado "IN_PROGRESS" para "EM_ANDAMENTO" conforme o padrão real da API ---
    val filtrosAtuais = if (selectedTab == 0) {
        listOf("Todos", "PENDENTE", "EM_ANDAMENTO", "COMPLETED")
    } else {
        listOf("Todos", "DISPONIVEL", "EMPRESTIMO", "DOACAO", "ACHADOS")
    }

    // --- SEPARAÇÃO LOCAL INTELIGENTE (PEDIDO DE SERVIÇO vs OBJETO) ---
    val dadosFiltradosPorAba = remember(viewModel.listaMeusItens, selectedTab) {
        viewModel.listaMeusItens.filter { item ->
            val tipoCategoriaString = item.category?.typeCategory?.toString()?.uppercase() ?: ""
            if (selectedTab == 0) {
                !tipoCategoriaString.contains("OBJETO")
            } else {
                tipoCategoriaString.contains("OBJETO")
            }
        }
    }

    // Aplica o filtro de chips horizontais sobre os dados da aba activa (com tratamento ignoreCase e uppercase)
    val listaExibida = remember(dadosFiltradosPorAba, filtroSelecionado) {
        if (filtroSelecionado.uppercase() == "TODOS") {
            dadosFiltradosPorAba
        } else {
            dadosFiltradosPorAba.filter { it.status.uppercase() == filtroSelecionado.uppercase() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- HEADER COM O DEGRADÊ LUMINOSO ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            com.example.mobilevizinhaa.ui.theme.GradientBlueStart,
                            com.example.mobilevizinhaa.ui.theme.GradientBlueEnd
                        )
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

        // --- TABROW (Troca o estado de qual aba está ativa) ---
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
                Text(
                    text = "Pedidos",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (selectedTab == 0) BluePrimary else Color.Gray
                )
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text(
                    text = "Objetos",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (selectedTab == 1) BluePrimary else Color.Gray
                )
            }
        }

        // --- LAZYROW DE FILTROS ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(filtrosAtuais) { filtro ->
                val labelExibicao = when (filtro.uppercase()) {
                    "PENDENTE" -> "Pendente"
                    "EM_ANDAMENTO" -> "Em andamento"
                    "COMPLETED" -> "Concluído"
                    "DISPONIVEL" -> "Disponível"
                    "EMPRESTIMO" -> "Empréstimo"
                    "DOACAO" -> "Doação"
                    "ACHADOS" -> "Achados"
                    else -> "Todos"
                }

                FilterChip(
                    selected = filtroSelecionado.uppercase() == filtro.uppercase(),
                    onClick = { filtroSelecionado = filtro },
                    label = { Text(labelExibicao) },
                    shape = CircleShape,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BluePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF5F5F5),
                        labelColor = Color.Black
                    ),
                    border = null
                )
            }
        }

        // --- EXIBIÇÃO TRATADA DO CONTEÚDO ---
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BluePrimary
                )
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    fontSize = 14.sp
                )
            } else if (listaExibida.isEmpty()) {
                Text(
                    text = if (selectedTab == 0) "Você não possui solicitações de pedidos." else "Você não possui objetos cadastrados.",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 14.sp
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(listaExibida) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { itemSelecionadoParaDetalhe = item }
                        ) {
                            PedidoCard(servico = item)
                        }
                    }
                }
            }
        }
    }

    // --- INTEGRADO: DIÁLOGO POP-UP COM REQUISIÇÕES REAIS ---
    itemSelecionadoParaDetalhe?.let { item ->
        AlertDialog(
            onDismissRequest = { itemSelecionadoParaDetalhe = null },
            confirmButton = {},
            dismissButton = {},
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = item.title ?: "Sem título",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
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
                            Text(text = item.status ?: "PENDENTE", color = BluePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Categoria:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
                            Text(text = item.category?.name ?: "Geral", color = Color.Gray, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // CHAMADA INTEGRADA: Colocar no Mural (Mudar status para andamento)
                    Button(
                        onClick = {
                            viewModel.mudarStatusParaAndamento(tokenUsuario, item.id)
                            itemSelecionadoParaDetalhe = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Colocar no Mural (Andamento)", color = Color.White, fontWeight = FontWeight.Medium)
                    }

                    // CHAMADA INTEGRADA: Excluir item permanentemente da API
                    OutlinedButton(
                        onClick = {
                            viewModel.excluirItemDoUsuario(tokenUsuario, item.id)
                            itemSelecionadoParaDetalhe = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFEF4444))
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