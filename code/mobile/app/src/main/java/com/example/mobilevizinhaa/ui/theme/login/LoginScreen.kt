package com.example.mobilevizinhaa.ui.theme.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.BluePrimary
import com.example.mobilevizinhaa.ui.theme.GrayBackground
import com.example.mobilevizinhaa.ui.theme.GrayText
import com.example.mobilevizinhaa.ui.theme.White

@Composable
fun LoginScreen(
    navController: (String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    // Estados de erro baseados no que o ViewModel processou
    val emailError = uiState.emailError != null
    val passwordError = uiState.passwordError != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(160.dp).aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(text = "Login", fontSize = 28.sp, color = BluePrimary)

        Spacer(modifier = Modifier.height(30.dp))

        // --- CAMPO EMAIL ---
        TccTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = "E-mail",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth(),
            isError = emailError
        )

        if (emailError) {
            Text(
                text = uiState.emailError ?: "E-mail inválido",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CAMPO SENHA ---
        TccTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = "Senha",
            keyboardType = KeyboardType.Password,
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = if (passwordError) Color.Red else GrayText
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        if (passwordError) {
            Text(
                text = uiState.passwordError ?: "A senha deve ter no mínimo 8 caracteres",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- BOTÃO DE LOGIN ---
        Button(
            onClick = {
                // AQUI ESTAVA O SEU ERRO: Adicionamos os parâmetros email e senha
                // para aceitar o que o ViewModel envia
                viewModel.onLoginClicked { email, senha ->
                    navController("home")
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BluePrimary,
                contentColor = White
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "ENTRAR", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TccTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GrayBackground,
            unfocusedContainerColor = GrayBackground,
            errorContainerColor = Color(0xFFFFEBEE),
            focusedIndicatorColor = BluePrimary,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Red,
            errorLabelColor = Color.Red,
            errorTrailingIconColor = Color.Red
        ),
        shape = RoundedCornerShape(25.dp)
    )
}