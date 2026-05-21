package com.example.mobilevizinhaa.ui.theme.listaitens.criar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CriarPedidoObjetoScreen(navController: NavController) {
    var titulo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var horas by remember { mutableStateOf("") }
    var minutos by remember { mutableStateOf("") }
    var urgencia by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- CABEÇALHO AZUL COM GRADIENTE MANUAL ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(brush = Brush.verticalGradient(colors = listOf(Color(0xFF3867F5), Color(0xFF1A46C7))))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pedido",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- CONTEÚDO SCROLLÁVEL ---
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- BOX IMAGE ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFCCCCCC))
                    .clickable { /* Abrir Galeria */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Adicione uma imagem",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }

            // --- TÍTULO ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Título", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                CustomTextField(value = titulo, onValueChange = { titulo = it }, placeholder = "")
            }

            // --- CATEGORIA E TEMPO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "Categoria", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    CustomTextField(value = categoria, onValueChange = { categoria = it }, placeholder = "")
                }

                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "Tempo estimado", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)

                    OutlinedTextField(
                        value = if (horas.isEmpty() && minutos.isEmpty()) "" else "$horas h | $minutos m",
                        onValueChange = { },
                        readOnly = true,
                        placeholder = { Text("      h |       m", color = Color.Gray.copy(alpha = 0.7f)) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color.Black.copy(alpha = 0.7f)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color(0xFFEFEFEF).copy(alpha = 0.5f),
                            unfocusedContainerColor = Color(0xFFEFEFEF).copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // --- URGÊNCIA ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Urgência", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                CustomTextField(
                    value = urgencia,
                    onValueChange = { urgencia = it },
                    placeholder = "",
                    modifier = Modifier.fillMaxWidth(0.46f)
                )
            }

            // --- DESCRIÇÃO ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Descrição", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3867F5),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    maxLines = 5
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- BOTÕES ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A9CF5))
                ) {
                    Text(text = "Cancelar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { /* Enviar */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CB85C))
                ) {
                    Text(text = "Enviar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}