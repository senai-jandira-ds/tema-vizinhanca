package com.example.mobilevizinhaa.ui.theme.home.createobjeto

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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE0E0E0))
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
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownCategoriasComponent(
    categorias: List<CategoryDetail>, // INTEGRADO: Usa diretamente o modelo CategoryDetail do Swagger
    categoriaSelecionada: CategoryDetail?,
    onCategorySelected: (CategoryDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandida by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Categoria", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
        ExposedDropdownMenuBox(
            expanded = expandida,
            onExpandedChange = { expandida = !expandida }
        ) {
            OutlinedTextField(
                value = categoriaSelecionada?.name ?: "Selecione",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandida) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3867F5),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            ExposedDropdownMenu(
                expanded = expandida,
                onDismissRequest = { expandida = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                categorias.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name ?: "Sem Nome") },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownDuracaoComponent(
    opcoesTempo: List<Pair<String, Int>>,
    tempoSelecionado: Pair<String, Int>?,
    onTempoSelected: (Pair<String, Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandida by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Duração Estimada", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
        ExposedDropdownMenuBox(
            expanded = expandida,
            onExpandedChange = { expandida = !expandida }
        ) {
            OutlinedTextField(
                value = tempoSelecionado?.first ?: "Selecione",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.AccessTime, null, tint = Color.Gray) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3867F5),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            ExposedDropdownMenu(
                expanded = expandida,
                onDismissRequest = { expandida = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                opcoesTempo.forEach { parTempo ->
                    DropdownMenuItem(
                        text = { Text(parTempo.first) },
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
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Nível de Urgência", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mapeia visualmente com acentuação se preferir, mas mantém a chave estável em letras maiúsculas
            listOf("BAIXA", "MEDIA", "ALTA").forEach { nivel ->
                val selecionado = urgenciaSelecionada == nivel

                // Texto de exibição amigável na Interface do Usuário
                val textoExibicao = if (nivel == "MEDIA") "MÉDIA" else nivel

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selecionado) Color(0xFF3867F5) else Color(0xFFEFEFEF))
                        .clickable { onUrgenciaChanged(nivel) }, // Repassa a String correta sem acento para a ViewModel
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = textoExibicao,
                        color = if (selecionado) Color.White else Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}