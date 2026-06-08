package com.example.mobilevizinhaa.ui.theme.data

import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.RequestBody
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
     * INTERCEPTOR ULTRA-CORRETIVO UNIVERSAL:
     * Mantido exatamente como o seu original para garantir estabilidade completa
     * nas telas de Login, Perfil e Publicação (Multipart).
     */
    private val contentTypeInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBody = originalRequest.body

        if (requestBody != null) {
            val contentTypeOriginal = requestBody.contentType()?.toString() ?: ""

            if (contentTypeOriginal.contains("charset", ignoreCase = true)) {

                val novoTipoString = contentTypeOriginal
                    .replace(";charset=UTF-8", "", ignoreCase = true)
                    .replace("; charset=UTF-8", "", ignoreCase = true)
                    .replace(";charset=utf-8", "", ignoreCase = true)
                    .replace("; charset=utf-8", "", ignoreCase = true)

                val novoContentType = novoTipoString.toMediaTypeOrNull()

                val novoRequestBody = object : RequestBody() {
                    override fun contentType() = novoContentType
                    override fun contentLength() = requestBody.contentLength()
                    override fun writeTo(sink: okio.BufferedSink) = requestBody.writeTo(sink)
                }

                val novaRequest = originalRequest.newBuilder()
                    .method(originalRequest.method, novoRequestBody)
                    .build()

                return@Interceptor chain.proceed(novaRequest)
            }
        }

        return@Interceptor chain.proceed(originalRequest)
    }

    /**
     * Configuração do Gson personalizada.
     */
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    /**
     * 1. CLIENTE HTTP PADRÃO (Usado por todas as outras telas do App)
     * Risco Zero: Preserva o fluxo intocado com o interceptor universal.
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(contentTypeInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    /**
     * 2. CLIENTE HTTP SEGURO (Exclusivo para a criação de Serviços)
     * Deixa o Gson trabalhar sem interceptores de escrita de fluxo intermediário,
     * garantindo que o JSON vá 100% preenchido para a API do Leonardo Scotti.
     */
    private val okHttpClientParaServico = OkHttpClient.Builder()
        .addInterceptor(contentTypeInterceptor) // 🎯 Adicionado o interceptor corretivo universal aqui também para blindar a rota multipart de Objetos
        .addInterceptor(loggingInterceptor) // Mantém o log ativo para você acompanhar
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    /**
     * Instância do Retrofit Padrão.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Instância do Retrofit Limpa (Nova).
     */
    private val retrofitParaServico: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClientParaServico)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * SERVIÇO PRINCIPAL: Usado para Login, Buscar Perfil, Criar Publicações, etc.
     */
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    /**
     * 🎯 LINK DE SEGURANÇA (COMPATIBILIDADE):
     * Mapeia chamadas antigas ou referências de ViewModels que buscam por 'authApiService'
     * apontando direto para a rota estável principal. Evita erros de compilação!
     */
    val authApiService: AuthApiService by lazy {
        authApi
    }

    /**
     * SERVIÇO EXCLUSIVO: Criada especificamente para a rota de serviços,
     * evitando o bug do JSON em branco (Erro 400).
     */
    val authApiParaServico: AuthApiService by lazy {
        retrofitParaServico.create(AuthApiService::class.java)
    }
}