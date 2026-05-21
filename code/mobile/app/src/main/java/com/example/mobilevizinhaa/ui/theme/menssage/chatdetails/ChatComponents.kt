package com.example.mobilevizinhaa.ui.theme.menssage.chatdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevizinhaa.ui.theme.BluePrimary
import com.example.mobilevizinhaa.ui.theme.menssage.ChatConversation
import com.example.mobilevizinhaa.ui.theme.menssage.Message
import coil.compose.AsyncImage

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
            // Usamos Column aqui para a imagem ficar SOBRE o texto
            Column(
                modifier = Modifier
                    .padding(all = 4.dp) // Espaço interno do balão
                    .widthIn(max = 280.dp)
            ) {
                // 1. SE TIVER IMAGEM, MOSTRA PRIMEIRO
                message.imageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .clip(RoundedCornerShape(10.dp)), // Imagem com cantos arredondados
                        contentScale = ContentScale.Crop
                    )

                    if (message.text.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // 2. TEXTO E HORA
                // Usamos um Box ou Row para alinhar a hora no cantinho inferior
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
        Row(modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 8.dp, start = 8.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
            Image(painter = painterResource(id = chat.profileImage), contentDescription = null, modifier = Modifier.size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = chat.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ChatInputBar(
    text: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onGalleryClick: () -> Unit, // Clique para abrir Galeria (o ícone de clipe)
    onCameraClick: () -> Unit   // Clique para abrir Câmera real
) {
    Surface(
        color = BluePrimary, // Certifique-se que BluePrimary está definido no seu Color.kt
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .navigationBarsPadding()
                .imePadding(),
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
                    cursorColor = BluePrimary
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