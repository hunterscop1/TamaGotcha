package com.example.api.gemini

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val api: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun generateDialogue(
        systemInstructionText: String,
        promptText: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(IllegalStateException("API key not configured"))
        }

        try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(Part(text = promptText))
                    )
                ),
                systemInstruction = Content(
                    parts = listOf(Part(text = systemInstructionText))
                ),
                generationConfig = GenerationConfig(
                    temperature = 0.85f,
                    maxOutputTokens = 200
                )
            )

            val response = api.generateContent(apiKey, request)
            val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!generatedText.isNullOrBlank()) {
                Result.success(generatedText.trim())
            } else {
                Result.failure(IllegalStateException("Empty candidate response"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
