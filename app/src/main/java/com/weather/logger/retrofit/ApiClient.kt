package com.weather.logger.retrofit

import com.weather.logger.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient{

    private var retrofit: Retrofit? = null

    val client: Retrofit?
        get() {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)

            httpClient.connectTimeout(2, TimeUnit.MINUTES)
            httpClient.readTimeout(2, TimeUnit.MINUTES)
            httpClient.writeTimeout(2, TimeUnit.MINUTES)

            val gson = GsonBuilder()
                .setLenient()
                .create()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl("http://api.openweathermap.org/data/2.5/")
//                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build()
            }
            return retrofit
        }

}