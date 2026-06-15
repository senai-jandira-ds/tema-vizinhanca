package com.example.mobilevizinhaa.ui.theme.mural

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilevizinhaa.ui.theme.*

@Composable
fun MuralScreen(viewModel: MuralViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        // Cabeçalho (Pode ser um componente também se você usar em outras telas)
        Box(
            modifier = Modifier.fillMaxWidth().height(140.dp)
                .background(brush = Brush.verticalGradient(colors = listOf(GradientBlueStart, GradientBlueEnd)))
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                Text("Mural", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Veja as últimas atualizações", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }

        // Lista observando o estado do ViewModel
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(uiState.posts) { post ->
                PostItem(post = post)
            }
        }
    }
}