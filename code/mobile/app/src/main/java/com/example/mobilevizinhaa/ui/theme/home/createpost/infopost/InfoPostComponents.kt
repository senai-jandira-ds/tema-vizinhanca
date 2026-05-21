package com.example.mobilevizinhaa.ui.theme.home.createpost.infopost

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mobilevizinhaa.R

@Composable
fun PostUserHeader(userName: String, userPhotoUrl: String? = null) {
    // Verifica se a string recebida é uma URL HTTP de internet
    val isUrlRemota = userPhotoUrl?.startsWith("http", ignoreCase = true) == true

    // Tenta decodificar o Base64 APENAS se não for uma URL comum da web
    val bitmapPerfil = remember(userPhotoUrl) {
        if (!isUrlRemota && !userPhotoUrl.isNullOrBlank()) {
            carregarImagemBase64Local(userPhotoUrl)
        } else null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // CONTAINER DA FOTO COM BORDA E SOMBRA IGUAL À HOME
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(2.dp, CircleShape)
                .border(2.dp, Color.White, CircleShape)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Condição 1: Se for uma URL tradicional de internet, deixa o Coil baixar e renderizar
                isUrlRemota -> {
                    AsyncImage(
                        model = userPhotoUrl,
                        contentDescription = "Foto de perfil de $userName",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.mulher),
                        error = painterResource(id = R.drawable.mulher)
                    )
                }
                // Condição 2: Se for uma imagem convertida de string Base64 do banco
                bitmapPerfil != null -> {
                    Image(
                        bitmap = bitmapPerfil,
                        contentDescription = "Foto de perfil de $userName",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                // Condição 3: Fallback se o link for nulo ou inválido
                else -> {
                    Image(
                        painter = painterResource(id = R.drawable.mulher),
                        contentDescription = "Foto padrão de $userName",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // INFORMAÇÕES DE TEXTO DO AUTOR
        Column {
            Text(
                text = userName.ifBlank { "Vizinho(a)" },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = "Publicado agora",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun PostDescriptionSection(titulo: String, descricao: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = titulo,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Detalhe visual elegante em AZUL abaixo do título do post
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(3.dp)
                .background(Color(0xFF3867F5), RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = descricao,
            fontSize = 15.sp,
            color = Color(0xFF444444),
            lineHeight = 22.sp
        )
    }
}

/**
 * Função utilitária interna para conversão de Base64
 */
private fun carregarImagemBase64Local(base64String: String?): ImageBitmap? {
    if (base64String.isNullOrBlank() || base64String == "string") return null
    return try {
        val stringSemCabecalho = if (base64String.contains(",")) {
            base64String.substring(base64String.indexOf(",") + 1)
        } else {
            base64String
        }

        val stringLimpa = stringSemCabecalho.trim().replace("\n", "").replace("\r", "")

        val bytesDecodificados = Base64.decode(stringLimpa, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytesDecodificados, 0, bytesDecodificados.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}


