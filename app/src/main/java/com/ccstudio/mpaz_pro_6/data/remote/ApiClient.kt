package com.ccstudio.mpaz_pro_6.data.remote

import com.ccstudio.mpaz_pro_6.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Construye el cliente HTTP de MPAZ.
 *
 * El interceptor agrega el token JWT salvo en el login. Cualquier `401` dispara
 * [onUnauthorized] para que la app limpie la sesión y vuelva al inicio.
 */
class ApiClient(
    baseUrl: String = BuildConfig.MPAZ_BASE_URL,
    private val tokenProvider: () -> String?,
    private val onUnauthorized: () -> Unit
) {

    val json: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            val isLogin = request.url.encodedPath.endsWith("/api/auth/login")
            val token = tokenProvider()

            val authenticated = if (!isLogin && !token.isNullOrBlank()) {
                request.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                request
            }

            val response = chain.proceed(authenticated)

            if (response.code == 401 && !isLogin) {
                onUnauthorized()
            }

            response
        }
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                        redactHeader("Authorization")
                    }
                )
            }
        }
        .build()

    val api: MpazApi = Retrofit.Builder()
        .baseUrl(normalizeBaseUrl(baseUrl))
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(MpazApi::class.java)

    private fun normalizeBaseUrl(value: String): String =
        if (value.endsWith('/')) value else "$value/"
}
