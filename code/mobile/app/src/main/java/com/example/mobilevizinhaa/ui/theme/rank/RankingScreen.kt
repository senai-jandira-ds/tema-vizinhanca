package com.example.mobilevizinhaa.ui.theme.rank

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilevizinhaa.ui.theme.BluePrimary
import com.example.mobilevizinhaa.ui.theme.GradientBlueEnd
import com.example.mobilevizinhaa.ui.theme.GradientBlueStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(viewModel: RankingViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    val opcoesFiltro = listOf("Hoje", "Semana", "Mês", "Ano")

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        // Cabeçalho
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(brush = Brush.verticalGradient(colors = listOf(GradientBlueStart, GradientBlueEnd)))
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Ranking", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Filtro ComboBox (Dropdown)
        Box(modifier = Modifier.padding(16.dp)) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = uiState.filtroSelecionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Período") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().width(180.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    opcoesFiltro.forEach { opcao ->
                        DropdownMenuItem(
                            text = { Text(opcao) },
                            onClick = {
                                viewModel.onFiltroChanged(opcao)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            uiState.usuarioLogado?.let {
                item {
                    Text("Sua posição", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    RankingCard(usuario = it)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            item {
                Text("Top 10", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            }

            items(uiState.topDez) { usuario ->
                RankingCard(usuario = usuario, isDestaque = usuario.posicao == 1)
            }
        }
    }
}