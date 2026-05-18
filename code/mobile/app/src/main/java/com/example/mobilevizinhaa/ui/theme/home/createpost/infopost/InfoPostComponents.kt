package com.example.mobilevizinhaa.ui.theme.home.createpost.infopost

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevizinhaa.R

@Composable
fun PostUserHeader(userName: String, userPhotoUrl: String? = null) {
    // Tenta decodificar o Base64 caso ele exista na conta do usuário
    val bitmapPerfil = remember(userPhotoUrl) { carregarImagemBase64Local(userPhotoUrl) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
        ) {
            if (bitmapPerfil != null) {
                // Se o Base64 do usuário for válido, renderiza a foto real
                Image(
                    bitmap = bitmapPerfil,
                    contentDescription = "Foto de $userName",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback caso o usuário não tenha imagem cadastrada
                Image(
                    painter = painterResource(id = R.drawable.mulher),
                    contentDescription = "Foto de $userName",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = userName.ifBlank { "Vizinho(a)" },
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
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

        Spacer(modifier = Modifier.height(4.dp))

        // Detalhe visual em AZUL
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(3.dp)
                .background(Color(0xFF3867F5), RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = descricao,
            fontSize = 15.sp,
            color = Color(0xFF444444),
            lineHeight = 22.sp
        )
    }
}

/**
 * Função utilitária interna para conversão do Base64 da foto do autor
 */
private fun carregarImagemBase64Local(base64String: String?): ImageBitmap? {
    if (base64String.isNullOrBlank() || base64String == "string") return null
    return try {
        val stringLimpa = if (base64String.contains(",")) {
            base64String.substring(base64String.indexOf(",") + 1)
        } else {
            base64String
        }
        val bytesDecodificados = Base64.decode(stringLimpa, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytesDecodificados, 0, bytesDecodificados.size)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}