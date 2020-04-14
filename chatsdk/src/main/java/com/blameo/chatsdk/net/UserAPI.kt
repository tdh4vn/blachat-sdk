package com.blameo.chatsdk.net

import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.results.CreateChannelResult
import com.blameo.chatsdk.models.results.GetChannelResult
import com.blameo.chatsdk.models.results.GetUsersByIdsResult
import com.blameo.chatsdk.models.results.GetUsersInChannelResult
import io.reactivex.Single
import retrofit2.http.*

interface UserAPI {

    @GET("user/channels/me")
    fun getChannel(
        @Query("pageSize") pageSize: Int
    ): Single<GetChannelResult>

    @GET("user/channels/members/{id}")
    fun getUsersInChannels(
        @Path("id") id: String
    ): Single<GetUsersInChannelResult>

    @POST("user/members/gets")
    fun getUsersByIds(
        @Body ids: UsersBody
    ): Single<GetUsersByIdsResult>

    @POST("user/channels/create")
    fun createChannel(
        @Body body: CreateChannelBody
    ): Single<CreateChannelResult>


}