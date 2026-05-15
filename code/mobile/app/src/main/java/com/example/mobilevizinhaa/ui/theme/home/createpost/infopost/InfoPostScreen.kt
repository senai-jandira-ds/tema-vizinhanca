package com.example.mobilevizinhaa.ui.theme.home.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val post = viewModel.posts.find { it.id == postId }

    // Estado para controlar a abertura do menu de opções
    var menuExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicação", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3867F5))
                    }
                },
                actions = {
                    // Menu de opções (Três pontinhos)
                    Box {
                        IconButton(onClick = { menuExpandido = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
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
                                    viewModel.deletarPost(postId) // Chama a função no ViewModel
                                    navController.popBackStack() // Volta para a Home
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
            // 1. Cabeçalho do Usuário (Sarah)
            PostUserHeader(userName = "Sarah", profileImageRes = R.drawable.mulher)

            // 2. Imagem Principal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF5F5F5))
            ) {
                if (post?.imagemUri != null) {
                    AsyncImage(
                        model = post.imagemUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = post?.imagemRes ?: R.drawable.mulher),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // 3. Título e Descrição
            PostDescriptionSection(
                titulo = post?.titulo ?: "",
                descricao = post?.descricao ?: ""
            )

            Spacer(modifier = Modifier.height(24.dp))


            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}