package com.example.mobilevizinhaa.ui.theme.home

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.*

// Imports de dados e utilitários
import com.example.mobilevizinhaa.ui.theme.data.ResidentResponse
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// --- 1. HOME HEADER ---
@Composable
fun HomeHeader(
    userName: String,
    apartment: String,
    userPhotoUrl: String? = null,
    navController: NavController? = null,
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            fotoUri = uri
            viewModel.atualizarFotoPerfil(context, uri)
        }
    }

    LaunchedEffect(userPhotoUrl) {
        if (!userPhotoUrl.isNullOrBlank()) {
            fotoUri = null
        }
    }

    val bitmapPerfil = remember(userPhotoUrl) {
        if (!userPhotoUrl.isNullOrEmpty() && !userPhotoUrl.startsWith("http") && userPhotoUrl != "string") {
            viewModel.carregarImagemBase64MuralLocal(userPhotoUrl)
        } else {
            null
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(brush = Brush.verticalGradient(
                    colors = listOf(GradientBlueStart, GradientBlueEnd)
                ))
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 125.dp, top = 20.dp)
            ) {
                Text(
                    text = if (userName.isNotEmpty()) "Olá, $userName!" else "Bem-vindo!",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = apartment,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 15.sp
                )
            }

            IconButton(
                onClick = {
                    navController?.navigate("configuracoes") {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 40.dp)
            ) {
                Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 10.dp)
                .size(110.dp)
                .shadow(10.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(5.dp)
                .clip(CircleShape)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            when {
                fotoUri != null -> {
                    AsyncImage(
                        model = fotoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                bitmapPerfil != null -> {
                    Image(
                        bitmap = bitmapPerfil,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                (!userPhotoUrl.isNullOrEmpty() && userPhotoUrl.startsWith("http")) -> {
                    AsyncImage(
                        model = userPhotoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Box(Modifier.fillMaxSize().background(Color(0xFFE0E0E0)), Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(55.dp))
                    }
                }
            }
        }
    }
}

// --- 2. INFO CARD ---
@Composable
fun InfoCard(titulo: String, quantity: String, iconeRes: Int, onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(168.dp)
            .height(130.dp)
            .shadow(6.dp, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column {
                Text(quantity, fontSize = 38.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(titulo, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            }
            Icon(
                painter = painterResource(id = iconeRes),
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.align(Alignment.TopEnd).size(22.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(34.dp)
                    .background(BluePrimary, CircleShape)
                    .clip(CircleShape)
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// --- 3. GRADE DE POSTAGENS ---
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
            .heightIn(max = 4000.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        userScrollEnabled = false
    ) {
        items(posts) { post ->
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
                    .clickable { onPostClick(post.id) },
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

// --- 4. NAVBAR PERSONALIZADA (PASSANDO ARGUMENTOS COM PROTEÇÃO CONTRA CRASH) ---
@Composable
fun CustomBottomNavBar(navController: NavController? = null) {
    val navBackStackEntry = navController?.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.value?.destination?.route

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val tokenString = sharedPreferences.getString("auth_token", "") ?: ""

    val jsonUsuario = sharedPreferences.getString("saved_user", null)
    val idUsuarioInt = if (!jsonUsuario.isNullOrBlank()) {
        try {
            Gson().fromJson(jsonUsuario, ResidentResponse::class.java).id
        } catch (e: Exception) {
            0
        }
    } else {
        0
    }

    // Codifica com segurança o Token para que caracteres como "." ou "/" não quebrem as URLs internas do Android
    val tokenCodificadoSeguro = try {
        URLEncoder.encode(tokenString, StandardCharsets.UTF_8.toString())
    } catch (e: Exception) {
        tokenString
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color.White,
        shadowElevation = 25.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val items = listOf(
                Triple(R.drawable.home2, "home", "Home"),
                Triple(R.drawable.pedido2, "pedido", "Pedidos"),
                Triple(R.drawable.logomural2, "mural", "Mural"),
                Triple(R.drawable.ranking2, "ranking", "Ranking"),
                Triple(R.drawable.chat2, "mensagens", "Chat"),
                Triple(R.drawable.notificacoes2, "notificacoes", "Notificações")
            )

            items.forEach { item ->
                val isSelected = currentRoute?.startsWith(item.second) == true

                BottomNavItem(
                    painter = painterResource(id = item.first),
                    isSelected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            val rotaDestino = when (item.second) {
                                "pedido" -> "pedido/$tokenCodificadoSeguro/$idUsuarioInt"
                                "mural" -> "mural/$tokenCodificadoSeguro/$idUsuarioInt"
                                "mensagens" -> "mensagens/$tokenCodificadoSeguro/$idUsuarioInt"
                                else -> item.second
                            }

                            Log.d("NAV_BAR", "Mudando para a rota: $rotaDestino | ID do morador: $idUsuarioInt")

                            navController?.navigate(rotaDestino) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

// --- 5. ITEM DA NAVBAR ---
@Composable
fun BottomNavItem(painter: Painter, isSelected: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(Modifier.fillMaxSize().background(NavbarActiveBlue, CircleShape), Alignment.Center) {
                Icon(painter, null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
        } else {
            Icon(painter, null, tint = Color.Gray, modifier = Modifier.size(26.dp))
        }
    }
}