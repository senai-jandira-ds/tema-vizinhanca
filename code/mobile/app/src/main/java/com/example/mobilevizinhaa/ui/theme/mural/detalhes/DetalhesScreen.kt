package com.example.mobilevizinhaa.ui.theme.mural.detalhes

import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetail
import com.example.mobilevizinhaa.ui.theme.data.RetrofitClient
import com.example.mobilevizinhaa.ui.theme.data.ServiceUpdateRequest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Tela de Detalhes do Pedido/Objeto (Versão totalmente Dinâmica, Adaptável e com Decoder Híbrido de Imagem).
 * Configurada com botão vermelho de alteração de status para exclusão de anúncios do próprio usuário.
 */
@Composable
fun MuralDetalheScreen(
    servico: ServiceDetail,
    idUsuarioLogado: Int,
    tokenUsuario: String, // 🎯 ADICIONADO: Necessário para a autenticação no Header da requisição HTTP
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var mostrarAlerta by remember { mutableStateOf(false) }
    var carregandoRemocao by remember { mutableStateOf(false) }

    // 🎯 IDENTIFICAÇÃO CRÍTICA: Descobre se o item detalhado é um Objeto ou um Serviço
    val ehObjeto = servico.urgency?.uppercase() == "OBJETO" ||
            servico.category?.typeCategory?.uppercase() == "OBJETO" ||
            servico.category?.name?.contains("Objeto", ignoreCase = true) == true

    // 🎯 REGRA DE NEGÓCIO CRÍTICA: Verifica se o ID do dono do post é igual ao do usuário atualmente logado
    val souDonoDoPost = servico.resident?.id == idUsuarioLogado

    // Define qual recurso local serve de fallback de segurança para esse item específico
    val imagemPadraoLocal = remember(servico.id, servico.title) {
        if (servico.id == 1 || (servico.title ?: "").contains("Vazamento", ignoreCase = true)) {
            R.drawable.vazamento
        } else {
            R.drawable.djuntor
        }
    }

    // Fallback de segurança para a foto de perfil baseando-se no nome do morador
    val fotoPerfilFallback = remember(servico.resident?.name) {
        val nomeLimpo = servico.resident?.name ?: ""
        if (nomeLimpo.contains("Rosana", ignoreCase = true)) {
            R.drawable.mulher
        } else {
            R.drawable.wellington
        }
    }

    // Tratamento seguro da tag de urgência (apenas se for Serviço)
    val urgenciaString = servico.urgency ?: "BAIXA"
    val textoUrgencia = when (urgenciaString.uppercase()) {
        "ALTA" -> "Urgente"
        "MEDIA", "MÉDIA" -> "Média"
        else -> "Baixa"
    }

    val corUrgencia = when (urgenciaString.uppercase()) {
        "ALTA" -> Color(0xFFEF5350)
        "MEDIA", "MÉDIA" -> Color(0xFFFFB74D)
        else -> Color(0xFF81C784)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- HEADER COM FOTO DINÂMICA DO SERVIÇO OU OBJETO (COIL) ---
            Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {

                // PROCESSAMENTO HÍBRIDO DA FOTO DO ANÚNCIO (Aceita URL ou Base64)
                val modeloImagemDetalhe: Any = remember(servico.photo, imagemPadraoLocal) {
                    try {
                        val foto = servico.photo ?: ""

                        if (foto.isNotBlank() && foto.lowercase() != "string") {
                            if (foto.startsWith("http://") || foto.startsWith("https://")) {
                                foto
                            } else if (foto.length > 50) {
                                val base64Limpo = if (foto.contains(",")) foto.substringAfter(",") else foto
                                Base64.decode(base64Limpo, Base64.DEFAULT)
                            } else {
                                imagemPadraoLocal
                            }
                        } else {
                            imagemPadraoLocal
                        }
                    } catch (e: Exception) {
                        imagemPadraoLocal
                    }
                }

                AsyncImage(
                    model = modeloImagemDetalhe,
                    contentDescription = "Foto do Pedido ou Objeto",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = imagemPadraoLocal),
                    placeholder = painterResource(id = imagemPadraoLocal)
                )

                // Sombra gradiente superior para dar leitura ao botão de voltar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(Brush.verticalGradient(colors = listOf(Color(0xFF002D5A), Color.Transparent)))
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp).clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (ehObjeto) "Detalhes do Objeto" else "Detalhes do Pedido",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- CORPO DA TELA ---
            Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(20.dp)) {

                // DADOS DO MORADOR (FOTO DE PERFIL DINÂMICA)
                Row(verticalAlignment = Alignment.CenterVertically) {

                    val modeloFotoPerfil: Any = remember(servico.resident?.photo, fotoPerfilFallback) {
                        try {
                            val fotoStr = servico.resident?.photo?.trim() ?: ""

                            if (fotoStr.isNotBlank() && fotoStr.lowercase() != "string") {
                                if (fotoStr.startsWith("http://") || fotoStr.startsWith("https://")) {
                                    fotoStr
                                } else if (fotoStr.length > 50) {
                                    val base64Limpo = if (fotoStr.contains(",")) fotoStr.substringAfter(",") else fotoStr
                                    Base64.decode(base64Limpo, Base64.DEFAULT)
                                } else {
                                    fotoPerfilFallback
                                }
                            } else {
                                fotoPerfilFallback
                            }
                        } catch (e: Exception) {
                            fotoPerfilFallback
                        }
                    }

                    AsyncImage(
                        model = modeloFotoPerfil,
                        contentDescription = "Foto de perfil do morador",
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = fotoPerfilFallback),
                        placeholder = painterResource(id = fotoPerfilFallback)
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = servico.resident?.name ?: "Morador",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        val apto = servico.resident?.apartment
                        if (!apto.isNullOrBlank()) {
                            Text(text = "Apto: $apto", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                Text(text = servico.title ?: "Sem título", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(14.dp))

                // --- TAGS INFORMATIVAS ADAPTÁVEIS ---
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {

                    // Tag Principal de Categoria
                    Box(modifier = Modifier.background(Color(0xFF26A69A), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(text = servico.category?.name ?: "Geral", color = Color.White, fontSize = 13.sp)
                    }

                    if (ehObjeto) {
                        // Se for Objeto, exibe etiqueta de Disponibilidade em vez de tempo
                        Box(modifier = Modifier.background(Color(0xFFE0F2FE), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Text(text = "📦 Disponível", color = Color(0xFF0369A1), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Se for Serviço, mantém o Tempo Estimado e o nível de Urgência
                        Box(modifier = Modifier.background(Color(0xFFF1F3F5), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Text(text = "Tempo estimado: ${servico.estimatedTime ?: 0}h", color = Color(0xFF495057), fontSize = 13.sp)
                        }

                        Box(modifier = Modifier.background(corUrgencia, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Text(text = textoUrgencia, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Descrição textual do item
                Text(
                    text = if (servico.description.isNullOrBlank()) "Nenhuma descrição fornecida para este item." else servico.description,
                    fontSize = 16.sp, color = Color(0xFF333333), lineHeight = 24.sp
                )
            }

            // --- 🎯 SEÇÃO DE BOTÕES INFERIORES MODIFICADOS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botão de fechar sempre ativo
                Button(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = "Fechar",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                if (souDonoDoPost) {
                    // 🎯 BOTÃO VERMELHO: Exibido e ativo unicamente para o criador do post remover o anúncio
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    carregandoRemocao = true
                                    val tokenHeader = "Bearer $tokenUsuario"

                                    // 🎯 RESOLUÇÃO DA INTEGRAÇÃO: Mapeia dinamicamente para a rota nativa do Swagger
                                    val resposta = if (ehObjeto) {
                                        val statusBody = "INDISPONIVEL".toRequestBody("text/plain".toMediaTypeOrNull())
                                        RetrofitClient.authApi.atualizarStatusObjeto(
                                            token = tokenHeader,
                                            idObjeto = servico.id,
                                            status = statusBody
                                        )
                                    } else {
                                        RetrofitClient.authApi.atualizarStatusServico(
                                            token = tokenHeader,
                                            idServico = servico.id,
                                            request = ServiceUpdateRequest(status = "PENDENTE")
                                        )
                                    }

                                    if (resposta.isSuccessful) {
                                        Toast.makeText(context, "Anúncio removido com sucesso!", Toast.LENGTH_SHORT).show()
                                        onBackClick() // Fecha a tela e retorna à listagem principal atualizada
                                    } else {
                                        Toast.makeText(context, "Servidor rejeitou a remoção do item.", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Log.e("MURAL_REMOVER", "Erro de conexão: ${e.message}")
                                    Toast.makeText(context, "Falha de rede ao conectar à API.", Toast.LENGTH_SHORT).show()
                                } finally {
                                    carregandoRemocao = false
                                }
                            }
                        },
                        enabled = !carregandoRemocao,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350), // Vermelho Red 500
                            disabledContainerColor = Color(0xFFE57373)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        if (carregandoRemocao) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Remover do Mural",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    // Caso contrário (outro morador vendo o anúncio), exibe o botão normal de engajamento
                    Button(
                        onClick = { mostrarAlerta = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = if (ehObjeto) "Pegar Objeto" else "Oferecer Ajuda",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // --- DIÁLOGO DE CONFIRMAÇÃO DINÂMICO ---
        if (mostrarAlerta) {
            val mensajeAlerta = if (ehObjeto) {
                "Você deseja pegar o objeto \"${servico.title ?: "deste item"}\" emprestado?\nApós confirmar, vocês poderão combinar os detalhes da entrega pelo chat."
            } else {
                "Você deseja oferecer ajuda para o pedido \"${servico.title ?: "deste serviço"}\"?\nApós confirmar, vocês poderão combinar os detalhes pelo chat."
            }

            AlertDialog(
                onDismissRequest = { mostrarAlerta = false },
                title = { Text(text = "Atenção", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black) },
                text = {
                    Text(
                        text = mensajeAlerta,
                        fontSize = 15.sp,
                        color = Color(0xFF495057)
                    )
                },
                confirmButton = {
                    TextButton(onClick = { mostrarAlerta = false }) {
                        Text(
                            text = if (ehObjeto) "Pegar Emprestado" else "Confirmar",
                            color = Color(0xFF26A69A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarAlerta = false }) {
                        Text("Cancelar", color = Color(0xFFEF5350), fontSize = 16.sp)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                containerColor = Color.White
            )
        }
    }
}