package com.blameo.chatsdk.repositories.remote.net

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


object APIProvider {
    var currentToken: String = ""

    private var retrofitWithSession: Retrofit? = null

    private var retrofitMessage: Retrofit? = null

    private var retrofit: Retrofit? = null
    lateinit var baseUrl: String

    fun setSession(baseUrl: String, token: String) {
        this.baseUrl = "$baseUrl/v1/"
        currentToken = token
    }

    val userAPI: UserAPI
        get() {
            if (retrofitWithSession == null) {
                val httpClient = OkHttpClient.Builder()
//                    .addInterceptor(CustomInterceptor.getInstance())
                    .addInterceptor { chain ->
                        val ongoing = chain.request().newBuilder()
                        ongoing.addHeader("Authorization", currentToken)
//                        ongoing.addHeader("Content-Type", "application/x-www-form-urlencoded")
                        chain.proceed(ongoing.build())
                    }
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build()


                retrofitWithSession = Retrofit.Builder()
                    .baseUrl(this.baseUrl)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
            return retrofitWithSession!!.create(UserAPI::class.java)
        }

    val messageAPI: MessageAPI
        get() {
            if (retrofitMessage == null) {
                val httpClient = OkHttpClient.Builder()
//                    .addInterceptor(CustomInterceptor.getInstance())
                    .addInterceptor { chain ->
                        val ongoing = chain.request().newBuilder()
                        ongoing.addHeader("Authorization", currentToken)
                        chain.proceed(ongoing.build())
                    }
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build()


                retrofitMessage = Retrofit.Builder()
                    .baseUrl(this.baseUrl +"messages/")
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
            return retrofitMessage!!.create(MessageAPI::class.java)
        }

    val sessionAPI: SessionAPI
        get() {
            if (retrofit == null) {

                val httpClient = OkHttpClient.Builder()
//                    .addInterceptor(CustomInterceptor.getInstance())
                    .addInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val ongoing = chain.request().newBuilder()
                            return chain.proceed(ongoing.build())
                        }
                    })
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(this.baseUrl+"/v1/")
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
            return retrofit!!.create(SessionAPI::class.java)
        }
}