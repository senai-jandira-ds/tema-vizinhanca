package com.example.mobilevizinhaa.ui.theme.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.BluePrimary
import com.example.mobilevizinhaa.ui.theme.GrayBackground

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val resident by viewModel.residentData.collectAsState()
    val posts = viewModel.posts
    val focusManager = LocalFocusManager.current

    // Sincroniza os dados do perfil em segundo plano assim que a Home abre
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {

            // Item 1: Header Dinâmico de Perfil
            item(span = { GridItemSpan(3) }) {
                HomeHeader(
                    userName = resident?.name ?: "Vizinho(a)",
                    apartment = resident?.apartment?.let { "Apto $it" } ?: "Condomínio",
                    userPhotoUrl = resident?.photo,
                    navController = navController,
                    viewModel = viewModel
                )
            }

            // Item 2: Espaçador Estrutural
            item(span = { GridItemSpan(3) }) {
                Spacer(modifier = Modifier.height(30.dp))
            }

            // Item 3: Seção reativa de Cards (Pedidos e Objetos vinculados à nova rota de serviços)
            item(span = { GridItemSpan(3) }) {
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
                            focusManager.clearFocus()
                            navController.navigate("criar_pedido") // Abre a tela CreateServiceScreen
                        }
                    )
                    InfoCard(
                        titulo = "Meus objetos",
                        quantity = "3",
                        iconeRes = R.drawable.objeto,
                        onAddClick = {
                            focusManager.clearFocus()
                            navController.navigate("criar_pedido") // Abre a tela CreateServiceScreen
                        }
                    )
                }
            }

            // Item 4: Título da Seção do Mural
            item(span = { GridItemSpan(3) }) {
                Text(
                    text = "Postagens",
                    modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Item 5: Grade reativa de Fotos em formato 3x3
            posts.forEach { post ->
                item {
                    val bitmap = remember(post.imagemUrl) {
                        if (post.imagemUrl != null && !post.imagemUrl.startsWith("http") && post.imagemUrl != "string") {
                            viewModel.carregarImagemBase64MuralLocal(post.imagemUrl)
                        } else {
                            null
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(Color(0xFFF2F2F7))
                            .clickable {
                                focusManager.clearFocus()
                                navController.navigate("detalhe_post/${post.id}")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
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

        // Botão Flutuante (FAB) para criar novas publicações comuns no mural
        ExtendedFloatingActionButton(
            onClick = {
                focusManager.clearFocus()
                navController.navigate("publicacao")
            },
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