package com.example.mobilevizinhaa.ui.theme.listaitens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevizinhaa.ui.theme.BluePrimary
import com.example.mobilevizinhaa.ui.theme.data.ServiceDetailBackend

@Composable
fun DetalheItemDialog(
    item: ServiceDetailBackend,
    onDismiss: () -> Unit,
    onExcluir: (Int) -> Unit,
    onMudarParaAndamento: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {}, // Mantido em branco para usar o layout customizado no corpo
        dismissButton = {},
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = item.title ?: "Sem título",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Seção de Descrição
                Column {
                    Text(
                        text = "Descrição:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = item.description ?: "Nenhuma descrição informada.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                // Linha de Status e Categoria alinhados lateralmente
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Status Atual:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = item.status ?: "PENDENTE",
                            color = BluePrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Categoria:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = item.category?.name ?: "Geral",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // BOTÃO: Colocar no Mural (Dispara a alteração para EM_ANDAMENTO e fecha o diálogo)
                Button(
                    onClick = {
                        onMudarParaAndamento(item.id)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Colocar no Mural (Andamento)",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                // BOTÃO: Excluir Item (Dispara o DELETE físico na API e fecha o diálogo)
                OutlinedButton(
                    onClick = {
                        onExcluir(item.id)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFEF4444))
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Excluir",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    )
}