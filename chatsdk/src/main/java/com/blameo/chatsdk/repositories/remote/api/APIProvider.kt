package com.blameo.chatsdk.repositories.remote.api

import com.blameo.chatsdk.utils.GsonDateFormatter
import com.blameo.chatsdk.utils.GsonHashMapFormatter
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


object  APIProvider {
    var currentToken: String = ""

    private var retrofitWithSession: Retrofit? = null

    private var retrofitMessage: Retrofit? = null

    private var retrofitPresence: Retrofit? = null

    private var retrofit: Retrofit? = null
    lateinit var baseUrl: String
    private var presenceUrl: String? = null

    fun setSession(baseUrl: String, token: String) {
        this.baseUrl = "$baseUrl:9000/v1/"
        currentToken = "Bearer $token"
        this.presenceUrl = "$baseUrl:8081/"
    }

    val blaChatAPI: BlaChatAPI
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

                val gson = GsonBuilder()
                    .registerTypeAdapter(HashMap::class.java, GsonHashMapFormatter())
                    .registerTypeAdapter(Date::class.java, GsonDateFormatter())
                    .create()

                retrofitWithSession = Retrofit.Builder()
                    .baseUrl(this.baseUrl)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
            return retrofitWithSession!!.create(BlaChatAPI::class.java)
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

                val gson = GsonBuilder()
                    .registerTypeAdapter(HashMap::class.java, GsonHashMapFormatter())
                    .registerTypeAdapter(Date::class.java, GsonDateFormatter())
                    .create()


                retrofitMessage = Retrofit.Builder()
                    .baseUrl(this.baseUrl +"messages/")
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
            return retrofitMessage!!.create(MessageAPI::class.java)
        }

    val presenceAPI: PresenceAPI
        get() {
            if (retrofitPresence == null) {
                val httpClient = OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val ongoing = chain.request().newBuilder()
                        ongoing.addHeader("Authorization", currentToken)
                        chain.proceed(ongoing.build())
                    }
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build()

                val gson = GsonBuilder()
                    .registerTypeAdapter(HashMap::class.java, GsonHashMapFormatter())
                    .registerTypeAdapter(Date::class.java, GsonDateFormatter())
                    .create()


                retrofitPresence = Retrofit.Builder()
                    .baseUrl(presenceUrl!!)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
            return retrofitPresence!!.create(PresenceAPI::class.java)
        }

}