package com.example.mobilevizinhaa.ui.theme.home.createservice

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilevizinhaa.ui.theme.data.CategoryDetail
import com.example.mobilevizinhaa.ui.theme.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarPedidoObjetoScreen(
    navController: NavController,
    criarServicoViewModel: CriarServicoViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val tokenAtivo = homeViewModel.obterTokenSalvo()
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF1C1B1F)

    // Estados locais do formulário
    var titulo by remember { mutableStateOf("") }
    var urgencia by remember { mutableStateOf("MEDIA") }
    var descricao by remember { mutableStateOf("") }
    var imagemUri by remember { mutableStateOf<Uri?>(null) }

    // Tipo mapeado para CategoryDetail para casar perfeitamente com o modelo da API
    var categoriaSelecionada by remember { mutableStateOf<CategoryDetail?>(null) }
    var tempoSelecionado by remember { mutableStateOf<Pair<String, Int>?>(null) }

    val opcoesTempo = listOf(
        "15 minutos" to 15,
        "30 minutos" to 30,
        "1 hora" to 60,
        "2 horas" to 120,
        "4 horas" to 240,
    )

    // Limites de segurança locais em sincronia com o banco de dados
    val limiteMaxTitulo = 45
    val limiteMaxDescricao = 250

    // Escuta ativamente o token. Quando ele mudar de vazio para o token real, dispara a API.
    LaunchedEffect(tokenAtivo) {
        if (tokenAtivo.isNotEmpty()) {
            Log.d("API_CATEGORIAS", "Token identificado! Disparando carregarCategorias...")
            criarServicoViewModel.carregarCategorias(tokenAtivo)
        } else {
            Log.w("API_CATEGORIAS", "Aviso: Token ativo ainda está vazio. Aguardando leitura do banco local...")
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) imagemUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- CABEÇALHO AZUL GRADIENTE ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF3867F5), Color(0xFF1A46C7))
                    )
                )
                .padding(horizontal = 16.dp)
                .padding(top = 25.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    focusManager.clearFocus()
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Criar Novo Serviço",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- CONTEÚDO SCROLLÁVEL ---
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // COMPONENTE ISOLADO: SELETOR DE IMAGEM
            SeletorImagemComponent(
                imagemUri = imagemUri,
                onClick = {
                    focusManager.clearFocus()
                    galleryLauncher.launch("image/*")
                }
            )

            // --- TÍTULO (CORRIGIDO COM BLOQUEIO DE CARACTERES) ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Título do Serviço",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${titulo.length}/$limiteMaxTitulo",
                        fontSize = 12.sp,
                        color = if (titulo.length >= limiteMaxTitulo) Color.Red else Color.Gray
                    )
                }
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { novoTexto ->
                        // Trava preventivamente no teclado para não extrapolar o limite VARCHAR
                        if (novoTexto.length <= limiteMaxTitulo) {
                            titulo = novoTexto
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3867F5),
                        unfocusedBorderColor = if (isDark) Color(0xFF444444) else Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFF3867F5)
                    )
                )
            }

            // --- CATEGORIA E TEMPO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DropdownCategoriasComponent(
                    categorias = criarServicoViewModel.categoriasIds,
                    categoriaSelecionada = categoriaSelecionada,
                    onCategorySelected = { categoriaSelecionada = it },
                    modifier = Modifier.weight(1f)
                )

                DropdownDuracaoComponent(
                    opcoesTempo = opcoesTempo,
                    tempoSelecionado = tempoSelecionado,
                    onTempoSelected = { tempoSelecionado = it },
                    modifier = Modifier.weight(1.2f)
                )
            }

            // COMPONENTE ISOLADO: SELETOR DE URGÊNCIA
            SeletorUrgenciaComponent(
                urgenciaSelecionada = urgencia,
                onUrgenciaChanged = { urgencia = it }
            )

            // --- DESCRIÇÃO (CORRIGIDA COM BLOQUEIO DE CARACTERES) ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Descrição Detalhada",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${descricao.length}/$limiteMaxDescricao",
                        fontSize = 12.sp,
                        color = if (descricao.length >= limiteMaxDescricao) Color.Red else Color.Gray
                    )
                }
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { novoTexto ->
                        // Trava preventivamente também a descrição do anúncio
                        if (novoTexto.length <= limiteMaxDescricao) {
                            descricao = novoTexto
                        }
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 110.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3867F5),
                        unfocusedBorderColor = if (isDark) Color(0xFF444444) else Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFF3867F5)
                    ),
                    maxLines = 4
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- BOTÕES / PROGRESS INDICATOR ---
            if (criarServicoViewModel.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF3867F5))
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E8E93))
                    ) {
                        Text(text = "Cancelar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            focusManager.clearFocus()

                            Log.d("BOTAO_ANUNCIAR", "Iniciando processo de postagem de serviço...")
                            Log.d("BOTAO_ANUNCIAR", "-> Titulo: '$titulo'")
                            Log.d("BOTAO_ANUNCIAR", "-> Descricao: '$descricao'")
                            Log.d("BOTAO_ANUNCIAR", "-> Urgencia Enviada: '$urgencia'")
                            Log.d("BOTAO_ANUNCIAR", "-> Categoria: Nome='${categoriaSelecionada?.name}', ID=${categoriaSelecionada?.id}")
                            Log.d("BOTAO_ANUNCIAR", "-> Tempo: Texto='${tempoSelecionado?.first}', Minutos=${tempoSelecionado?.second}")
                            Log.d("BOTAO_ANUNCIAR", "-> Imagem Uri Local: $imagemUri")

                            if (titulo.isBlank() || descricao.isBlank() || categoriaSelecionada == null || tempoSelecionado == null) {
                                Toast.makeText(context, "Selecione a categoria e preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            criarServicoViewModel.postarServico(
                                context = context,
                                token = tokenAtivo,
                                titulo = titulo,
                                descricao = descricao,
                                urgencia = urgencia,
                                tempoEstimado = tempoSelecionado!!.second,
                                categoryId = categoriaSelecionada!!.id,
                                imagemUri = imagemUri,
                                onSuccess = {
                                    Toast.makeText(context, "Serviço anunciado com sucesso!", Toast.LENGTH_LONG).show()
                                    navController.popBackStack()
                                },
                                onError = { erro ->
                                    // A mensagem customizada ou erro 409 mapeado no ViewModel será exibido aqui perfeitamente!
                                    Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759))
                    ) {
                        Text(text = "Anunciar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}