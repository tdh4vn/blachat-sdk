package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.results.GetUsersByIdsResult
import com.blameo.chatsdk.net.UserAPI
import com.blameo.chatsdk.sources.UserResultListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRemoteRepositoryImpl(
    private val userAPI: UserAPI,
    private val listener: UserResultListener
) : UserRemoteRepository {

    override fun getUsersByIds(channelID: String, body: UsersBody) {
        return userAPI.getUsersByIds(body)
            .enqueue(object : Callback<GetUsersByIdsResult>{
                override fun onFailure(call: Call<GetUsersByIdsResult>, t: Throwable) {
                    listener.onGetUsersFailed(t.message!!)
                }

                override fun onResponse(
                    call: Call<GetUsersByIdsResult>,
                    response: Response<GetUsersByIdsResult>
                ) {
                    if(response.isSuccessful)
                        listener.onGetUsersSuccess(channelID, response.body()?.data!!)
                }
            })
    }

}