package com.example.mobilevizinhaa.ui.theme.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilevizinhaa.R
import com.example.mobilevizinhaa.ui.theme.home.HomeViewModel
import com.example.mobilevizinhaa.ui.theme.data.ResidentResponse

@Composable
fun LoginScreen(
    navController: (String) -> Unit,
    viewModel: LoginViewModel = viewModel(),
    homeViewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    /**
     * CLIQUE ÚNICO E AUTOMÁTICO:
     * O LaunchedEffect monitora o loginResponse. Assim que a API responde com sucesso,
     * ele executa a lógica de salvar e navegar uma única vez, sem depender de cliques extras.
     */
    LaunchedEffect(uiState.loginResponse) {
        uiState.loginResponse?.response?.let { loginData ->
            val user = loginData.user

            val resident = ResidentResponse(
                id = user.id,
                name = user.name,
                email = user.email,
                apartment = user.apto,
                block = user.block,
                score = 0,
                phone = user.phone
            )

            // Salva no ViewModel (Memória + Disco)
            homeViewModel.setResidentData(resident)

            // Sincroniza em background
            homeViewModel.carregarDadosPerfil(loginData.token, user.id)

            // Navega para a Home
            navController("home")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
        Text(text = "Login", fontSize = 28.sp, color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(30.dp))

        // Campo E-mail
        TccTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = "E-mail",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = uiState.emailError != null,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.emailError != null) {
            Text(
                text = uiState.emailError!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Senha
        TccTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = "Senha",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.onLoginClicked { /* Navegação tratada pelo LaunchedEffect */ }
                }
            ),
            isError = uiState.passwordError != null,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = if (uiState.passwordError != null) Color.Red else Color.Gray
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.passwordError != null) {
            Text(
                text = uiState.passwordError!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
            )
        }

        uiState.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // BOTÃO DE CLIQUE ÚNICO
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                // Chamamos apenas a intenção de logar.
                // A navegação acontece automaticamente pelo observador lá em cima.
                viewModel.onLoginClicked { }
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White
            ),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
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
    trailingIcon: @Composable (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF1F1F1),
            unfocusedContainerColor = Color(0xFFF1F1F1),
            errorContainerColor = Color(0xFFFFEBEE),
            focusedIndicatorColor = Color(0xFF2196F3),
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Red,
            focusedLabelColor = Color(0xFF2196F3),
            unfocusedLabelColor = Color.Gray
        ),
        shape = RoundedCornerShape(25.dp)
    )
}