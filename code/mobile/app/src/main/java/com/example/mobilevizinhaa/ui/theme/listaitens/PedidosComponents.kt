package com.example.mobilevizinhaa.ui.theme.listaitens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend

// Cores padrão mantidas para o Header Gradiente
val GradientBlueStart = Color(0xFF2196F3)
val GradientBlueEnd = Color(0xFF42A5F5)
val BluePrimary = Color(0xFF2196F3)

// ==========================================
// COMPONENTE DE CARD TOTALMENTE BLINDADO
// ==========================================
@Composable
fun PedidoCard(servico: ServiceDetailBackend) {
    // Decodifica de forma segura a string Base64 vinda do banco usando a função abaixo
    val bitmapDaFoto = lembrarBase64ComoImageBitmap(servico.photoBase64)

    // Converte o status garantindo proteção total contra nulos corporativos
    val statusCru = servico.status?.uppercase()?.trim() ?: "PENDENTE"

    // 🎯 Mantém o mapeamento visual idêntico ao esperado pelas validações da Screen
    val textoStatusExibicao = when (statusCru) {
        "PENDENTE" -> "Pendente"
        "IN_PROGRESS", "EM_ANDAMENTO" -> "Em andamento"
        "COMPLETED", "CONCLUIDO" -> "Concluído"
        "INDISPONÍVEL" -> "Indisponível"
        "DISPONÍVEL" -> "Disponível"
        "EMPRESTADO" -> "Emprestado"
        "DOACAO", "DOAÇÃO" -> "Doação"
        "ACHADOS" -> "Achados"
        else -> statusCru.lowercase().replaceFirstChar { it.uppercase() }
    }

    // Mapeamento exato de par de cores (Fundo e Texto) baseado nos status do backend
    val (backgroundColor, textColor) = when (statusCru) {
        "PENDENTE" -> {
            Color(0xFFFFEAD2) to Color(0xFFFF9800) // Laranja Pastel
        }
        "IN_PROGRESS", "EM_ANDAMENTO", "EMPRESTADO" -> {
            Color(0xFFE3F2FD) to Color(0xFF1E88E5) // Azul suave
        }
        "COMPLETED", "CONCLUIDO", "DISPONIVEL", "DOACAO", "DOAÇÃO", "ACHADOS" -> {
            Color(0xFFE8F5E9) to Color(0xFF4CAF50) // Verde claro
        }
        "INDISPONIVEL" -> {
            Color(0xFFFCE4EC) to Color(0xFFD81B60) // Rosa/Vermelho Pastel suave
        }
        else -> {
            Color(0xFFF5F5F5) to Color(0xFF616161) // Cinza padrão
        }
    }

    // Tratamento ultra seguro de datas para evitar crashes de String
    val dataCrua = servico.creationDate
    val dataExibicao = if (!dataCrua.isNullOrBlank()) {
        if (dataCrua.length >= 10) dataCrua.substring(0, 10) else dataCrua
    } else {
        "Recentemente"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem lateral com fallback seguro
            if (bitmapDaFoto != null) {
                Image(
                    bitmap = bitmapDaFoto,
                    contentDescription = null,
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder caso a imagem falhe ou venha nula
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .background(Color(0xFFE9ECEF), RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.objeto),
                        contentDescription = "Sem imagem",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = servico.title ?: "Sem título",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge de Status com formato pílula dinâmico
                    Surface(
                        color = backgroundColor,
                        shape = CircleShape
                    ) {
                        Text(
                            text = textoStatusExibicao,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }

                // Descrição com elipse controlada para nulos
                Text(
                    text = servico.description ?: "Nenhuma descrição fornecida.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp),
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rodapé contendo Categoria tratada contra nulos e data formatada
                val nomeCategoria = servico.category?.name ?: "Geral"
                Text(
                    text = "$nomeCategoria - $dataExibicao",
                    fontSize = 11.sp,
                    color = Color.DarkGray.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

// ==========================================
// FUNÇÃO AUXILIAR PARA PROCESSAMENTO DE IMAGEM BASE64
// ==========================================
@Composable
fun lembrarBase64ComoImageBitmap(base64String: String?): ImageBitmap? {
    if (base64String.isNullOrBlank()) return null
    return remember(base64String) {
        try {
            val cleanString = if (base64String.contains(",")) {
                base64String.substringAfter(",")
            } else {
                base64String
            }
            val decodedBytes = Base64.decode(cleanString.trim(), Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}