package com.blameo.chatsdk.net

import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.results.GetMessageByIDResult
import com.blameo.chatsdk.models.results.GetMessagesResult
import io.reactivex.Single
import retrofit2.http.*

interface MessageAPI {

    @GET("get-by-id/{id}")
    fun getMessageById(
        @Path("id") id: String
    ): Single<GetMessageByIDResult>

    @POST("create")
    fun createMessage(
        @Body body: CreateMessageBody
    ): Single<GetMessageByIDResult>

    @GET("channel/{channelId}")
    fun getMessagesInChannel(
        @Path("channelId") channelId: String,
        @Query("lastId") lastId: String
    ): Single<GetMessagesResult>


}