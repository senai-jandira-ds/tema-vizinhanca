package com.example.mobilevizinhaa.ui.theme.menssage.chatdetails

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilevizinhaa.ui.theme.menssage.MessagesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatDetalheScreen(
    id: String?,
    tokenUsuario: String,      // 🎯 Garanta que esta linha existe
    idUsuarioLogado: Int,      // 🎯 Garanta que esta linha existe
    navController: NavController,
    viewModel: MessagesViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Converte o ID string da rota com segurança para Long
    val idConversaLong = id?.toLongOrNull() ?: 0L

    // Localiza os metadados da conversa (Nome, Foto) na lista global do uiState
    val chatSummary = uiState.conversations.find { it.id == idConversaLong }

    val textState = remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // URI que guardará o caminho oficial na Galeria
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // 🎯 Dispara a carga do histórico de mensagens reais direto do banco de dados ao entrar na tela
    LaunchedEffect(idConversaLong, tokenUsuario, idUsuarioLogado) {
        if (idConversaLong != 0L && tokenUsuario.isNotEmpty()) {
            viewModel.abrirConversaEspecifica(tokenUsuario, idConversaLong, idUsuarioLogado)
        }
    }

    // Fecha o chat ativo na ViewModel ao sair da tela (limpa o cache da tela anterior)
    DisposableEffect(Unit) {
        onDispose {
            viewModel.fecharChatAtivo()
        }
    }

    // Launcher para tirar foto com a Câmera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            // Envia o aviso local ou mensagem de imagem simulada temporariamente
            viewModel.sendMessage(idConversaLong, "📷 Envou uma imagem")
        }
    }

    // Launcher para escolher arquivos direto da Galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.sendMessage(idConversaLong, "📷 Enviou uma imagem")
        }
    }

    // Launcher de Permissão da Câmera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permissão concedida!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "A permissão da câmera é necessária para tirar fotos.", Toast.LENGTH_LONG).show()
        }
    }

    // Helper interno para gerenciar a inicialização segura da câmera
    fun launchCamera() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val uri = createImageUri(context)
            if (uri != null) {
                tempUri = uri
                cameraLauncher.launch(uri)
            }
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // 🎯 Rolagem automática da tela para a última mensagem recebida/enviada no histórico
    LaunchedEffect(uiState.currentChatMessages.size) {
        val tamanhoLista = uiState.currentChatMessages.size
        if (tamanhoLista > 0) {
            listState.animateScrollToItem(tamanhoLista - 1)
        }
    }

    Scaffold(
        topBar = {
            // Renderiza o cabeçalho usando as informações resumidas da conversa carregada
            chatSummary?.let { summary ->
                ChatHeader(summary, onBack = { navController.popBackStack() })
            }
        },
        bottomBar = {
            ChatInputBar(
                text = textState.value,
                onValueChange = { textState.value = it },
                onSendClick = {
                    if (textState.value.isNotBlank()) {
                        viewModel.sendMessage(idConversaLong, textState.value)
                        textState.value = "" // Limpa a caixa de entrada de texto
                    }
                },
                onGalleryClick = { galleryLauncher.launch("image/*") },
                onCameraClick = { launchCamera() }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFE5DDD5)) // Cor de fundo clássica estilo WhatsApp
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // 🎯 LÊ AS MENSAGENS REAIS sincronizadas do banco de dados na ViewModel
                items(uiState.currentChatMessages) { msg ->
                    WhatsAppBubble(msg)
                }
            }
        }
    }
}
