package com.example.mobilevizinhaa.ui.theme.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.mobilevizinhaa.ui.theme.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel // Recebe a instância única vinda da MainActivity
) {
    // Observa os dados reativos do ViewModel
    val resident by viewModel.residentData.collectAsState()
    val posts = viewModel.posts

    LaunchedEffect(Unit) {
        val tokenSalvo = viewModel.obterTokenSalvo()
        if (tokenSalvo.isNotEmpty()) {
            viewModel.carregarDadosPerfil(tokenSalvo)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Dinâmico com clique para configurações/perfil ativado
            HomeHeader(
                userName = resident?.name ?: "Vizinho(a)",
                apartment = resident?.apartment?.let { "Apto $it" } ?: "Condomínio",
                userPhotoUrl = resident?.photo,
                navController = navController
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Seção de Cards de Atalho
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(
                    titulo = "Meus pedidos",
                    quantity = "3",
                    iconeRes = R.drawable.pedido,
                    onAddClick = {
                        navController.navigate("criar_pedido")
                    }
                )
                InfoCard(
                    titulo = "Meus objetos",
                    quantity = "3",
                    iconeRes = R.drawable.objeto,
                    onAddClick = {
                        navController.navigate("criar_pedido")
                    }
                )
            }

            Text(
                text = "Postagens",
                modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Chamada da seção de fotos em 3 colunas quadradas e retas
            PostGridSection(
                posts = posts,
                viewModel = viewModel,
                onPostClick = { postId ->
                    navController.navigate("detalhe_post/$postId")
                }
            )

            Spacer(modifier = Modifier.height(110.dp))
        }

        // Botão Flutuante (FAB) para criar postagem
        ExtendedFloatingActionButton(
            onClick = { navController.navigate("publicacao") },
            containerColor = BluePrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 16.dp),
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Criar postagem", fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * COMPONENTE DA GRADE DE POSTAGENS - 3 COLUNAS QUADRADAS E RETAS
 */
@Composable
fun PostGridSection(
    posts: List<Post>,
    viewModel: HomeViewModel,
    onPostClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 3000.dp) // Define um limite alto para a expansão do Grid
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp), // Espaçamento horizontal original de 12.dp
        verticalArrangement = Arrangement.spacedBy(12.dp),   // Espaçamento vertical de 12.dp
        userScrollEnabled = false // CRÍTICO: Delega o controle de rolagem para a Column principal
    ) {
        items(posts) { post ->
            val bitmap = remember(post.imagemUrl) {
                if (post.imagemUrl?.startsWith("/9j") == true) {
                    viewModel.carregarImagemBase64MuralLocal(post.imagemUrl)
                } else {
                    null
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Mantém a proporção estritamente 1:1 (Quadrada)
                    .background(Color(0xFFE0E0E0)) // Removido o .clip para deixar os cantos retos
                    .clickable { onPostClick(post.id) },
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // Recorta mantendo o preenchimento total
                    )
                } else if (!post.imagemUrl.isNullOrEmpty() && post.imagemUrl != "string") {
                    AsyncImage(
                        model = post.imagemUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (post.imagemUri != null) {
                    AsyncImage(
                        model = post.imagemUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.objeto),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}