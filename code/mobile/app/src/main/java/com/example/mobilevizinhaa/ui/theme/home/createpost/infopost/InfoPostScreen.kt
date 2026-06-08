package com.example.mobilevizinhaa.ui.theme.home.detail

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.home.HomeViewModel
import com.example.mobilevizinhaa.ui.theme.home.createpost.infopost.PostDescriptionSection
import com.example.mobilevizinhaa.ui.theme.home.createpost.infopost.PostUserHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhePostagemScreen(
    postId: Int,
    navController: NavController,
    viewModel: HomeViewModel
) {
    // 1. Busca a postagem selecionada na lista local de posts do ViewModel
    val post = viewModel.posts.find { it.id == postId }

    // 2. Coleta os dados em tempo real do Morador Logado e o status de carregamento
    val resident by viewModel.residentData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Estado para controlar a abertura do menu de opções (Excluir)
    var menuExpandido by remember { mutableStateOf(false) }

    // Verifica se a imagem DO POST vinda do banco é um link de internet (HTTP)
    val isPostImgUrlRemota = post?.imagemUrl?.startsWith("http", ignoreCase = true) == true

    // Caso a imagem do post venha em formato Base64 puro do banco, decodifica aqui
    val bitmapPostImage = remember(post?.imagemUrl) {
        if (!isPostImgUrlRemota && !post?.imagemUrl.isNullOrBlank()) {
            carregarImagemBase64MuralLocal(post.imagemUrl)
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicação", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color(0xFF3867F5))
                    }
                },
                actions = {
                    // Só exibe o menu de opções se houver um post carregado
                    if (post != null) {
                        Box {
                            IconButton(onClick = { menuExpandido = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Opções", tint = Color.Gray)
                            }

                            DropdownMenu(
                                expanded = menuExpandido,
                                onDismissRequest = { menuExpandido = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Excluir postagem", color = Color.Red) },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
                                    onClick = {
                                        menuExpandido = false
                                        // 🎯 CHAMADA TOTALMENTE INTEGRADA: Passa o callback para voltar à tela anterior após o sucesso do DELETE na API
                                        viewModel.deletarPost(postId = postId, onSuccess = {
                                            navController.popBackStack()
                                        })
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {

                // ====================================================================
                // 1. CABEÇALHO DO USUÁRIO CORRIGIDO
                // ====================================================================
                PostUserHeader(
                    userName = resident?.name ?: "Morador",
                    userPhotoUrl = resident?.photo // Repassa a string Base64 ou URL da foto de perfil
                )

                // ====================================================================
                // 2. IMAGEM PRINCIPAL DA POSTAGEM
                // ====================================================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color(0xFFF5F5F5))
                ) {
                    when {
                        // Condição A: Se for um link HTTP da internet (Nuvem)
                        isPostImgUrlRemota -> {
                            AsyncImage(
                                model = post?.imagemUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        // Condição B: Se for uma string Base64 decodificada do banco
                        bitmapPostImage != null -> {
                            Image(
                                bitmap = bitmapPostImage,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        // Condição C: Se for uma URI de mídia local do dispositivo
                        post?.imagemUri != null -> {
                            AsyncImage(
                                model = post.imagemUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        // Condição D: Fallback caso o post não possua foto nenhuma
                        else -> {
                            Image(
                                painter = painterResource(id = post?.imagemRes ?: R.drawable.mulher),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // ====================================================================
                // 3. SEÇÃO DE TEXTO (TÍTULO E DESCRIÇÃO)
                // ====================================================================
                PostDescriptionSection(
                    titulo = post?.titulo ?: "Publicação sem título",
                    descricao = post?.descricao ?: "Nenhuma descrição fornecida."
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            // 🎯 CAMADA VISUAL DE LOADING ASSÍNCRONO: Aparece como um overlay suave bloqueando cliques repetidos se estiver deletando na API
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF3867F5))
                }
            }
        }
    }
}

/**
 * Função utilitária para limpar o cabeçalho e converter o Base64 das fotos do Mural
 */
private fun carregarImagemBase64MuralLocal(base64String: String?): ImageBitmap? {
    if (base64String.isNullOrBlank() || base64String == "string") return null
    return try {
        val stringLimpa = if (base64String.contains(",")) {
            base64String.substring(base64String.indexOf(",") + 1)
        } else {
            base64String
        }.trim()

        val bytesDecodificados = Base64.decode(stringLimpa, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytesDecodificados, 0, bytesDecodificados.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}