package com.example.mobilevizinhaa.ui.theme.home.createpost.createpost

import android.R
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobilevizinhaa.ui.theme.BordaDescricao
import com.example.mobilevizinhaa.ui.theme.BordaFocada
import com.example.mobilevizinhaa.ui.theme.home.HomeViewModel

@Composable
fun PublicacaoScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var imagemUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para abrir a galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imagemUri = uri }

    // Validação para o botão Enviar
    val podeEnviar = titulo.isNotBlank() && descricao.isNotBlank() && imagemUri != null

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF3867F5), Color(0xFF7098FF))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = "Publicação",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // --- FORMULÁRIO ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // --- ÁREA DE IMAGE UPLOAD (ESTILIZADA) ---
            Text(text = "Foto do item", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (imagemUri == null) Color(0xFFF0F4FF) else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (imagemUri == null) Color(0xFF3867F5).copy(alpha = 0.3f) else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imagemUri != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = imagemUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Badge de trocar foto
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                "Trocar Foto",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_camera),
                            contentDescription = null,
                            modifier = Modifier.size(42.dp),
                            tint = Color(0xFF3867F5)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Adicionar imagem", color = Color(0xFF3867F5), fontWeight = FontWeight.Bold)
                        Text("Clique para abrir a galeria", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TÍTULO ---
            Text(text = "Título", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                placeholder = { Text("Ex: Cadeira de escritório") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BordaFocada,
                    unfocusedBorderColor = Color(0xFFC7C7C7)
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- DESCRIÇÃO ---
            Text(text = "Descrição", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                placeholder = { Text("Dê detalhes sobre o item ou pedido...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BordaFocada,
                    unfocusedBorderColor = BordaDescricao
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- BOTÕES ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cancelar
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f).height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar", color = Color.DarkGray, fontWeight = FontWeight.Bold)
                }

                // Enviar (INTEGRADO COM O SEU BANCO DE DADOS REMOTE)
                Button(
                    onClick = {
                        // Converte a Uri da imagem para String e dispara o POST real no seu servidor
                        viewModel.adicionarPostNoBanco(
                            titulo = titulo,
                            descricao = descricao,
                            fotoUrlOuBase64 = imagemUri?.toString()
                        )
                        navController.popBackStack()
                    },
                    enabled = podeEnviar, // Botão fica cinza se não validar
                    modifier = Modifier.weight(1f).height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Enviar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}