package com.blameo.chatsdk.repositories.remote.net

import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.bodies.PostIDsBody
import com.blameo.chatsdk.models.bodies.UpdateStatusBody
import com.blameo.chatsdk.models.results.BaseResult
import com.blameo.chatsdk.models.results.GetMessageByIDResult
import com.blameo.chatsdk.models.results.GetMessagesResult
import com.blameo.chatsdk.models.results.UsersStatusResult
import com.blameo.chatsdk.repositories.remote.MessageRemoteRepositoryImpl
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