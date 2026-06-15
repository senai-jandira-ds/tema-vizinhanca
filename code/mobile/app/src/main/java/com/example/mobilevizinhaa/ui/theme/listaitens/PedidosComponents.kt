package com.example.mobilevizinhaa.ui.theme.listaitens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PedidoCard(
    titulo: String,
    descricao: String,
    status: String,
    categoria: String,
    data: String,
    imagemRes: Int
) {

    val statusColor = when (status) {
        "Pendente" -> Color(0xFFFDE6B0)
        "Em andamento" -> Color(0xFFFFF9C4)
        "Concluído" -> Color(0xFFC8E6C9)
        else -> Color(0xFFF5F5F5)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp), // Espaçamento menor entre cards
        shape = RoundedCornerShape(20.dp), // Bordas mais arredondadas como no design
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Centraliza imagem e texto
        ) {
            // Imagem com bordas suaves
            Image(
                painter = painterResource(id = imagemRes),
                contentDescription = null,
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    // Badge de Status em formato de pílula (CircleShape)
                    Surface(
                        color = statusColor,
                        shape = CircleShape // Deixa o botão de status oval/redondo
                    ) {
                        Text(
                            text = status,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.7f)
                        )
                    }
                }

                // Descrição com ajuste de altura de linha
                Text(
                    text = descricao,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rodapé do card
                Text(
                    text = "$categoria - $data",
                    fontSize = 11.sp,
                    color = Color.DarkGray.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}