package com.blameo.chatsdk.net

import com.blameo.chatsdk.models.pojos.Me
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SessionAPI {

    @POST("login")
    @FormUrlEncoded
    fun login(@Field("idToken") idToken: String): Single<Me>

}