package com.example.mobilevizinhaa.ui.theme.home.createpost.createpost

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ImagePickerCard(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit // MODIFICADO: Corrigido de '() -> Modifier' para '() -> Unit'
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF7F7F9))
            .border(1.dp, Color(0xFFDCDCE0), RoundedCornerShape(16.dp))
            .clickable { onPickImage() },
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUri != null) {
            // Renderiza a pré-visualização da imagem selecionada pelo usuário
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Imagem selecionada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Exibe o layout padrão convidando a abrir a galeria
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AddAPhoto,
                    contentDescription = "Ícone adicionar foto",
                    tint = Color(0xFF3867F5),
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Toque para selecionar da galeria",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}