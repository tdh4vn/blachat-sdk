package com.blameo.data.net

import com.blameo.data.models.pojos.Me
import io.reactivex.Single
import retrofit2.http.*

interface SessionAPI {

    @POST("login")
    @FormUrlEncoded
    fun login(@Field("idToken") idToken: String): Single<Me>

}