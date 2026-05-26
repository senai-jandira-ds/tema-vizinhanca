package com.example.mobilevizinhaa.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- PALETA DO MODO ESCURO (Com o preto mais claro suave) ---
private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    background = Color(0xFF1C1B1F),       // O preto mais claro que você escolheu para o fundo de todas as telas
    surface = Color(0xFF252428),          // Tom de cinza escuro sutil para destacar os Cards do fundo
    onBackground = Color.White,           // Textos fora de cards ficam brancos automaticamente
    onSurface = Color.White,              // Textos dentro de cards ficam brancos automaticamente

    // Cores padrão do projeto mantidas para compatibilidade interna do Material
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// --- PALETA DO MODO CLARO ORIGINAL ---
private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    background = GrayBackground,          // Seu fundo original Color(0xFFF5F5F5)
    surface = Color.White,                // Seus cards e inputs brancos originais
    onBackground = Color.Black,           // Textos fora de cards ficam pretos automaticamente
    onSurface = Color.Black,              // Textos dentro de cards ficam pretos automaticamente

    // Cores padrão do projeto mantidas para compatibilidade interna do Material
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun MobileVizinhaçaTheme(
    // Recebe o estado booleano do SharedPreferences que vem do seu HomeViewModel através da MainActivity
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    // Escolhe dinamicamente qual paleta de cores aplicar em todo o ecossistema do app
    val colorScheme = if (isDarkMode) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}