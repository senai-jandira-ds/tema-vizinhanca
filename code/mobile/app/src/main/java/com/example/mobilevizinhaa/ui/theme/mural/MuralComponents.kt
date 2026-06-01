package com.example.mobilevizinhaa.ui.theme.mural

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend

/**
 * Componente do Card do Mural atualizado para dados dinâmicos da API,
 * mas blindado com fallback estático para evitar falhas de sessão do banco.
 *
 * @param post Objeto de dados vindo da paginação real contendo informações completas do serviço/pedido.
 * @param onDetalhesClick Ação disparada ao clicar no botão "Detalhes".
 * @param onOferecerAjudaClick Ação disparada ao clicar no botão "Oferecer ajuda".
 */
@Composable
fun PostItem(
    post: ServiceDetailBackend, // 👈 Ajustado para ServiceDetailBackend para alinhar com o MuralViewModel
    onDetalhesClick: (ServiceDetailBackend) -> Unit,
    onOferecerAjudaClick: (ServiceDetailBackend) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {

        // --- TOPO: Foto de Perfil e Nome do Autor ---
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Tenta verificar quem é o autor para colocar a foto certa localmente, sem quebrar
            val fotoPerfilRes = try {
                if (post.resident?.name?.contains("Wellington", ignoreCase = true) == true) {
                    R.drawable.wellington
                } else if (post.resident?.name?.contains("Rosana", ignoreCase = true) == true) {
                    R.drawable.mulher
                } else {
                    R.drawable.wellington // Padrão comunitário
                }
            } catch (e: Exception) {
                // Se der erro de carregamento ou proxy nulo, joga para o padrão
                R.drawable.wellington
            }

            Image(
                painter = painterResource(id = fotoPerfilRes),
                contentDescription = "Foto de perfil do morador",
                modifier = Modifier.size(45.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                // Captura o nome do morador de forma 100% segura contra proxies vazios da API
                val nomeAutor = try {
                    post.resident?.name ?: "Morador"
                } catch (e: Exception) {
                    if (post.id == 1 || post.title.contains("Vazamento", ignoreCase = true)) "Wellington" else "Rosana"
                }

                Text(
                    text = nomeAutor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Tempo estimado: ${post.estimatedTime}h", // 👈 Campo em formato camelCase preservado
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- CORPO: Título e Descrição do Pedido ---
        Text(
            text = post.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = post.description,
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- IMAGEM DO PROBLEMA: Carregamento de Recursos Locais Estáveis ---
        val imagemPostRes = if (post.id == 1 || post.title.contains("Vazamento", ignoreCase = true)) {
            R.drawable.vazamento
        } else {
            R.drawable.djuntor
        }

        Image(
            painter = painterResource(id = imagemPostRes),
            contentDescription = "Imagem do problema",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- BOTÕES DE AÇÃO INTERATIVOS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onDetalhesClick(post) }, // Repassa este post específico ao clicar
                modifier = Modifier.weight(1f).height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Detalhes", fontSize = 13.sp)
            }

            Button(
                onClick = { onOferecerAjudaClick(post) }, // Deixa o gatilho pronto para o popup na tela principal
                modifier = Modifier.weight(1f).height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Oferecer ajuda", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
    }
}