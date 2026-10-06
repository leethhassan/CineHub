package com.example.data.remote

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val PREFS_NAME = "cinehub_api_prefs"
    private const val KEY_BASE_URL = "base_api_url"
    private const val DEFAULT_BASE_URL = "https://api.cinehub.example.com/"

    private var authToken: String? = null
    private var currentBaseUrl: String = DEFAULT_BASE_URL
    private var retrofitInstance: Retrofit? = null
    private var apiServiceInstance: CineHubApiService? = null

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentBaseUrl = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        rebuildRetrofit()
    }

    fun setAuthToken(token: String?) {
        authToken = token
    }

    fun setBaseUrl(context: Context, newUrl: String) {
        var formatted = newUrl.trim()
        if (!formatted.endsWith("/")) formatted += "/"
        if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
            formatted = "https://$formatted"
        }
        currentBaseUrl = formatted
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_BASE_URL, formatted).apply()
        rebuildRetrofit()
    }

    fun getBaseUrl(): String = currentBaseUrl

    fun getService(): CineHubApiService {
        if (apiServiceInstance == null) {
            rebuildRetrofit()
        }
        return apiServiceInstance!!
    }

    private fun rebuildRetrofit() {
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")

            authToken?.let { token ->
                builder.header("Authorization", "Bearer $token")
            }

            chain.proceed(builder.build())
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        retrofitInstance = retrofit
        apiServiceInstance = retrofit.create(CineHubApiService::class.java)
    }
}
