package com.blameo.chatsdk.repositories.remote

import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.results.BaseResult
import com.blameo.chatsdk.models.results.CreateChannelResult
import com.blameo.chatsdk.models.results.GetChannelResult
import com.blameo.chatsdk.models.results.GetUsersInChannelResult
import com.blameo.chatsdk.repositories.remote.net.UserAPI
import com.blameo.chatsdk.repositories.ChannelResultListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChannelRemoteRepositoryImpl(private val userAPI: UserAPI,
                                  private val channelResultListener: ChannelResultListener
)
    : ChannelRemoteRepository {

    override fun getChannels() {

        return userAPI.getChannel(10)
            .enqueue(object : Callback<GetChannelResult>{
                override fun onFailure(call: Call<GetChannelResult>, t: Throwable) {
                    channelResultListener.onGetRemoteChannelsFailed(t.message!!)
                }

                override fun onResponse(call: Call<GetChannelResult>, response: Response<GetChannelResult>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            channelResultListener.onGetRemoteChannelsSuccess(response.body()!!.data)
                        }else{
                            channelResultListener.onGetRemoteChannelsFailed("")
                        }
                    }else{
                        channelResultListener.onGetRemoteChannelsFailed("")
                    }
                }
            })
    }

    override fun getUsersInChannel(id: String) {

        return userAPI.getUsersInChannels(id)
            .enqueue(object : Callback<GetUsersInChannelResult>{
                override fun onFailure(call: Call<GetUsersInChannelResult>, t: Throwable) {
                    channelResultListener.onGetUsersInChannelFailed(t.message!!)
                }

                override fun onResponse(call: Call<GetUsersInChannelResult>, response: Response<GetUsersInChannelResult>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val ids = arrayListOf<String>()
                            response.body()!!.data.forEach {
                                ids.add(it.memberId)
                            }
                            channelResultListener.onGetUsersInChannelSuccess(id, ids)
                        }
                    }
                }
            })

    }

    override fun createChannel(body: CreateChannelBody){
        return userAPI.createChannel(body)
            .enqueue(object : Callback<CreateChannelResult>{
                override fun onFailure(call: Call<CreateChannelResult>, t: Throwable) {
                    channelResultListener.onCreateChannelFailed(t.message!!)
                }

                override fun onResponse(call: Call<CreateChannelResult>, response: Response<CreateChannelResult>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            channelResultListener.onCreateChannelSuccess(response.body()!!.data)
                        }
                    }
                }
            })

    }

    override fun putTypingInChannel(cId: String) {
        println("put typing in $cId")
        userAPI.putTypingEvent(cId)
            .enqueue(object: Callback<BaseResult>{
                override fun onFailure(call: Call<BaseResult>, t: Throwable) {

                }

                override fun onResponse(call: Call<BaseResult>, response: Response<BaseResult>) {
                    if(response.isSuccessful)
                        if(response.body()!!.success())
                            println("ok sent typing")
                }
            })
    }

    override fun putStopTypingInChannel(cId: String) {
        println("put stop typing in $cId")
        userAPI.putStopTypingEvent(cId)
            .enqueue(object: Callback<BaseResult>{
                override fun onFailure(call: Call<BaseResult>, t: Throwable) {

                }

                override fun onResponse(call: Call<BaseResult>, response: Response<BaseResult>) {
                    if(response.isSuccessful)
                        if(response.body()!!.success())
                            println("ok sent stop")
                }
            })
    }

}