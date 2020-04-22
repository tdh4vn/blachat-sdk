package com.blameo.chatsdk.repositories.remote.net

import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.results.BaseResult
import com.blameo.chatsdk.models.results.GetMessageByIDResult
import com.blameo.chatsdk.models.results.GetMessagesResult
import com.blameo.chatsdk.repositories.remote.MessageRemoteRepositoryImpl
import retrofit2.Call
import retrofit2.http.*

interface MessageAPI {

    @GET("get-by-id/{id}")
    fun getMessageById(
        @Path("id") id: String
    ): Call<GetMessageByIDResult>

    @POST("create")
    fun createMessage(
        @Body body: CreateMessageBody
    ): Call<GetMessageByIDResult>

    @GET("channel/{channelId}")
    fun getMessagesInChannel(
        @Path("channelId") channelId: String,
        @Query("lastId") lastId: String
    ): Call<GetMessagesResult>

    @POST("mark-seen")
    fun markSeenMessage(
        @Body body: MessageRemoteRepositoryImpl.MarkStatusMessage
    ): Call<BaseResult>

    @POST("mark-receive")
    fun markReceiveMessage(
        @Body body: MessageRemoteRepositoryImpl.MarkStatusMessage
    ): Call<BaseResult>


}