package com.example.mobilevizinhaa.ui.theme.home.createobjeto

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilevizinhaa.ui.theme.GradientBlueEnd
import com.example.mobilevizinhaa.ui.theme.GradientBlueStart
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarObjetoScreen(
    tokenUsuario: String,
    onVoltarClick: () -> Unit,
    viewModel: CriarObjetoViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var diasDisponiveis by remember { mutableStateOf(1) }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    // 🎯 Mantido o estado correto de bytes para compilar perfeitamente
    var fotoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var bitmapExibicao by remember { mutableStateOf<Bitmap?>(null) }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fotoUri = it
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }

                // Redimensiona para evitar estouro de memória (OutOfMemoryException)
                val bitmapRedimensionado = Bitmap.createScaledBitmap(bitmap, 600, 600, true)
                bitmapExibicao = bitmapRedimensionado

                val outputStream = ByteArrayOutputStream()
                bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

                // 🎯 Alimenta os bytes que serão lidos pelo gatilho do botão Enviar
                fotoBytes = outputStream.toByteArray()
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Erro ao processar a imagem selecionada.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            // 🎯 TEXTO SINCRO: Alinha o feedback com a regra (o objeto é criado como INDISPONÍVEL por padrão)
            Toast.makeText(context, "Objeto guardado! Disponibilize-o no mural para os seus vizinhos.", Toast.LENGTH_LONG).show()
            viewModel.limparEstado()
            onVoltarClick()
        }
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // --- CABEÇALHO COM GRADIENTE AZUL ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(GradientBlueStart, GradientBlueEnd)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onVoltarClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Objeto",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // --- CONTAINER DE SELEÇÃO DE FOTO ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE5E5E5))
                        .clickable { launcherGaleria.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmapExibicao != null) {
                        Image(
                            bitmap = bitmapExibicao!!.asImageBitmap(),
                            contentDescription = "Foto selecionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Adicionar imagem",
                                tint = Color.Gray,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Adicione uma imagem",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- FORMULÁRIO: TÍTULO ---
                Text(
                    text = "Título",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    placeholder = { Text("O que você está emprestando?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GradientBlueStart,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- FORMULÁRIO: PRAZO (STEPPER NUMÉRICO) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tempo disponível",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Prazo máximo em dias",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { if (diasDisponiveis > 1) diasDisponiveis-- },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        Text(
                            text = "$diasDisponiveis d",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { diasDisponiveis++ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- FORMULÁRIO: DESCRIÇÃO ---
                Text(
                    text = "Descrição",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    placeholder = { Text("Diga as regras do empréstimo, estado do objeto...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GradientBlueStart,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- BOTÕES DE COMPORTAMENTO EM LINHA ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { onVoltarClick() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancelar", color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.cadastrarObjetoNoCondominio(
                                token = tokenUsuario,
                                titulo = titulo,
                                descricao = descricao,
                                fotoBytes = fotoBytes,
                                diasDisponiveis = diasDisponiveis,
                                categoryId = 1
                            )
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Enviar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}