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
    onPickImage: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selectedImageUri == null) Color(0xFFF0F4FF) else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (selectedImageUri == null) Color(0xFF3867F5).copy(alpha = 0.3f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onPickImage() },
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUri != null) {
            // --- ESTADO: IMAGEM SELECIONADA ---
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Pré-visualização da imagem escolhida",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Etiqueta escura semi-transparente indicando re-seleção
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "Trocar Foto",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // --- ESTADO PADRÃO: PLACEHOLDER AZUL DE ADICIONAR ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Ícone de Câmera",
                    modifier = Modifier.size(42.dp),
                    tint = Color(0xFF3867F5)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Adicionar imagem",
                    color = Color(0xFF3867F5),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "Clique para abrir a galeria",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}