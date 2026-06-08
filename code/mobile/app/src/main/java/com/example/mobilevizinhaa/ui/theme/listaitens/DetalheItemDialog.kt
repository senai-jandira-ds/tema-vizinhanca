package com.example.mobilevizinhaa.ui.theme.listaitens

import androidx.compose.foundation.BorderStroke
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
    onExcluir: (Int, Boolean) -> Unit, // 🎯 Recebe o ID do item e a flag booleana indicando se é um Objeto
    onMudarParaAndamento: (Int) -> Unit // Dispara a ação do ViewModel para transicionar o status e publicar no Mural
) {
    // 🎯 IDENTIFICA SE É UM OBJETO OU SERVIÇO ATRAVÉS DA TAG INJETADA NO VIEWMODEL
    val ehObjeto = item.urgency == "OBJETO" ||
            item.category?.name?.contains("Objeto", ignoreCase = true) == true ||
            item.title?.contains("Objeto", ignoreCase = true) == true

    // 🎯 DEFINE O TEXTO DO BOTÃO BASEADO NA REGRA DE NEGÓCIO DO SEU TCC
    val textoBotaoMural = if (ehObjeto) {
        "Colocar no Mural (Disponível)"
    } else {
        "Colocar no Mural (Em Andamento)"
    }

    // 🎯 VERIFICA SE O ITEM ESTÁ APTO PARA IR AO MURAL (PENDENTE OU INDISPONÍVEL)
    val statusAtual = item.status?.uppercase()?.trim() ?: "PENDENTE"
    val podeIrParaOMural = statusAtual == "PENDENTE" ||
            statusAtual == "INDISPONIVEL" ||
            statusAtual == "INDISPONÍVEL"

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
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
                            text = statusAtual,
                            color = if (statusAtual == "PENDENTE" || statusAtual == "INDISPONIVEL" || statusAtual == "INDISPONÍVEL") {
                                Color(0xFFEAB308) // Amarelo/Laranja para estados pendentes
                            } else {
                                BluePrimary // Azul para Disponível/Em Andamento
                            },
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

                // BOTÃO: Colocar no Mural (Apenas se estiver Pendente ou Indisponível)
                if (podeIrParaOMural) {
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
                            text = textoBotaoMural,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // BOTÃO: Excluir Item (Dispara o DELETE físico na API passando se é objeto ou não)
                OutlinedButton(
                    onClick = {
                        onExcluir(item.id, ehObjeto) // 🎯 Passa a informação do tipo de item para a exclusão correta no VM
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    // 🎯 CORRIGIDO: Substituído o código deprecated por BorderStroke estável
                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
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