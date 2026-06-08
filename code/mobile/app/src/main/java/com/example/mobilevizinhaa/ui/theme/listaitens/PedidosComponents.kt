package com.example.mobilevizinhaa.ui.theme.listaitens

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend

val GradientBlueStart = Color(0xFF2196F3)
val GradientBlueEnd = Color(0xFF42A5F5)
val BluePrimary = Color(0xFF2196F3)

// ==========================================
// COMPONENTE HÍBRIDO BLINDADO COM LOGS DE MONITORAMENTO
// ==========================================
@Composable
fun PedidoCard(servico: ServiceDetailBackend) {

    val stringDaFoto = servico.photoBase64?.trim()

    // Identificação Inteligente do Tipo de Conteúdo (URLs da web ou caminhos de servidores)
    val ehUrlDeInternet = remember(stringDaFoto) {
        if (stringDaFoto.isNullOrBlank()) false else {
            stringDaFoto.startsWith("http://", ignoreCase = true) ||
                    stringDaFoto.startsWith("https://", ignoreCase = true) ||
                    stringDaFoto.contains("blob.core.windows.net", ignoreCase = true) ||
                    stringDaFoto.contains(".jpg", ignoreCase = true) ||
                    stringDaFoto.contains(".png", ignoreCase = true)
        }
    }

    // Processa como Base64 apenas se NÃO for uma URL da Web
    val bitmapDaFoto = if (!ehUrlDeInternet) {
        lembrarBase64ComoImageBitmap(stringDaFoto)
    } else {
        null
    }

    // Normalização estável e segura de status
    val statusCru = servico.status?.uppercase()?.trim()
        ?.replace("Í", "I")?.replace("Á", "A")
        ?.replace("Ã", "A")?.replace("Ç", "C") ?: "PENDENTE"

    val textoStatusExibicao = when (statusCru) {
        "PENDENTE" -> "Pendente"
        "IN_PROGRESS", "EM_ANDAMENTO" -> "Em andamento"
        "COMPLETED", "CONCLUIDO" -> "Concluído"
        "INDISPONIVEL" -> "Indisponível"
        "DISPONIVEL" -> "Disponível"
        "EMPRESTADO" -> "Emprestado"
        "DOACAO" -> "Doação"
        "ACHADOS" -> "Achados"
        else -> statusCru.lowercase().replaceFirstChar { it.uppercase() }
    }

    val (backgroundColor, textColor) = when (statusCru) {
        "PENDENTE" -> { Color(0xFFFFEAD2) to Color(0xFFFF9800) }
        "IN_PROGRESS", "EM_ANDAMENTO", "EMPRESTADO" -> { Color(0xFFE3F2FD) to Color(0xFF1E88E5) }
        "COMPLETED", "CONCLUIDO", "DISPONIVEL", "DOACAO", "ACHADOS" -> { Color(0xFFE8F5E9) to Color(0xFF4CAF50) }
        "INDISPONIVEL" -> { Color(0xFFFCE4EC) to Color(0xFFD81B60) }
        else -> { Color(0xFFF5F5F5) to Color(0xFF616161) }
    }

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

            // CONTAINER DE IMAGEM ADAPTATIVO
            if (ehUrlDeInternet && !stringDaFoto.isNullOrBlank()) {
                // 1️⃣ Cenário: Link Web / Nuvem (Objeto). Monitorado com Listeners via Logcat.
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(stringDaFoto)
                        .crossfade(true)
                        .listener(
                            onStart = { Log.i("COIL_DEBUG", "Iniciando download da imagem ID ${servico.id}: $stringDaFoto") },
                            onSuccess = { _, _ -> Log.i("COIL_DEBUG", "Sucesso ao carregar imagem do ID ${servico.id}") },
                            onError = { _, result ->
                                Log.e("COIL_DEBUG", "Erro crítico no Coil para o ID ${servico.id}! Causa: ${result.throwable.message}", result.throwable)
                            }
                        )
                        .placeholder(R.drawable.objeto)
                        .error(R.drawable.objeto)
                        .build(),
                    contentDescription = "Foto vinda da URL da API",
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop
                )
            } else if (bitmapDaFoto != null) {
                // 2️⃣ Cenário: Dados Base64 locais (Serviço).
                Image(
                    bitmap = bitmapDaFoto,
                    contentDescription = "Foto vinda de String Base64",
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 3️⃣ Cenário: Sem imagem cadastrada ou formato corrompido ("string" ou nulo).
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .background(Color(0xFFE9ECEF), RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.objeto),
                        contentDescription = "Sem imagem disponível",
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

@Composable
fun lembrarBase64ComoImageBitmap(base64String: String?): ImageBitmap? {
    if (base64String.isNullOrBlank() || base64String.lowercase().trim() == "string") return null
    return remember(base64String) {
        try {
            // Remove cabeçalhos de dados comuns em strings base64 vindas da web
            val cleanString = if (base64String.contains(",")) {
                base64String.substringAfter(",")
            } else {
                base64String
            }
            val decodedBytes = Base64.decode(cleanString.trim(), Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            Log.e("BASE64_DECODE", "Falha ao decodificar imagem Base64: ${e.message}")
            null
        }
    }
}