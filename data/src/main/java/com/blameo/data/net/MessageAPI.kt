package com.blameo.data.net

import com.blameo.data.models.results.GetMessageByIDResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface MessageAPI {

    @GET("get-by-id/{id}")
    fun getMessageById(
        @Path("id") id: String
    ): Single<GetMessageByIDResult>


}