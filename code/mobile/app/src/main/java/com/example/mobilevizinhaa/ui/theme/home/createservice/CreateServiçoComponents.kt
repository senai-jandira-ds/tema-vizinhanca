package com.example.mobilevizinhaa.ui.theme.home.createservice

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.mobilevizinhaa.ui.theme.data.CategoryDetail

@Composable
fun SeletorImagemComponent(
    imagemUri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF1C1B1F)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDark) Color(0xFF2D2C30) else Color(0xFFE0E0E0))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imagemUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imagemUri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "Adicione uma imagem",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color.Gray else Color.DarkGray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownCategoriasComponent(
    categorias: List<CategoryDetail>,
    categoriaSelecionada: CategoryDetail?,
    onCategorySelected: (CategoryDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandida by remember { mutableStateOf(false) }
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF1C1B1F)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Categoria",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        ExposedDropdownMenuBox(
            expanded = expandida,
            onExpandedChange = { expandida = !expandida }
        ) {
            OutlinedTextField(
                value = categoriaSelecionada?.name ?: "Selecione",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandida) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3867F5),
                    unfocusedBorderColor = if (isDark) Color(0xFF444444) else Color(0xFFE0E0E0),
                    focusedTextColor = if (isDark) Color.White else Color.Black,
                    unfocusedTextColor = if (isDark) Color.White else Color.Black
                )
            )

            // CORRIGIDO: Modificador limpo + fillMaxWidth() para o encaixe perfeito na Row
            ExposedDropdownMenu(
                expanded = expandida,
                onDismissRequest = { expandida = false },
                modifier = Modifier
                    .exposedDropdownSize()
                    .fillMaxWidth()
                    .background(if (isDark) Color(0xFF2D2C30) else Color.White)
            ) {
                if (categorias.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Carregando...", color = Color.Gray) },
                        onClick = {}
                    )
                } else {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = cat.name ?: "Sem Nome",
                                    color = if (isDark) Color.White else Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = {
                                onCategorySelected(cat)
                                expandida = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownDuracaoComponent(
    opcoesTempo: List<Pair<String, Int>>,
    tempoSelecionado: Pair<String, Int>?,
    onTempoSelected: (Pair<String, Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandida by remember { mutableStateOf(false) }
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF1C1B1F)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Duração Estimada",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        ExposedDropdownMenuBox(
            expanded = expandida,
            onExpandedChange = { expandida = !expandida }
        ) {
            OutlinedTextField(
                value = tempoSelecionado?.first ?: "Selecione",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.AccessTime, null, tint = if (isDark) Color.Gray else Color.DarkGray) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3867F5),
                    unfocusedBorderColor = if (isDark) Color(0xFF444444) else Color(0xFFE0E0E0),
                    focusedTextColor = if (isDark) Color.White else Color.Black,
                    unfocusedTextColor = if (isDark) Color.White else Color.Black
                )
            )

            // CORRIGIDO: Modificador limpo + fillMaxWidth() para o encaixe perfeito na Row
            ExposedDropdownMenu(
                expanded = expandida,
                onDismissRequest = { expandida = false },
                modifier = Modifier
                    .exposedDropdownSize()
                    .fillMaxWidth()
                    .background(if (isDark) Color(0xFF2D2C30) else Color.White)
            ) {
                opcoesTempo.forEach { parTempo ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = parTempo.first,
                                color = if (isDark) Color.White else Color.Black
                            )
                        },
                        onClick = {
                            onTempoSelected(parTempo)
                            expandida = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SeletorUrgenciaComponent(
    urgenciaSelecionada: String,
    onUrgenciaChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF1C1B1F)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Nível de Urgência",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("BAIXA", "MEDIA", "ALTA").forEach { nivel ->
                val selecionado = urgenciaSelecionada == nivel
                val textoExibicao = if (nivel == "MEDIA") "MÉDIA" else nivel

                val corFundoBotao = when {
                    selecionado -> Color(0xFF3867F5)
                    isDark -> Color(0xFF252428)
                    else -> Color(0xFFEFEFEF)
                }

                val corTextoBotao = when {
                    selecionado -> Color.White
                    isDark -> Color(0xFF9E9E9E)
                    else -> Color.DarkGray
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(corFundoBotao)
                        .clickable { onUrgenciaChanged(nivel) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = textoExibicao,
                        color = corTextoBotao,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}