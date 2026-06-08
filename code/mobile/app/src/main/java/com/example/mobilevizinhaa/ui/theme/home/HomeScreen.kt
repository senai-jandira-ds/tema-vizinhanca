package com.example.mobilevizinhaa.ui.theme.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
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
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // 🎯 ORDENAÇÃO: Pegamos os posts e ordenamos pelo ID (ou data) decrescente para que o mais recente fique no topo.
    // Se o seu modelo tiver uma propriedade estruturada em Date/String (ex: creationDate), você pode mudar para:
    // sortByDescending { it.creationDate }
    val postsOrdenados = remember(viewModel.posts.size, viewModel.posts) {
        viewModel.posts.sortedByDescending { it.id }
    }

    // Sincroniza os dados do perfil e força o recarregamento do mural assim que a Home abre
    LaunchedEffect(Unit) {
        val tokenSalvo = viewModel.obterTokenSalvo()
        if (tokenSalvo.isNotEmpty()) {
            viewModel.carregarDadosPerfil(tokenSalvo)
            // Caso tenha uma função específica para atualizar o mural ao abrir a tela:
            // viewModel.carregarPostagensMural(tokenSalvo)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
    ) {
        // Uso da Column vertical idêntica ao design original
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // Espaço para não cobrir a barra inferior fixa
        ) {

            // 1. Header Dinâmico de Perfil
            HomeHeader(
                userName = resident?.name ?: "Vizinho(a)",
                apartment = resident?.apartment?.let { "Apto $it" } ?: "Condomínio",
                userPhotoUrl = resident?.photo,
                navController = navController,
                viewModel = viewModel
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 2. Seção de Cards - Alinhamento Perfeito Original
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // InfoCard de Pedidos - Redirecionando para Criar Serviço com a rota correta
                InfoCard(
                    titulo = "Meus pedidos",
                    quantity = "3",
                    iconeRes = R.drawable.pedido,
                    onAddClick = {
                        focusManager.clearFocus()
                        val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val token = sharedPrefs.getString("auth_token", "") ?: ""
                        val idUsuario = sharedPrefs.getInt("auth_user_id", 0)

                        // Rota mapeada exatamente igual à registrada no NavHost da MainActivity para Serviços
                        navController.navigate("criar_servico/$token/$idUsuario")
                    }
                )

                // InfoCard de Objetos - Redirecionando para Criar Objeto com a rota correspondente
                InfoCard(
                    titulo = "Meus objetos",
                    quantity = "3",
                    iconeRes = R.drawable.objeto,
                    onAddClick = {
                        focusManager.clearFocus()
                        val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val token = sharedPrefs.getString("auth_token", "") ?: ""
                        val idUsuario = sharedPrefs.getInt("auth_user_id", 0)

                        // 🎯 ATUALIZADO: Redireciona para a tela de objetos passando as credenciais
                        navController.navigate("criar_objeto/$token/$idUsuario")
                    }
                )
            }

            // 3. Título da Seção do Mural
            Text(
                text = "Postagens",
                modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // 4. Grade de Postagens Atualizada com a lista ordenada cronologicamente
            PostGridSection(
                posts = postsOrdenados,
                viewModel = viewModel,
                onPostClick = { postId ->
                    focusManager.clearFocus()
                    navController.navigate("detalhe_post/$postId")
                }
            )
        }

        // Botão Flutuante (FAB) posicionado exatamente no canto inferior direito
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