package com.example.mobilevizinhaa.ui.theme.home

import android.content.Context
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobilevizinhaa.ui.theme.GradientBlueEnd
import com.example.mobilevizinhaa.ui.theme.GradientBlueStart

// ====================================================================
// 🎯 TELA PRINCIPAL DE PERFIL (INTEGRAÇÃO TOTAL COM VIEWMODEL E NAV)
// ====================================================================
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val residentData by viewModel.residentData.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val qtdPedidos by viewModel.qtdPedidos.collectAsState()
    val qtdObjetos by viewModel.qtdObjetos.collectAsState()

    // Calcula o tamanho real da lista de publicações locais do morador
    val qtdMural = remember(residentData?.publications) {
        (residentData?.publications?.size ?: 0).toString()
    }

    // Tratamento de strings nulas ou vazias vindas do banco
    val nomeExibido = residentData?.name ?: "Morador"
    val emailExibido = residentData?.email ?: "usuario@vizinhanca.com"
    val blocoAptoExibido = "Bloco: ${residentData?.block?.name ?: "N/A"} - Apto: ${residentData?.apartment ?: "N/A"}"
    val fotoPerfil = residentData?.photo

    // Definição da cor de fundo da tela inteira baseado no tema
    val corFundoTela = if (isDarkMode) Color(0xFF121212) else Color(0xFFF8F9FA)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(corFundoTela)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Cabeçalho Dinâmico
        ProfileHeader(
            userName = nomeExibido,
            userEmail = emailExibido,
            apartment = blocoAptoExibido,
            userPhotoUrl = fotoPerfil,
            qtdPedidos = qtdPedidos,
            qtdObjetos = qtdObjetos,
            qtdMural = qtdMural,
            isDarkMode = isDarkMode,
            onBackClick = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Lista de Ações mapeadas para navegar até a ConfiguracoesScreen
        val menuOptions = remember {
            listOf(
                Triple(Icons.Default.Person, "Alterar Nome de Exibição") {
                    navController.navigate("configuracoes") // Rota configurada no seu NavHost
                },
                Triple(Icons.Default.Lock, "Privacidade e Senha") {
                    navController.navigate("configuracoes")
                },
                Triple(Icons.Default.ExitToApp, "Sair da Conta") {
                    viewModel.deslogar(navController)
                }
            )
        }

        ProfileOptionMenu(
            options = menuOptions,
            isDarkMode = isDarkMode
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ====================================================================
// --- COMPONENTE 1: HEADER AZUL INTEGRADO COM BOTÃO VOLTAR EMBUTIDO ---
// ====================================================================
@Composable
fun ProfileHeader(
    userName: String,
    userEmail: String,
    apartment: String,
    userPhotoUrl: String?,
    qtdPedidos: String = "0",
    qtdObjetos: String = "0",
    qtdMural: String = "0",
    isDarkMode: Boolean = false,
    onBackClick: (() -> Unit)? = null
) {
    val isPostImgUrlRemota = userPhotoUrl?.startsWith("http", ignoreCase = true) == true

    val bitmapProfileImage = remember(userPhotoUrl) {
        if (!isPostImgUrlRemota && !userPhotoUrl.isNullOrBlank()) {
            try {
                val stringLimpa = if (userPhotoUrl.contains(",")) {
                    userPhotoUrl.substring(userPhotoUrl.indexOf(",") + 1)
                } else userPhotoUrl

                val bytesDecodificados = Base64.decode(stringLimpa.trim(), Base64.DEFAULT)
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytesDecodificados, 0, bytesDecodificados.size)
                bitmap?.asImageBitmap()
            } catch (e: Exception) { null }
        } else null
    }

    val corCardFundo = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val corTextoPrincipal = if (isDarkMode) Color.White else Color.Black
    val corTextoSecundario = if (isDarkMode) Color.LightGray else Color.Gray
    val corDivisor = if (isDarkMode) Color(0xFF2E2E2E) else Color(0xFFF0F0F0)
    val corAvatarFundo = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFEAEAEA)

    Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
        // Bloco Superior de Fundo Azul Degradê
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(brush = Brush.verticalGradient(
                    colors = listOf(GradientBlueStart, GradientBlueEnd)
                ))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar para Home",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Text(
                    text = "Perfil",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Card de Informações Principais (Flutuando sobre o degradê)
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .wrapContentHeight()
                .shadow(8.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = corCardFundo)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(corAvatarFundo, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isPostImgUrlRemota -> {
                                AsyncImage(
                                    model = userPhotoUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            bitmapProfileImage != null -> {
                                Image(
                                    bitmap = bitmapProfileImage,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF3867F5), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val iniciais = userName.split(" ")
                                        .take(2)
                                        .map { it.firstOrNull()?.uppercase() ?: "" }
                                        .joinToString("")

                                    Text(
                                        text = iniciais.ifEmpty { "U" },
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(text = userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = corTextoPrincipal)
                        Text(text = userEmail, fontSize = 14.sp, color = corTextoSecundario)
                        Text(text = apartment, fontSize = 14.sp, color = corTextoSecundario)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = corDivisor, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Métricas inferiores reativas totalmente visíveis e distribuídas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem(quantidade = qtdPedidos, legenda = "Pedidos", isDarkMode = isDarkMode)
                    MetricItem(quantidade = qtdObjetos, legenda = "Objetos", isDarkMode = isDarkMode)
                    MetricItem(quantidade = qtdMural, legenda = "Mural", isDarkMode = isDarkMode)
                }
            }
        }
    }
}

// --- METRIC ITEM COMPONENT ---
@Composable
private fun MetricItem(quantidade: String, legenda: String, isDarkMode: Boolean) {
    val corTextoLegenda = if (isDarkMode) Color.LightGray else Color.Gray
    val corNumeroMetrica = if (isDarkMode) Color(0xFF537FF9) else Color(0xFF3867F5)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = quantidade,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = corNumeroMetrica
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = legenda,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = corTextoLegenda
        )
    }
}

// ====================================================================
// --- COMPONENTE 2: MENU DE OPÇÕES ---
// ====================================================================
@Composable
fun ProfileOptionMenu(
    options: List<Triple<ImageVector, String, () -> Unit>>,
    isDarkMode: Boolean = false
) {
    val corCardFundo = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val corTextoPrincipal = if (isDarkMode) Color.White else Color.Black
    val corIconeFundo = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFF2F5FE)
    val corIconeTint = if (isDarkMode) Color.LightGray else Color.DarkGray
    val corDivisor = if (isDarkMode) Color(0xFF2E2E2E) else Color(0xFFF6F6F6)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = corCardFundo)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { option.third() }
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(corIconeFundo, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = option.first,
                            contentDescription = null,
                            tint = corIconeTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = option.second,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = corTextoPrincipal,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }

                if (index < options.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = corDivisor,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}