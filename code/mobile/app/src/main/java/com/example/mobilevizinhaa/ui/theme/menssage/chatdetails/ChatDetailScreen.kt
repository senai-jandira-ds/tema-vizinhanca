package com.example.mobilevizinhaa.ui.theme.menssage.chatdetails

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
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

@Composable
fun ChatDetalheScreen(
    id: String?,
    navController: NavController,
    viewModel: MessagesViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val chat = uiState.conversations.find { it.id.toString() == id }

    var textState by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // URI que guardará o caminho oficial na Galeria
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para tirar foto
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            chat?.let { viewModel.sendMessage(it.id, text = "", imageUri = tempUri.toString()) }
        }
    }

    // Launcher para escolher da galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { chat?.let { c -> viewModel.sendMessage(c.id, text = "", imageUri = it.toString()) } }
    }

    // Launcher de Permissão
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Se o user acabou de permitir, podemos disparar a lógica da câmera
            Toast.makeText(context, "Permissão concedida!", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchCamera() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // USANDO O MÉTODO PROFISSIONAL DE MEDIASTORE
            val uri = createImageUri(context)
            if (uri != null) {
                tempUri = uri
                cameraLauncher.launch(uri)
            }
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Scroll automático
    LaunchedEffect(chat?.messages?.size) {
        chat?.messages?.size?.let { size ->
            if (size > 0) listState.animateScrollToItem(size - 1)
        }
    }

    Scaffold(
        topBar = { chat?.let { ChatHeader(it, onBack = { navController.popBackStack() }) } },
        bottomBar = {
            ChatInputBar(
                text = textState,
                onValueChange = { textState = it },
                onSendClick = {
                    if (textState.isNotBlank()) {
                        chat?.let {
                            viewModel.sendMessage(it.id, textState)
                            textState = ""
                        }
                    }
                },
                onGalleryClick = { galleryLauncher.launch("image/*") },
                onCameraClick = { launchCamera() }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFE5DDD5))) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                chat?.let {
                    items(it.messages) { msg -> WhatsAppBubble(msg) }
                }
            }
        }
    }
}