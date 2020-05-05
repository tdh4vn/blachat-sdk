package com.blameo.chatsdk.repositories.remote

import android.util.Log
import com.blameo.chatsdk.models.bodies.PostIDsBody
import com.blameo.chatsdk.models.bodies.UpdateStatusBody
import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.results.GetUsersByIdsResult
import com.blameo.chatsdk.models.results.UsersStatusResult
import com.blameo.chatsdk.repositories.remote.net.UserAPI
import com.blameo.chatsdk.repositories.UserResultListener
import com.blameo.chatsdk.repositories.remote.net.PresenceAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRemoteRepositoryImpl(
    private val userAPI: UserAPI,
    private val presenceAPI: PresenceAPI,
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
                    else
                        listener.onGetUsersFailed(response.errorBody().toString())
                }
            })
    }

    override fun getAllMembers() {

        return userAPI.getAllMembers()
            .enqueue(object : Callback<GetUsersByIdsResult>{
                override fun onFailure(call: Call<GetUsersByIdsResult>, t: Throwable) {
                    listener.onGetMembersFailed(t.message!!)
                }

                override fun onResponse(
                    call: Call<GetUsersByIdsResult>,
                    response: Response<GetUsersByIdsResult>
                ) {
                    if(response.isSuccessful)
                        listener.onGetAllMembersSuccess(response.body()?.data!!)
                    else
                        listener.onGetMembersFailed("")
                }
            })
    }

    override fun getUsersStatus(body: PostIDsBody) {
        return presenceAPI.getUsersStatus(body)
            .enqueue(object : Callback<UsersStatusResult>{
                override fun onFailure(call: Call<UsersStatusResult>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<UsersStatusResult>,
                    response: Response<UsersStatusResult>
                ) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            listener.onGetUsersStatusSuccess(response.body()?.data!!)
                        }
                    }
                }
            })

    }

    override fun updateStatus(body: UpdateStatusBody) {
        return presenceAPI.updateStatus(body)
            .enqueue(object : Callback<UsersStatusResult>{
                override fun onFailure(call: Call<UsersStatusResult>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<UsersStatusResult>,
                    response: Response<UsersStatusResult>
                ) {}
            })
    }

}