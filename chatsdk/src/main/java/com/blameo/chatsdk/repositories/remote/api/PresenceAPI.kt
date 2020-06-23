package com.blameo.chatsdk.repositories.remote.api

import com.blameo.chatsdk.models.bodies.PostIDsBody
import com.blameo.chatsdk.models.bodies.UpdateStatusBody
import com.blameo.chatsdk.models.results.UsersStatusResult
import retrofit2.Call
import retrofit2.http.*

interface PresenceAPI {

    @POST("get-by-ids")
    fun getUsersStatus(
        @Body body: PostIDsBody
    ): Call<UsersStatusResult>

    @POST("update")
    fun updateStatus(
        @Body body: UpdateStatusBody
    ): Call<UsersStatusResult>

}