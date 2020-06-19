package com.blameo.chatsdk.repositories.remote.api

import androidx.room.Update
import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.bodies.DeleteMessageBody
import com.blameo.chatsdk.models.bodies.MarkStatusMessageBody
import com.blameo.chatsdk.models.bodies.UpdateMessageBody
import com.blameo.chatsdk.models.results.BaseResult
import com.blameo.chatsdk.models.results.GetMessageByIDResult
import com.blameo.chatsdk.models.results.GetMessagesResult
import retrofit2.Call
import retrofit2.http.*

interface MessageAPI {

    @GET("get-by-id/{id}")
    fun getMessageById(
        @Path("id") id: String
    ): Call<GetMessageByIDResult>

    @POST("create")
    fun sendMessage(
        @Body body: CreateMessageBody
    ): Call<GetMessageByIDResult>

    @GET("channel/{channelId}")
    fun getMessagesInChannel(
        @Path("channelId") channelId: String,
        @Query("lastId") lastId: String
    ): Call<GetMessagesResult>

    @POST("mark-seen")
    fun markSeenMessage(
        @Body body: MarkStatusMessageBody
    ): Call<BaseResult>

    @POST("mark-receive")
    fun markReceiveMessage(
        @Body body: MarkStatusMessageBody
    ): Call<BaseResult>

    @GET("channel/{channelId}")
    fun getNewerMessagesInChannel(
        @Path("channelId") channelId: String,
        @Query("latestId") latestId: String
    ): Call<GetMessagesResult>

    @HTTP(method = "DELETE", path = "delete", hasBody = true)
    fun deleteMessage(
        @Body body: DeleteMessageBody
    ): Call<BaseResult>

    @PUT("update")
    fun updateMessage(
        @Body body: UpdateMessageBody
    ): Call<GetMessageByIDResult>

}