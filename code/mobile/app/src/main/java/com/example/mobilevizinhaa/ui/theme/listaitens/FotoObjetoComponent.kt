package com.example.mobilevizinhaa.ui.theme.listaitens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun FotoObjetoComponent(
    photoData: String?,
    modifier: Modifier = Modifier
) {
    if (photoData.isNullOrEmpty()) {
        // Se não tiver imagem, você pode colocar um comportamento padrão aqui futuramente
        return
    }

    // Identifica se o texto guardado é uma URL da internet (Azure)
    if (photoData.startsWith("http")) {
        // Decodifica os caracteres como "%2F" para uma barra "/" comum
        val urlFormatada = remember(photoData) {
            try {
                URLDecoder.decode(photoData, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                photoData
            }
        }

        AsyncImage(
            model = urlFormatada,
            contentDescription = "Foto carregada via URL",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        // Se não começar com http, o app trata como String Base64 do banco antigo
        val bitmap = remember(photoData) {
            try {
                val decodedString = Base64.decode(photoData, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            } catch (e: Exception) {
                null
            }
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Foto carregada via Base64",
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        }
    }
}