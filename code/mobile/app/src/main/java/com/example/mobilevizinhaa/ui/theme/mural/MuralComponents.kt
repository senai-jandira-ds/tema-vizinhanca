package com.example.mobilevizinhaa.ui.theme.mural

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend

/**
 * Componente do Card do Mural unificado (Serviços e Objetos)
 * Suporta dinamicamente carregamento por URL (HTTP/HTTPS) ou decodificação de Base64.
 */
@Composable
fun PostItem(
    post: ServiceDetailBackend,
    onDetalhesClick: (ServiceDetailBackend) -> Unit,
    onOferecerAjudaClick: (ServiceDetailBackend) -> Unit
) {
    val ehObjeto = post.urgency == "OBJETO"

    // Fallback de segurança para a imagem principal do post
    val imagemPadraoLocal = remember(post.id, post.title) {
        val tituloLimpo = post.title ?: ""
        if (post.id == 1 || tituloLimpo.contains("Vazamento", ignoreCase = true)) {
            R.drawable.vazamento
        } else {
            R.drawable.djuntor
        }
    }

    // Fallback de segurança para a foto de perfil baseando-se no nome do morador
    val fotoPerfilFallback = remember(post.resident?.name) {
        val nomeLimpo = post.resident?.name ?: ""
        if (nomeLimpo.contains("Rosana", ignoreCase = true)) {
            R.drawable.mulher
        } else {
            R.drawable.wellington
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {

        // --- TOPO: Foto de Perfil Dinâmica, Nome do Autor e Etiqueta do Tipo de Item ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                // 🎯 1. PROCESSAMENTO HÍBRIDO DA FOTO DE PERFIL (Detecta link URL ou Base64)
                val modeloFotoPerfil: Any = remember(post.resident?.photo, fotoPerfilFallback) {
                    try {
                        val fotoStr = post.resident?.photo?.trim() ?: ""

                        if (fotoStr.isNotBlank() && fotoStr.lowercase() != "string") {
                            if (fotoStr.startsWith("http://") || fotoStr.startsWith("https://")) {
                                // Se for o link direto retornado no JSON, entrega a String da URL diretamente ao Coil
                                fotoStr
                            } else if (fotoStr.length > 50) {
                                // Caso o formato mude para Base64 em outros fluxos
                                val base64Limpo = if (fotoStr.contains(",")) {
                                    fotoStr.substringAfter(",")
                                } else {
                                    fotoStr
                                }
                                android.util.Base64.decode(base64Limpo, android.util.Base64.DEFAULT)
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

                // 🎯 2. EXIBIÇÃO ASYNCIMAGE DA FOTO DE PERFIL
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
                    val nomeAutor = post.resident?.name ?: "Morador"

                    Text(
                        text = nomeAutor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = if (ehObjeto) "Item Compartilhado" else "Tempo estimado: ${post.estimatedTime ?: 0}h",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            // ETIQUETA VISUAL: Identifica se é um Objeto ou Pedido de Serviço
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (ehObjeto) Color(0xFFE0F2FE) else Color(0xFFFEF3C7))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (ehObjeto) "📦 OBJETO" else "🛠️ SERVIÇO",
                    color = if (ehObjeto) Color(0xFF0369A1) else Color(0xFFB45309),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- CORPO: Título e Descrição do Item ---
        Text(
            text = post.title ?: "Sem título",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = post.description ?: "",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- 🎯 3. IMAGEM DO PROBLEMA / OBJETO (Suporta URL do Azure ou Base64) ---
        val modeloImagemMural: Any = remember(post.photoBase64, imagemPadraoLocal) {
            val stringFoto = post.photoBase64 ?: ""

            if (stringFoto.isNotBlank() && stringFoto.lowercase() != "string") {
                try {
                    if (stringFoto.startsWith("http://") || stringFoto.startsWith("https://")) {
                        // Trata o link do Azure Storage ou Placehold fornecido pelo Backend para Objetos
                        stringFoto
                    } else if (stringFoto.length > 50) {
                        // Trata se vier codificado em Base64 nativo
                        val base64Limpo = if (stringFoto.contains(",")) {
                            stringFoto.substringAfter(",")
                        } else {
                            stringFoto
                        }
                        android.util.Base64.decode(base64Limpo, android.util.Base64.DEFAULT)
                    } else {
                        imagemPadraoLocal
                    }
                } catch (e: Exception) {
                    imagemPadraoLocal
                }
            } else {
                imagemPadraoLocal
            }
        }

        AsyncImage(
            model = modeloImagemMural,
            contentDescription = "Imagem ilustrativa do item no mural",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(id = imagemPadraoLocal),
            placeholder = painterResource(id = imagemPadraoLocal)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- BOTÕES DE AÇÃO INTERATIVOS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onDetalhesClick(post) },
                modifier = Modifier.weight(1f).height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Detalhes", fontSize = 13.sp)
            }

            Button(
                onClick = { onOferecerAjudaClick(post) },
                modifier = Modifier.weight(1f).height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (ehObjeto) "Pegar Emprestado" else "Oferecer ajuda",
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
    }
}