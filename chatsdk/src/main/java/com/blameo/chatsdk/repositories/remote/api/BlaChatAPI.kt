package com.blameo.chatsdk.repositories.remote.api

import com.blameo.chatsdk.models.bodies.*
import com.blameo.chatsdk.models.results.*
import retrofit2.Call
import retrofit2.http.*

interface BlaChatAPI {

    @GET("user/channels/me")
    fun getChannel(
        @Query("pageSize") pageSize: Long,
        @Query("lastId") lastId: String
    ): Call<GetChannelsResult>

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
    ): Call<GetChannelsResult>

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
    ): Call<GetChannelsResult>

    @GET("events/gets")
    fun getEvent(
        @Query("eventId") eventId: String
    ): Call<GetEventResult>

    @PUT("user/channels/channel/{channelId}")
    fun updateChannel(
        @Path("channelId") channelID: String,
        @Body body: UpdateChannelBody
    ): Call<GetChannelResult>

    @DELETE("user/channels/delete/{channelId}")
    fun deleteChannelById(
        @Path("channelId") channelID: String
    ): Call<BaseResult>

    @HTTP(method = "DELETE", path = "user/channels/remove-user", hasBody = true)
    fun removeUserFromChannel(
        @Body body: RemoveUserFromChannelBody
    ): Call<BaseResult>

//    @DELETE("user/channels/remove-user")
//    fun removeUserFromChannel(
//        @Body body: RemoveUserFromChannelBody
//    ): Call<BaseResult>

}