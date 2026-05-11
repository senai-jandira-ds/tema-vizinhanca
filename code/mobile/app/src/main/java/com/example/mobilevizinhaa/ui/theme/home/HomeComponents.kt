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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

// 1. HEADER: Gradiente real e Foto com borda branca flutuante


@Composable
fun HomeHeader(userName: String, apartment: String) {
    // 1. Criamos um estado interno aqui dentro.
    // Ele guarda a foto enquanto o app estiver aberto sem precisar de ViewModel.
    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    // 2. O "Contrato" que abre a galeria do celular.
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) fotoUri = uri
    }

    Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
        // Seu fundo azul (Mantido IGUAL)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientBlueStart, GradientBlueEnd)
                    )
                )
                .padding(horizontal = 24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart).padding(start = 115.dp, top = 20.dp)) {
                Text(text = "Olá, $userName!", color = White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(text = apartment, color = White.copy(alpha = 0.9f), fontSize = 16.sp)
            }
            IconButton(
                onClick = { },
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 40.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = White, modifier = Modifier.size(28.dp))
            }
        }

        // A Foto de Perfil (Onde a mágica acontece)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 15.dp)
                .size(110.dp)
                .shadow(12.dp, CircleShape)
                .background(White, CircleShape)
                .padding(6.dp) // Borda branca que você já tinha
                .clip(CircleShape)
                // AQUI: Quando clica no círculo, abre a galeria
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (fotoUri != null) {
                // Se o usuário escolheu uma foto, mostra ela
                AsyncImage(
                    model = fotoUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Se não escolheu, mantém o cinza que você criou
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    // Coloquei um ícone de pessoa só para não ficar o cinza vazio
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, count: String, iconRes: Int) {
    Card(
        modifier = Modifier.width(168.dp).height(135.dp).shadow(8.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = White),
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column {
                Text(text = count, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = title, fontSize = 14.sp, color = GrayText)
            }
            // Ícone do topo do card (Ex: prancheta ou caixa)
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
            )
            // Botão "+" azul no canto inferior
            Box(
                modifier = Modifier.align(Alignment.BottomEnd).size(38.dp).background(BluePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

// 3. CUSTOM NAVBAR: Com o círculo azul vibrante e logos extraídos do seu drawable
@Composable
fun CustomBottomNavBar(navController: NavController? = null) {
    // Ajuste de sintaxe para o Compose aceitar o estado opcional
    val navBackStackEntry = navController?.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.value?.destination?.route

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        color = White,
        shadowElevation = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // HOME
            BottomNavItem(
                painter = painterResource(id = R.drawable.logohome),
                isSelected = currentRoute == "home",
                onClick = { if (currentRoute != "home") navController?.navigate("home") }
            )

            // PEDIDOS
            BottomNavItem(
                painter = painterResource(id = R.drawable.pedido),
                isSelected = currentRoute == "pedido" || currentRoute == "objeto",
                onClick = { if (currentRoute != "pedido") navController?.navigate("pedido") }
            )

            // MURAL
            BottomNavItem(
                painter = painterResource(id = R.drawable.logomural),
                isSelected = currentRoute == "mural",
                onClick = { navController?.navigate("mural") }
            )

            // RANKING
            BottomNavItem(
                painter = painterResource(id = R.drawable.ranking),
                isSelected = currentRoute == "ranking",
                onClick = { navController?.navigate("ranking") }
            )

            // CHAT
            BottomNavItem(
                painter = painterResource(id = R.drawable.chat),
                isSelected = currentRoute == "mensagens",
                onClick = { navController?.navigate("mensagens") }
            )

            // NOTIFICAÇÕES
            BottomNavItem(
                painter = painterResource(id = R.drawable.notificacoes),
                isSelected = currentRoute == "notificacoes",
                onClick = { navController?.navigate("notificacoes") }
            )
        }
    }
}
@Composable
fun PostGridSection() {
    // Simulando as fotos (usando o seu drawable 'objeto')
    val posts = List(6) { R.drawable.mulher }

    // O FlowRow organiza os itens em linhas e pula para a próxima automaticamente
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 3 // Força as 3 colunas do seu design
    ) {
        posts.forEach { imageRes ->
            Box(
                modifier = Modifier
                    .size(110.dp) // Tamanho fixo para as fotos da grade
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(12.dp)) // Bordas arredondadas como no Figma
                    .background(Color.LightGray)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Foto da postagem",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Faz a foto preencher o quadrado sem esticar
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(painter: Painter, isSelected: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier.fillMaxSize().background(NavbarActiveBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painter, contentDescription = null, tint = White, modifier = Modifier.size(28.dp))
            }
        } else {
            Icon(painter = painter, contentDescription = null, tint = GrayText, modifier = Modifier.size(28.dp))
        }
    }
}