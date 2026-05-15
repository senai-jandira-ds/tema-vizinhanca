package com.example.mobilevizinhaa.ui.theme.home

import android.net.Uri
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


@Composable
fun HomeHeader(userName: String, apartment: String) {
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) fotoUri = uri }

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
                onClick = { /* Configurações */ },
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
            if (fotoUri != null) {
                AsyncImage(model = fotoUri, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Box(Modifier.fillMaxSize().background(Color(0xFFE0E0E0)), Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(55.dp))
                }
            }
        }
    }
}

// --- 2. INFO CARD ---
@Composable
fun InfoCard(titulo: String, quantidade: String, iconeRes: Int) {
    Card(
        modifier = Modifier
            .width(168.dp)
            .height(130.dp)
            .shadow(6.dp, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column {
                Text(quantidade, fontSize = 38.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
                    .background(BluePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// --- 3. GRADE DE POSTAGENS ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostGridSection(posts: List<Post>, onPostClick: (Int) -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
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
                if (post.imagemUri != null) {
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