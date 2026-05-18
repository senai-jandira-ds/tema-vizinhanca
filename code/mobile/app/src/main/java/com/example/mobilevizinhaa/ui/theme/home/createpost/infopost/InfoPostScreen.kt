package com.example.mobilevizinhaa.ui.theme.home.detail

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    // 1. Busca a postagem selecionada na lista local de posts
    val post = viewModel.posts.find { it.id == postId }

    // 2. Coleta os dados em tempo real do Morador Logado vindos da API
    val resident by viewModel.residentData.collectAsState()

    // Estado para controlar a abertura do menu de opções (Excluir)
    var menuExpandido by remember { mutableStateOf(false) }

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
                                    viewModel.deletarPost(postId)
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ====================================================================
            // 1. CABEÇALHO DO USUÁRIO (INTEGRADO À API)
            // ====================================================================
            // Passa dinamicamente o nome e a String Base64 vinda do seu Back-end.
            // O seu PostUserHeader vai decodificar e remover a foto da mulher!
            PostUserHeader(
                userName = resident?.name ?: "Vizinho(a)",
                userPhotoUrl = resident?.photo
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
                if (post?.imagemUri != null) {
                    // Se houver uma URI de imagem local ou web
                    AsyncImage(
                        model = post.imagemUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Imagem de fallback para o corpo do post caso não tenha URI
                    Image(
                        painter = painterResource(id = post?.imagemRes ?: R.drawable.mulher),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
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
    }
}