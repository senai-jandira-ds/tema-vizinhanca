package com.example.mobilevizinhaa.ui.theme.home

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.mobilevizinhaa.R

data class HomeUiState(
    val userName: String = "Sarah!",
    val apartment: String = "Apartamento 100",
    val pedidosCount: Int = 3,
    val objetosCount: Int = 3
)

data class Post(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val imagemRes: Int? = null,
    val imagemUri: Uri? = null // Adicionado para suportar Galeria
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _posts = mutableStateListOf<Post>(
        Post(1, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(2, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(3, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(4, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(5, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(6, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(7, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(8, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(9, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(10, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(11, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(1, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(2, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(3, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(4, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(5, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(6, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(7, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(8, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(9, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(10, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher),
        Post(11, "Bem-vinda!", "Exemplo de postagem inicial.", R.drawable.mulher)




    )
    val posts: List<Post> = _posts

    // Agora recebe a URI da imagem escolhida
    fun adicionarPost(titulo: String, descricao: String, uri: Uri?) {
        _posts.add(0, Post(
            id = _posts.size + 1,
            titulo = titulo,
            descricao = descricao,
            imagemUri = uri
        ))
    }
}