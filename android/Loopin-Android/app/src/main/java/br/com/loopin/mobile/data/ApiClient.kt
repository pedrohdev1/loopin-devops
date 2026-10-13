package br.com.loopin.mobile.data

import br.com.loopin.mobile.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Token mantido em memória durante a sessão; não registramos cabeçalhos de autenticação em logs. */
object TokenHolder {
    @Volatile var accessToken: String? = null
}

object ApiClient {
    val api: LoopinApi by lazy {
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token = TokenHolder.accessToken
            val request = if (!token.isNullOrBlank()) {
                original.newBuilder().header("Authorization", "Bearer $token").build()
            } else original
            chain.proceed(request)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoopinApi::class.java)
    }
}
