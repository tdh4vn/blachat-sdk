package com.blameo.chatsdk.net

import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.results.*
import retrofit2.Call
import retrofit2.http.*

interface UserAPI {

    @GET("user/channels/me")
    fun getChannel(
        @Query("pageSize") pageSize: Int
    ): Call<GetChannelResult>

    @GET("user/channels/members/{id}")
    fun getUsersInChannels(
        @Path("id") id: String
    ): Call<GetUsersInChannelResult>

    @POST("user/members/gets")
    fun getUsersByIds(
        @Body ids: UsersBody
    ): Call<GetUsersByIdsResult>

    @POST("user/channels/create")
    fun createChannel(
        @Body body: CreateChannelBody
    ): Call<CreateChannelResult>

    @PUT("user/channels/events/typing/{channelID}")
    fun putTypingEvent(
        @Path("channelID") channelID: String
    ): Call<BaseResult>

    @PUT("user/channels/events/stop-typing/{channelID}")
    fun putStopTypingEvent(
        @Path("channelID") channelID: String
    ): Call<BaseResult>

}