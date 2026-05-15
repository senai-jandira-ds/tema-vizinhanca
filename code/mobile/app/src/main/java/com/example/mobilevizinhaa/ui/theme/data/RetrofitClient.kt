package com.example.mobilevizinhaa.ui.theme.data

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // A URL base deve terminar sempre com "/"
    private const val BASE_URL = "https://api-vizinhanca.onrender.com/"

    /**
     * Interceptor de Log: essencial para debugar as requisições no Logcat.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Configuração do Gson personalizada:
     * .setLenient() ajuda a aceitar JSONs mal formatados que alguns servidores enviam.
     */
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    /**
     * Configuração do Cliente HTTP (OkHttp)
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // Timeouts de 30s são ideais para o Render.com não dar erro de "Timeout"
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    /**
     * Instância do Retrofit.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            // Usando o conversor GSON com a configuração que definimos acima
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Nosso serviço de Autenticação e Residentes.
     */
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}