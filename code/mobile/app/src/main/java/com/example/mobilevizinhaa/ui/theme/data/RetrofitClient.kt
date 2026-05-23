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
     * Localiza e remove o ";charset=UTF-8" de qualquer requisição (seja Multipart ou JSON puro).
     * Isso impede que o servidor rejeite payloads pesados contendo Strings Base64.
     */
    private val contentTypeInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBody = originalRequest.body

        if (requestBody != null) {
            val contentTypeOriginal = requestBody.contentType()?.toString() ?: ""

            // Aplica a limpeza se contiver qualquer menção a "charset", protegendo tanto JSON quanto Multipart
            if (contentTypeOriginal.contains("charset", ignoreCase = true)) {

                // Realiza a limpeza tratando variações comuns de espaçamento e caixa das letras
                val novoTipoString = contentTypeOriginal
                    .replace(";charset=UTF-8", "", ignoreCase = true)
                    .replace("; charset=UTF-8", "", ignoreCase = true)
                    .replace(";charset=utf-8", "", ignoreCase = true)
                    .replace("; charset=utf-8", "", ignoreCase = true)

                val novoContentType = novoTipoString.toMediaTypeOrNull()

                // Reconstrói o corpo da requisição com a nova etiqueta de Content-Type limpa
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

        // Devolve a requisição pronta e limpa para seguir viagem para o servidor
        return@Interceptor chain.proceed(originalRequest)
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
        // O nosso limpador de cabeçalho roda obrigatoriamente ANTES do logger para vermos a requisição purificada
        .addInterceptor(contentTypeInterceptor)
        .addInterceptor(loggingInterceptor)
        // Timeouts de 30s expandidos para o Render.com não derrubar conexões lentas de upload
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