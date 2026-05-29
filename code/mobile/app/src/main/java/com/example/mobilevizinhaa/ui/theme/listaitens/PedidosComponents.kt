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
// SEU COMPONENTE DE CARD ADAPTADO PARA AS CORES DA FOTO
// ==========================================
@Composable
fun PedidoCard(servico: ServiceDetailBackend) {
    // Decodifica a string Base64 vinda do banco em uma imagem usável pelo Compose
    val bitmapDaFoto = lembrarBase64ComoImageBitmap(servico.photoBase64)

    // Converte o status cru em String amigável
    val textoStatusExibicao = when (servico.status.uppercase()) {
        "PENDENTE" -> "Pendente"
        "IN_PROGRESS", "EM_ANDAMENTO" -> "Em andamento"
        "COMPLETED", "CONCLUIDO" -> "Concluído"
        "DISPONIVEL" -> "Disponível"
        "EMPRESTIMO" -> "Empréstimo"
        "DOACAO" -> "Doação"
        else -> servico.status
    }

    // RESTAURADO: Mapeamento EXATO de par de cores (Fundo e Texto) baseado na foto enviada
    val (backgroundColor, textColor) = when (textoStatusExibicao) {
        "Pendente" -> {
            Color(0xFFFFEAD2) to Color(0xFFFF9800) // Laranja Pastel da foto
        }
        "Em andamento", "Empréstimo" -> {
            Color(0xFFE3F2FD) to Color(0xFF1E88E5) // Azul suave da foto
        }
        "Concluído", "Disponível", "Doação" -> {
            Color(0xFFE8F5E9) to Color(0xFF4CAF50) // Verde claro da foto
        }
        else -> {
            Color(0xFFF5F5F5) to Color(0xFF616161) // Cinza padrão de fallback
        }
    }

    // Trata e formata a data de criação retornada pelo banco
    val dataExibicao = if (!servico.creationDate.isNullOrBlank()) {
        if (servico.creationDate.length >= 10) servico.creationDate.substring(0, 10) else servico.creationDate
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
            // Imagem lateral dinâmica com fallback seguro contra fotos nulas
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
                // RESTAURADO: Placeholder elegante cinza usando o R.drawable.objeto como na foto
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
                        text = servico.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge de Status oval em formato de pílula com as cores restauradas da foto
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

                // Descrição controlada para até 2 linhas com elipse (...)
                Text(
                    text = servico.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp),
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rodapé com metadados reais vindo do banco
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
            val cleanString = base64String.substringAfter(",")
            val decodedBytes = Base64.decode(cleanString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}