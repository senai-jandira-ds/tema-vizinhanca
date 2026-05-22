package com.example.mobilevizinhaa.ui.theme.home

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.*

// --- 1. HOME HEADER (ÁREA VERDE COMPLETA - CONFIGURADO PARA NAVEGAÇÃO) ---
@Composable
fun HomeHeader(
    userName: String,
    apartment: String,
    userPhotoUrl: String? = null,
    navController: NavController? = null // Adicionado o parâmetro do NavController
) {
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) fotoUri = uri }

    // Identifica se a foto vinda da API é um link remoto HTTP (Nuvem)
    val isUrlRemota = userPhotoUrl?.startsWith("http", ignoreCase = true) == true

    // Caso NÃO seja link direto HTTP, tenta converter a string assumindo que seja Base64 válido
    val bitmapPerfil = remember(userPhotoUrl) {
        if (!isUrlRemota) carregarImagemBase64(userPhotoUrl) else null
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

            // AQUI: O botão de engrenagem agora dispara a navegação para a tela de configurações
            IconButton(
                onClick = {
                    navController?.navigate("configuracoes") {
                        launchSingleTop = true // Evita abrir a mesma tela várias vezes seguidas
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
            // Condição 1: Prioridade se o usuário escolheu uma foto local do dispositivo
            if (fotoUri != null) {
                AsyncImage(
                    model = fotoUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            // Condição 2: Se a API enviou uma URL HTTP direta
            else if (isUrlRemota && !userPhotoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = userPhotoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.mulher)
                )
            }
            // Condição 3: Se a API mandou um Base64, renderiza o bitmap decodificado
            else if (bitmapPerfil != null) {
                Image(
                    bitmap = bitmapPerfil,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            // Condição 4: Fallback padrão caso não exista foto cadastrada de nenhum modo
            else {
                Box(Modifier.fillMaxSize().background(Color(0xFFE0E0E0)), Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(55.dp))
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

// --- 3. GRADE DE POSTAGENS STYLE INSTAGRAM ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostGridSection(posts: List<Post>, onPostClick: (Int) -> Unit) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.Start,
        maxItemsInEachRow = 3
    ) {
        posts.forEach { post ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3333f)
                    .aspectRatio(1f)
                    .padding(1.dp)
                    .background(Color(0xFFF0F0F0))
                    .clickable { onPostClick(post.id) }
            ) {
                val hasRemoteImage = !post.imagemUrl.isNullOrEmpty() && post.imagemUrl != "string"

                if (hasRemoteImage) {
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
                        painter = painterResource(id = post.imagemRes ?: R.drawable.mulher),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = post.titulo,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.align(Alignment.Center).padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- 4. NAVBAR ---
@Composable
fun CustomBottomNavBar(navController: NavController? = null) {
    val navBackStackEntry = navController?.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.value?.destination?.route

    Surface(
        modifier = Modifier.fillMaxWidth().height(80.dp),
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
                BottomNavItem(
                    painter = painterResource(id = item.first),
                    // Aceita "configuracoes" como rota ativa se quisermos destacar o botão home ou manter desmarcado
                    isSelected = currentRoute?.startsWith(item.second) == true,
                    onClick = {
                        if (currentRoute != item.second) {
                            navController?.navigate(item.second) {
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

@Composable
fun BottomNavItem(painter: Painter, isSelected: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(50.dp).clip(CircleShape).clickable { onClick() },
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

fun carregarImagemBase64(base64String: String?): ImageBitmap? {
    if (base64String.isNullOrBlank() || base64String == "string") return null
    return try {
        val stringLimpa = if (base64String.contains(",")) {
            base64String.substring(base64String.indexOf(",") + 1)
        } else {
            base64String
        }
        val bytesDecodificados = Base64.decode(stringLimpa, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytesDecodificados, 0, bytesDecodificados.size)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}