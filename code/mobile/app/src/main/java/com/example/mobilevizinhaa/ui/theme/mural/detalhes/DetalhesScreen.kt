package com.example.mobilevizinhaa.ui.theme.mural.detalhes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetail

/**
 * Tela de Detalhes do Pedido (Versão totalmente Dinâmica e Segura contra NullPointer).
 */
@Composable
fun MuralDetalheScreen(
    servico: ServiceDetail,
    onBackClick: () -> Unit
) {
    var mostrarAlerta by remember { mutableStateOf(false) }

    // Tratamento seguro da tag de urgência (evita que quebre caso venha nulo)
    val urgenciaString = servico.urgency ?: "BAIXA"
    val textoUrgencia = when (urgenciaString.uppercase()) {
        "ALTA" -> "Urgente"
        "MEDIA", "MÉDIA" -> "Média"
        else -> "Baixa"
    }

    val corUrgencia = when (urgenciaString.uppercase()) {
        "ALTA" -> Color(0xFFEF5350) // Vermelho
        "MEDIA", "MÉDIA" -> Color(0xFFFFB74D) // Laranja
        else -> Color(0xFF81C784) // Verde
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- HEADER COM FOTO ---
            Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.vazamento),
                    contentDescription = "Foto do Pedido",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(Brush.verticalGradient(colors = listOf(Color(0xFF0056B3), Color.Transparent)))
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp).clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Detalhes do Pedido", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }

            // --- CORPO DA TELA TOTALMENTE SEGURO (?.) ---
            Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(20.dp)) {

                // Dados do Morador que criou o pedido
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.wellington),
                        contentDescription = "Foto perfil",
                        modifier = Modifier.size(45.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        // Correção do erro: Acesso seguro usando ?. e operador Elvis ?: para String padrão
                        Text(
                            text = servico.resident?.name ?: "Morador",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        // Verificação segura para exibir o apartamento se ele existir
                        val apto = servico.resident?.apartment
                        if (!apto.isNullOrBlank()) {
                            Text(text = "Apto: $apto", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                // Título real do pedido com fallback seguro
                Text(text = servico.title ?: "Sem título", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(14.dp))

                // Tags de Categorias e Metadados dinâmicos
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {

                    // Correção do erro: Nome da categoria tratado com segurança
                    Box(modifier = Modifier.background(Color(0xFF26A69A), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(text = servico.category?.name ?: "Geral", color = Color.White, fontSize = 13.sp)
                    }

                    // Tempo estimado tratado de forma segura
                    Box(modifier = Modifier.background(Color(0xFFF1F3F5), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(text = "Tempo estimado: ${servico.estimatedTime ?: 0}h", color = Color(0xFF495057), fontSize = 13.sp)
                    }

                    // Urgência calculada acima
                    Box(modifier = Modifier.background(corUrgencia, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(text = textoUrgencia, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Descrição real digitada pelo morador
                Text(
                    text = if (servico.description.isNullOrBlank()) "Nenhuma descrição fornecida para este pedido." else servico.description,
                    fontSize = 16.sp, color = Color(0xFF333333), lineHeight = 24.sp
                )
            }

            // --- BOTÕES INFERIORES ---
            Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onBackClick() },
                    modifier = Modifier.weight(1f).height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Fechar", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { mostrarAlerta = true },
                    modifier = Modifier.weight(1f).height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Oferecer Ajuda", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- DIÁLOGO DE ALERTA SEGURO ---
        if (mostrarAlerta) {
            AlertDialog(
                onDismissRequest = { mostrarAlerta = false },
                title = { Text(text = "Alerta", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black) },
                text = {
                    Text(
                        text = "Você deseja oferecer ajuda para o pedido \"${servico.title ?: "deste serviço"}\"?\nApós confirmar, vocês poderão combinar os detalhes pelo chat.",
                        fontSize = 15.sp,
                        color = Color(0xFF495057)
                    )
                },
                confirmButton = {
                    TextButton(onClick = { mostrarAlerta = false }) {
                        Text("Confirmar", color = Color(0xFF26A69A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarAlerta = false }) {
                        Text("Cancelar", color = Color(0xFFEF5350), fontSize = 16.sp)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                containerColor = Color.White
            )
        }
    }
}