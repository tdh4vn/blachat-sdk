package com.blameo.chatsdk.repositories.remote

import com.blameo.chatsdk.models.bodies.ChannelsBody
import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.bodies.InviteUserToChannelBody
import com.blameo.chatsdk.models.results.*
import com.blameo.chatsdk.repositories.remote.net.UserAPI
import com.blameo.chatsdk.repositories.ChannelResultListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChannelRemoteRepositoryImpl(private val userAPI: UserAPI,
                                  private val channelResultListener: ChannelResultListener
)
    : ChannelRemoteRepository {

    override fun getChannels(pageSize: String) {

        return userAPI.getChannel(pageSize)
            .enqueue(object : Callback<GetChannelResult>{
                override fun onFailure(call: Call<GetChannelResult>, t: Throwable) {
                    channelResultListener.onGetRemoteChannelsFailed(t.message!!)
                }

                override fun onResponse(call: Call<GetChannelResult>, response: Response<GetChannelResult>) {
                    if(response.isSuccessful){
                        if(response.body()?.data != null){
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

    override fun getNewerChannels(id: String) {

        return userAPI.getNewerChannels(id)
            .enqueue(object : Callback<GetChannelResult>{
                override fun onFailure(call: Call<GetChannelResult>, t: Throwable) {
                }

                override fun onResponse(call: Call<GetChannelResult>, response: Response<GetChannelResult>) {
                    if(response.isSuccessful) {
                        if (response.body()?.data != null) {
                            channelResultListener.onGetNewerChannelsSuccess(response.body()!!.data)
                        }
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
                        if(response.body()?.data != null){
                            channelResultListener.onGetUsersInChannelSuccess(id, response.body()?.data!!)
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
        userAPI.putTypingEvent(cId)
            .enqueue(object: Callback<BaseResult>{
                override fun onFailure(call: Call<BaseResult>, t: Throwable) {

                }

                override fun onResponse(call: Call<BaseResult>, response: Response<BaseResult>) {
                }
            })
    }

    override fun putStopTypingInChannel(cId: String) {
        userAPI.putStopTypingEvent(cId)
            .enqueue(object: Callback<BaseResult>{
                override fun onFailure(call: Call<BaseResult>, t: Throwable) {

                }

                override fun onResponse(call: Call<BaseResult>, response: Response<BaseResult>) {
                }
            })
    }

    override fun inviteUserToChannel(userIds: ArrayList<String>, channelId: String) {
        val body = InviteUserToChannelBody()
        body.userIds = userIds
        userAPI.inviteUserToChannel(channelId, body)
            .enqueue(object: Callback<BaseResult>{
                override fun onFailure(call: Call<BaseResult>, t: Throwable) {
                }

                override fun onResponse(call: Call<BaseResult>, response: Response<BaseResult>) {
                    if(response.isSuccessful){
                        if(response.body() != null)
                            channelResultListener.onInviteUsersToChannelSuccess(channelId, userIds)
                    }
                }
            })
    }

    override fun getMembersOfMultiChannel(ids: ArrayList<String>) {

        userAPI.getMembersOfMultiChannel(ChannelsBody(ids))
            .enqueue(object: Callback<GetMembersOfMultiChannelResult>{
                override fun onFailure(call: Call<GetMembersOfMultiChannelResult>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<GetMembersOfMultiChannelResult>,
                    response: Response<GetMembersOfMultiChannelResult>
                ) {
                    if(response.isSuccessful){
                        if(response.body()?.membersInChannels != null)
                            channelResultListener.onGetMembersOfMultiChannelSuccess(response.body()?.membersInChannels!!)
                    }
                }
            })
    }

}