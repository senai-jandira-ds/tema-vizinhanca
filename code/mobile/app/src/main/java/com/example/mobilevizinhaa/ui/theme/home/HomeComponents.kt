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

// 1. HEADER (Foto de perfil clicável)
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
                .background(brush = Brush.verticalGradient(colors = listOf(GradientBlueStart, GradientBlueEnd)))
                .padding(horizontal = 24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart).padding(start = 115.dp, top = 20.dp)) {
                Text(text = "Olá, $userName!", color = White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(text = apartment, color = White.copy(alpha = 0.9f), fontSize = 16.sp)
            }
            IconButton(onClick = { }, modifier = Modifier.align(Alignment.TopEnd).padding(top = 40.dp)) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = White, modifier = Modifier.size(28.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 15.dp)
                .size(110.dp)
                .shadow(12.dp, CircleShape)
                .background(White, CircleShape)
                .padding(6.dp)
                .clip(CircleShape)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (fotoUri != null) {
                AsyncImage(model = fotoUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
                }
            }
        }
    }
}

// 2. INFO CARD
@Composable
fun InfoCard(title: String, count: String, iconRes: Int) {
    Card(
        modifier = Modifier.width(168.dp).height(135.dp).shadow(8.dp, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = White),
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column {
                Text(text = count, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = title, fontSize = 14.sp, color = GrayText)
            }
            Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = BluePrimary, modifier = Modifier.align(Alignment.TopEnd).size(24.dp))
            Box(modifier = Modifier.align(Alignment.BottomEnd).size(38.dp).background(BluePrimary, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Add, contentDescription = null, tint = White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

// 3. GRADE DE POSTAGENS (Corrigida para imagens da Galeria ou Resource)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostGridSection(posts: List<Post>) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth(), // REMOVIDO o padding horizontal de 16.dp para encostar na tela
        horizontalArrangement = Arrangement.spacedBy(1.dp), // Espaço mínimo entre as fotos
        maxItemsInEachRow = 3
    ) {
        posts.forEach { post ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f) // Quadrado perfeito (1:1)
                    .padding(bottom = 1.dp) // Espaço mínimo entre as linhas
                    .background(Color.LightGray)
                // Sem .clip ou .clip(RoundedCornerShape) para manter as bordas RETAS
            ) {
                if (post.imagemUri != null) {
                    AsyncImage(
                        model = post.imagemUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // Corta a imagem para preencher o quadrado todo
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

        // Isso evita que o último item fique gigante se estiver sozinho na linha
        val emptySlots = 3 - (posts.size % 3)
        if (emptySlots < 3) {
            repeat(emptySlots) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

// 4. NAVBAR
@Composable
fun CustomBottomNavBar(navController: NavController? = null) {
    val navBackStackEntry = navController?.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.value?.destination?.route

    Surface(
        modifier = Modifier.fillMaxWidth().height(85.dp),
        color = White,
        shadowElevation = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(painter = painterResource(id = R.drawable.logohome), isSelected = currentRoute == "home", onClick = { if (currentRoute != "home") navController?.navigate("home") })
            BottomNavItem(painter = painterResource(id = R.drawable.pedido), isSelected = currentRoute == "pedido", onClick = { if (currentRoute != "pedido") navController?.navigate("pedido") })
            BottomNavItem(painter = painterResource(id = R.drawable.logomural), isSelected = currentRoute == "mural", onClick = { navController?.navigate("mural") })
            BottomNavItem(painter = painterResource(id = R.drawable.ranking), isSelected = currentRoute == "ranking", onClick = { navController?.navigate("ranking") })
            BottomNavItem(painter = painterResource(id = R.drawable.chat), isSelected = currentRoute == "mensagens", onClick = { navController?.navigate("mensagens") })
            BottomNavItem(painter = painterResource(id = R.drawable.notificacoes), isSelected = currentRoute == "notificacoes", onClick = { navController?.navigate("notificacoes") })
        }
    }
}

@Composable
fun BottomNavItem(painter: Painter, isSelected: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(54.dp).clip(CircleShape).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(modifier = Modifier.fillMaxSize().background(NavbarActiveBlue, CircleShape), contentAlignment = Alignment.Center) {
                Icon(painter = painter, contentDescription = null, tint = White, modifier = Modifier.size(28.dp))
            }
        } else {
            Icon(painter = painter, contentDescription = null, tint = GrayText, modifier = Modifier.size(28.dp))
        }
    }
}