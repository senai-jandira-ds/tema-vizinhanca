package com.example.mobilevizinhaa.ui.theme.menssage.chatdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Send
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
import coil.compose.AsyncImage // 🎯 Importante para carregar imagens da internet
import com.example.mobilevizinhaa.ui.theme.BluePrimary
import com.example.mobilevizinhaa.ui.theme.menssage.ChatConversation
import com.example.mobilevizinhaa.ui.theme.menssage.Message

@Composable
fun WhatsAppBubble(message: Message) {
    val bubbleColor = if (message.isFromMe) BluePrimary else Color.White
    val textColor = if (message.isFromMe) Color.White else Color.Black
    val alignment = if (message.isFromMe) Alignment.End else Alignment.Start

    // Define o formato do balão (cantinho pontudo no lado certo)
    val shape = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 12.dp,
        bottomStart = if (message.isFromMe) 12.dp else 0.dp,
        bottomEnd = if (message.isFromMe) 0.dp else 12.dp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = shape,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 4.dp) // Espaço interno do balão
                    .widthIn(max = 280.dp)
            ) {
                // Se no futuro houver imagens trafegando, ela renderiza aqui com segurança
                if (message.text.startsWith("📷")) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = if (message.isFromMe) Color.White else Color.Gray,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                // TEXTO E HORA
                Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
                    if (message.text.isNotEmpty()) {
                        Text(
                            text = message.text,
                            fontSize = 16.sp,
                            color = textColor
                        )
                    }

                    Text(
                        text = message.time,
                        fontSize = 10.sp,
                        color = if (message.isFromMe) Color(0xFFD1E3FF) else Color.Gray,
                        modifier = Modifier.align(Alignment.End) // Joga a hora para a direita
                    )
                }
            }
        }
    }
}

@Composable
fun ChatHeader(chat: ChatConversation, onBack: () -> Unit) {
    Surface(color = BluePrimary, shadowElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 8.dp, start = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }

            // 🎯 CORREÇÃO CRÍTICA: Substituído o Image local por AsyncImage para ler a URL do servidor
            if (!chat.profileImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = chat.profileImageUrl,
                    contentDescription = "Foto de ${chat.name}",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback elegante caso o morador não tenha imagem cadastrada no sistema
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Sem foto",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = chat.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(
    text: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onGalleryClick: () -> Unit, // Clique para abrir Galeria
    onCameraClick: () -> Unit   // Clique para abrir Câmera real
) {
    Surface(
        color = BluePrimary,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .navigationBarsPadding()
                .imePadding(), // Faz com que a barra suba junto com o teclado do Android automaticamente
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BOTÃO DA GALERIA (Anexo)
            IconButton(onClick = onGalleryClick) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Galeria",
                    tint = Color.White
                )
            }

            TextField(
                value = text,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape),
                placeholder = { Text("Digitar...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = BluePrimary,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            // BOTÃO DA CÂMERA
            IconButton(onClick = onCameraClick) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Câmera",
                    tint = Color.White
                )
            }

            // BOTÃO DE ENVIAR
            IconButton(onClick = onSendClick) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = Color.White
                )
            }
        }
    }
}