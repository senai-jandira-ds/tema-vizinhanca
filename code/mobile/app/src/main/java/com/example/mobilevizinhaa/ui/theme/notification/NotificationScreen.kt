package com.example.mobilevizinhaa.ui.theme.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen() {
    // 1. ESTADOS
    var selectedFilter by remember { mutableStateOf("Todos") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedNotification by remember { mutableStateOf<NotificationItem?>(null) }

    // 2. DADOS (Ajustados para bater com os filtros)
    val notifications = listOf(
        NotificationItem(1, "Wellington", "Pedido solicitado", "16:35", Color(0xFF4CAF50)),
        NotificationItem(2, "Ana Souza", "Objeto emprestado", "15:20", Color(0xFF2196F3)),
        NotificationItem(3, "Marcos", "Solicitação enviada", "14:10", Color(0xFFFF9800)),
        NotificationItem(4, "Wellington", "Oferta de ajuda enviada", "13:00", Color(0xFF4CAF50)),
        NotificationItem(5, "Beatriz", "Solicitação de empréstimos", "12:45", Color(0xFFE91E63))
    )

    // 3. LÓGICA DE FILTRO
    val filters = listOf(
        "Todos",
        "Pedido solicitado",
        "Objeto emprestado",
        "Solicitação enviada",
        "Oferta de ajuda enviada",
        "Solicitação de empréstimos"
    )

    val filteredList = if (selectedFilter == "Todos") {
        notifications
    } else {
        notifications.filter { it.action == selectedFilter }
    }

    // 4. LAYOUT
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {

        // CABEÇALHO
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF2196F3))
                .padding(start = 24.dp, bottom = 20.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text("Notificações", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        // LINHA DE FILTROS (Arrastável para o lado)
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2196F3),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // LISTA DE NOTIFICAÇÕES
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList) { item ->
                NotificationCard(
                    notification = item,
                    onClick = {
                        selectedNotification = item
                        showDialog = true
                    }
                )
            }
        }
    }

    // 5. POPUP (DIÁLOGO)
    if (showDialog && selectedNotification != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Detalhes") },
            text = {
                Text("Morador: ${selectedNotification?.name}\nAção: ${selectedNotification?.action}\nHorário: ${selectedNotification?.time}")
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Ok")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}