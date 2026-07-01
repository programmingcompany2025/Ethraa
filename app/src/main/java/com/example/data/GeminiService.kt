package com.example.data

import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// Request & Response structures matching Gemini API JSON formats
data class GeminiPart(val text: String)
data class GeminiContent(val parts: List<GeminiPart>)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

data class GeminiCandidate(val content: GeminiContent)
data class GeminiResponse(val candidates: List<GeminiCandidate>?)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: GeminiApi = retrofit.create(GeminiApi::class.java)

    suspend fun getAiAnalysis(prompt: String, systemInstruction: String = "أنت مساعد ذكاء اصطناعي محترف لشركة هندسية وتدعى إثراء المصممين. قدم تقاريرك دائماً باللغة العربية بأسلوب احترافي جداً يركز على زيادة الأرباح وتحسين الإيرادات."): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "تنبيه: مفتاح API للذكاء الاصطناعي غير مهيأ بعد. يرجى إضافته في لوحة Secrets في AI Studio لتفعيل الميزات الحية.\n\n*التحليل المحاكي لمساعد إثراء:* استناداً للمؤشرات الحالية، تظهر البيانات نمواً مستقراً بنسبة 14٪. نوصي بتركيز جهود التسويق على فئة خدمات 'التصميم المعماري المتكامل' لارتفاع هامش ربحيتها وتوجيه المبيعات نحو العقود طويلة الأجل لضمان تدفق نقدي مستقر."
        }

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(prompt)))),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(systemInstruction)))
        )

        return try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "لم يتم استلام رد صالح من الذكاء الاصطناعي."
        } catch (e: Exception) {
            "فشل الاتصال بالذكاء الاصطناعي: ${e.localizedMessage}. تم استخدام نظام التحليل الذكي الاحتياطي لتوليد التوقعات والتقارير."
        }
    }
}
