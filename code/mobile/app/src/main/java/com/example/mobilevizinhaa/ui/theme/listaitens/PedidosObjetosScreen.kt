package com.example.mobilevizinhaa.ui.theme.listaitens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

    // --- FILTROS EM BRUTO SINCRONIZADOS COM OS ENUMS DA API (CAIXA ALTA) ---
    val filtrosAtuais = if (selectedTab == 0) {
        listOf("Todos", "PENDENTE", "IN_PROGRESS", "COMPLETED")
    } else {
        listOf("Todos", "DISPONIVEL", "EMPRESTIMO", "DOACAO", "ACHADOS")
    }

    // --- SEPARAÇÃO LOCAL INTELIGENTE (PEDIDO DE SERVIÇO vs OBJETO) ---
    val dadosFiltradosPorAba = remember(viewModel.listaMeusItens, selectedTab) {
        viewModel.listaMeusItens.filter { item ->
            val tipoCategoriaString = item.category?.typeCategory?.toString()?.uppercase() ?: ""
            if (selectedTab == 0) {
                // Aba Pedidos: Tudo o que NÃO for um objeto
                !tipoCategoriaString.contains("OBJETO")
            } else {
                // Aba Objetos: Apenas categorias classificadas como objeto
                tipoCategoriaString.contains("OBJETO")
            }
        }
    }

    // Aplica o filtro de chips horizontais sobre os dados da aba ativa
    val listaExibida = remember(dadosFiltradosPorAba, filtroSelecionado) {
        if (filtroSelecionado == "Todos") {
            dadosFiltradosPorAba
        } else {
            dadosFiltradosPorAba.filter { it.status.equals(filtroSelecionado, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- HEADER COM O SEU GRADIENTE IGUAL À HOME ---
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

        // --- LAZYROW DE FILTROS (Traduz os Enums da API para textos bonitos na tela) ---
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
                    "IN_PROGRESS" -> "Em andamento"
                    "COMPLETED" -> "Concluído"
                    "DISPONIVEL" -> "Disponível"
                    "EMPRESTIMO" -> "Empréstimo"
                    "DOACAO" -> "Doação"
                    "ACHADOS" -> "Achados"
                    else -> "Todos"
                }

                FilterChip(
                    selected = filtroSelecionado == filtro,
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

        // --- EXIBIÇÃO TRATADA DO CONTEÚDO (LOADING, ERRO, VAZIO OU REAL) ---
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
                // --- LAZYCOLUMN REAL ---
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(listaExibida) { item ->
                        PedidoCard(servico = item)
                    }
                }
            }
        }
    }
}