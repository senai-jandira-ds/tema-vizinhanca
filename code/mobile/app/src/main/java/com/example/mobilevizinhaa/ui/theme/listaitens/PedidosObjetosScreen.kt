package com.example.mobilevizinhaa.ui.theme.listaitens

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.*

// Modelo de dados único para representar os itens do mural na tela
data class ItemMock(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val status: String,
    val categoria: String,
    val data: String,
    val imagemRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosObjetosScreen() {
    // Controla o estado da aba atual: 0 para Pedidos, 1 para Objetos
    var selectedTab by remember { mutableIntStateOf(0) }
    var filtroSelecionado by remember { mutableStateOf("Todos") }

    // Reseta o filtro horizontal para "Todos" toda vez que o valor da Tab mudar
    LaunchedEffect(selectedTab) {
        filtroSelecionado = "Todos"
    }

    // --- TROCA DE VALOR DOS FILTROS (COMPONENTES HORIZONTAIS) ---
    val filtrosAtuais = if (selectedTab == 0) {
        listOf("Todos", "Pendente", "Em andamento", "Concluído")
    } else {
        listOf("Todos", "Disponível", "Empréstimo", "Doação", "Achados")
    }

    // --- TROCA DE VALOR DA LISTA DE DADOS DE FORMA DIRETA ---
    val dadosAtuaisDoMural = if (selectedTab == 0) {
        listOf(
            ItemMock(1, "Arrumar disjuntor", "O disjuntor não para de desarmar... toda hora cai a energia, principalmente quando...", "Pendente", "Manutenção", "27/03/2026", R.drawable.mulher),
            ItemMock(2, "Vazamento na pia", "Gente, alguém mais já passou por isso? 😩 Tô com um vazamento na cozinha...", "Em andamento", "Reparo", "28/03/2026", R.drawable.mulher)
        )
    } else {
        listOf(
            ItemMock(3, "Furadeira de Impacto", "Empresto furadeira Mondial com jogo de brocas completo. Favor devolver limpa.", "Disponível", "Ferramentas", "15/05/2026", R.drawable.mulher),
            ItemMock(4, "Chave de Fenda Extensa", "Alguém teria uma chave de fenda longa para me emprestar hoje à tarde?", "Empréstimo", "Empréstimo", "17/05/2026", R.drawable.mulher),
            ItemMock(5, "Cadeira de Escritório", "Estou desapegando desta cadeira antiga, rodinhas perfeitas, apenas marcas de uso.", "Doação", "Móveis", "18/05/2026", R.drawable.mulher)
        )
    }

    // Aplica o filtro de chips sobre os dados que mudaram de valor
    val listaExibida = if (filtroSelecionado == "Todos") {
        dadosAtuaisDoMural
    } else {
        dadosAtuaisDoMural.filter { it.status == filtroSelecionado }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- HEADER COM SEU GRADIENTE ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(brush = Brush.verticalGradient(colors = listOf(GradientBlueStart, GradientBlueEnd)))
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

        // --- LAZYROW DE FILTROS (Muda os valores dinamicamente) ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(filtrosAtuais) { filtro ->
                FilterChip(
                    selected = filtroSelecionado == filtro,
                    onClick = { filtroSelecionado = filtro },
                    label = { Text(filtro) },
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

        // --- LAZYCOLUMN (Consome a lista que trocou de valor) ---
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(listaExibida) { item ->
                PedidoCard(
                    titulo = item.titulo,
                    descricao = item.descricao,
                    status = item.status,
                    categoria = item.categoria,
                    data = item.data,
                    imagemRes = item.imagemRes
                )
            }
        }
    }
}
