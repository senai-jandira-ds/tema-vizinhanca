package com.example.mobilevizinhaa.ui.theme.home.createpost.createpost

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    // Observa reativamente a variável de estado de sucesso vinda do seu HomeViewModel
    val postCriadoComSucesso by viewModel.postCriadoComSucesso.collectAsState()

    // Estados locais do formulário
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var imagemUri by remember { mutableStateOf<Uri?>(null) }

    // MONITOR DE SUCESSO: Quando o banco salva e a lista atualiza, a tela fecha sozinha
    LaunchedEffect(postCriadoComSucesso) {
        if (postCriadoComSucesso) {
            Toast.makeText(context, "Publicado com sucesso!", Toast.LENGTH_SHORT).show()
            viewModel.resetarEstadoSucesso() // Reseta o estado no ViewModel para evitar loops ao reabrir a tela
            navController.popBackStack() // Retorna para a Home com o feed atualizado em tempo real
        }
    }

    // Contrato reativo para abrir a galeria de fotos do smartphone
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagemUri = uri
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // ====================================================================
        // --- HEADER COM DEGRADÊ ---
        // ====================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF3867F5), Color(0xFF7098FF))
                    )
                )
                .statusBarsPadding()
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

        // ====================================================================
        // --- FORMULÁRIO DE ENTRADA DE DADOS ---
        // ====================================================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // --- ÁREA DE SELEÇÃO DE IMAGEM ---
            Text(text = "Foto do item", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))

            // Componente integrado que renderiza a pré-visualização ou o botão de adicionar
            ImagePickerCard(
                selectedImageUri = imagemUri,
                onPickImage = {
                    if (!isLoading) {
                        galleryLauncher.launch("image/*")
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- CAMPO DO TÍTULO ---
            Text(text = "Título", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                placeholder = { Text("Ex: Cadeira de escritório") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BordaFocada,
                    unfocusedBorderColor = Color(0xFFC7C7C7)
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- CAMPO DA DESCRIÇÃO ---
            Text(text = "Descrição", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                placeholder = { Text("Dê detalhes sobre o item ou pedido...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BordaFocada,
                    unfocusedBorderColor = BordaDescricao
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ====================================================================
            // --- BOTÕES DE AÇÃO ---
            // ====================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botão Cancelar
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f).height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    Text("Cancelar", color = Color.DarkGray, fontWeight = FontWeight.Bold)
                }

                // Botão Enviar
                Button(
                    onClick = {
                        // Validação assistiva: avisa o usuário o que falta preencher antes de chamar o ViewModel
                        if (imagemUri == null) {
                            Toast.makeText(context, "Por favor, adicione uma imagem para o item", Toast.LENGTH_SHORT).show()
                        } else if (titulo.isBlank()) {
                            Toast.makeText(context, "Por favor, insira um título", Toast.LENGTH_SHORT).show()
                        } else if (descricao.isBlank()) {
                            Toast.makeText(context, "Por favor, insira uma descrição", Toast.LENGTH_SHORT).show()
                        } else {
                            // Se todos os campos estiverem preenchidos corretamente, envia para a API
                            viewModel.adicionarPostNoBanco(
                                titulo = titulo,
                                descricao = descricao,
                                uriImagem = imagemUri
                            )
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f).height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Enviar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ====================================================================
// --- COMPONENTE AUXILIAR DE SELEÇÃO DE IMAGEM ---
// ====================================================================
