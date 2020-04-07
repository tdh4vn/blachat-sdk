package com.blameo.data.net

import com.blameo.data.models.results.GetChannelResult
import com.blameo.data.models.results.LikeResult
import io.reactivex.Single
import retrofit2.http.*

interface UserAPI {

    @PUT("post/like/{id}")
    @FormUrlEncoded
    fun likeFeed(
        @Path("id") id: String,
        @Field("isLike") isLike: Boolean
    ): Single<LikeResult>

    @GET("user/channels/me")
    fun getChannel(
        @Query("pageSize") pageSize: Int
    ): Single<GetChannelResult>


}