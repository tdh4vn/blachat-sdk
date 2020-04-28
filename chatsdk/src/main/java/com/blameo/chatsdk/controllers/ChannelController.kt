package com.blameo.chatsdk.controllers

import android.util.Log
import com.blameo.chatsdk.repositories.local.LocalChannelRepository
import com.blameo.chatsdk.repositories.local.LocalUserInChannelRepository
import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.repositories.ChannelRepository
import com.blameo.chatsdk.repositories.ChannelRepositoryImpl


interface ChannelListener{
    fun onGetChannelsSuccess(channels: ArrayList<Channel>)
    fun onGetChannelError(error: String)
    fun onCreateChannelSuccess(channel: Channel)
    fun onCreateChannelFailed(error: String)
    fun onGetUsersInChannelSuccess(channelId: String, ids: ArrayList<String>)
    fun onGetUsersInChannelFailed(error: String)
}

class ChannelController(
    private val listener: ChannelListener,
    localChannelRepository: LocalChannelRepository,
    localUserInChannels: LocalUserInChannelRepository
)
    : ChannelListener {

    private var channelRepository: ChannelRepository =
        ChannelRepositoryImpl(
            this,
            localChannelRepository,
            localUserInChannels
        )

    var localChannels : ArrayList<String> = arrayListOf()



    private val TAG = "CHANNEL_VM"

    fun getChannels() {
        channelRepository.getChannels()
    }

    fun getUsersInChannel(channelId: String) {
        channelRepository.getUsersInChannel(channelId)
    }

    fun createChannel(ids: ArrayList<String>, name: String, type: Int) {
        val body = CreateChannelBody(ids, name, type)
        channelRepository.createChannel(body)
    }

    fun putTypingInChannel(cId: String){
        channelRepository.putTypingInChannel(cId)
    }

    fun putStopTypingInChannel(cId: String){
        channelRepository.putStopTypingInChannel(cId)
    }

    fun updateLastMessage(channelId: String, messageId: String){
        channelRepository.updateLastMessage(channelId, messageId)
    }

    fun addNewChannel(channel: Channel){
        channelRepository.addNewChannel(channel)
    }

    override fun onGetChannelsSuccess(channels: ArrayList<Channel>) {

        Log.e(TAG, "result")
//        var channelsResult = (channelRepository as ChannelRepositoryImpl).getLocalChannels()
//        if(channels.size == 0){
//            channelsResult = channels
//        }

        listener.onGetChannelsSuccess(channels)

        channels.forEachIndexed { index, channel ->
            Log.e(TAG, "$index: ${channel.id}")
        }
    }

    override fun onGetChannelError(error: String) {
//        val localChannels = (channelRepository as ChannelRepositoryImpl).getLocalChannels()
//        listener.onGetChannelsSuccess(localChannels)
        listener.onGetChannelError(error)
    }

    override fun onCreateChannelSuccess(channel: Channel) {
        listener.onCreateChannelSuccess(channel)
    }

    override fun onCreateChannelFailed(error: String) {
        listener.onCreateChannelFailed(error)
    }

    override fun onGetUsersInChannelSuccess(channelId: String, ids: ArrayList<String>) {
        listener.onGetUsersInChannelSuccess(channelId, ids)
    }

    override fun onGetUsersInChannelFailed(error: String) {
        listener.onGetUsersInChannelFailed(error)
    }
}