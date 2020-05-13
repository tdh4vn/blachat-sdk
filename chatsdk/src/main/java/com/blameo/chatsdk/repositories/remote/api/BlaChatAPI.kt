package com.blameo.chatsdk.repositories.remote.api

import com.blameo.chatsdk.models.bodies.ChannelsBody
import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.bodies.InviteUserToChannelBody
import com.blameo.chatsdk.models.results.*
import retrofit2.Call
import retrofit2.http.*

interface BlaChatAPI {

    @GET("user/channels/me")
    fun getChannel(
        @Query("pageSize") pageSize: Long,
        @Query("lastId") lastId: String
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

    @GET("user/members/members")
    fun getAllMembers(
    ): Call<GetUsersByIdsResult>

    @GET("user/channels/channel")
    fun getNewerChannels(
        @Query("channelId") channelId: String
    ): Call<GetChannelResult>

    @POST("user/channels/invite/{channelId}")
    fun inviteUserToChannel(
        @Path("channelId") channelID: String,
        @Body body: InviteUserToChannelBody
    ): Call<BaseResult>

    @POST("user/channels/members")
    fun getMembersOfMultiChannel(
        @Body body: ChannelsBody
    ): Call<GetMembersOfMultiChannelResult>

    @POST("user/channels/multi-channel")
    fun getChannelByIds(
        @Body body: ChannelsBody
    ): Call<GetChannelResult>

}