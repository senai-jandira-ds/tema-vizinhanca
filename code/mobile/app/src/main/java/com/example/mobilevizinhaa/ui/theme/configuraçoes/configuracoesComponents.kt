package com.example.mobilevizinhaa.ui.theme.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevizinhaa.ui.theme.BluePrimary
import com.example.mobilevizinhaa.ui.theme.GradientBlueEnd
import com.example.mobilevizinhaa.ui.theme.GradientBlueStart

// --- COMPONENTE 1: HEADER AZUL INTEGRADO ---
@Composable
fun ProfileHeader(userName: String, userEmail: String, apartment: String) {
    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(brush = Brush.verticalGradient(
                    colors = listOf(GradientBlueStart, GradientBlueEnd)
                ))
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Perfil",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Card de Informações Principais (Flutuando sobre o degradê)
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(160.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Linha de Nome e Foto
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar com iniciais (AS) igual ao print
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(BluePrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val iniciais = userName.split(" ")
                            .take(2)
                            .map { it.firstOrNull()?.uppercase() ?: "" }
                            .joinToString("")

                        Text(
                            text = iniciais.ifEmpty { "U" },
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(text = userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(text = userEmail, fontSize = 14.sp, color = Color.Gray)
                        Text(text = apartment, fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Métricas inferiores (Pedidos, Objetos, Atividades)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem(quantidade = "3", legenda = "Pedidos")
                    MetricItem(quantidade = "2", legenda = "Objetos")
                    MetricItem(quantidade = "5", legenda = "Atividades")
                }
            }
        }
    }
}

@Composable
private fun MetricItem(quantidade: String, legenda: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = quantidade, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3867F5))
        Text(text = legenda, fontSize = 12.sp, color = Color.Gray)
    }
}


@Composable
fun ProfileOptionMenu(
    options: List<Triple<ImageVector, String, () -> Unit>>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { option.third() }
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFF2F5FE), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = option.first,
                            contentDescription = null,
                            tint = Color.DarkGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = option.second, // Exibe de forma limpa o que for passado por parâmetro
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }

                // Linha divisória fina entre os itens (menos no último)
                if (index < options.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFFF6F6F6),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}