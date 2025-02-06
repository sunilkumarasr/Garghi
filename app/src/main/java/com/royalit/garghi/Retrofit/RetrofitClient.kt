package com.royalit.garghi.Retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://divyaangdisha.com/disability/api/"
    const val Image_Path = "https://divyaangdisha.com/disability/"
    const val MapKey = "AIzaSyCMpzC9h1qmCRgC6SYHVzKhn4vFHztXp-A"
//    https://ritps.com/garghi_new/api
//    const val MapKey = "AIzaSyCMpzC9h1qmCRgC6SYHVzKhn4vFHztXp-A"
//    const val MapKey = "AIzaSyASaGw-Knef0CC9LZI7pcQh66_ywg6xrT8"

    // Initialize logging interceptor
    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Initialize OkHttpClient with interceptors
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Increase connection timeout
        .readTimeout(60, TimeUnit.SECONDS)    // Increase read timeout
        .writeTimeout(60, TimeUnit.SECONDS)   // Increase write timeout
        .addInterceptor(interceptor)
        .addInterceptor(RequestInterceptor) // Add the custom interceptor
        .build()

    // Initialize Retrofit with OkHttpClient
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Create ApiInterface instance
    val apiInterface: ApiInterface = retrofit.create(ApiInterface::class.java)
}

object RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
            .header("Content-Type", "application/json")
        // Uncomment and set your API key if needed
        //.header("api_key", API_KEY)

        return chain.proceed(requestBuilder.build())
    }
}