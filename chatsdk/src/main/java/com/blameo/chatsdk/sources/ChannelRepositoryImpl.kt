package com.blameo.chatsdk.sources

import android.util.Log
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.local.LocalChannelRepository
import com.blameo.chatsdk.local.LocalUserInChannelRepository
import com.blameo.chatsdk.local.LocalUserInChannelRepositoryImpl
import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.net.APIProvider
import com.blameo.chatsdk.repositories.ChannelRemoteRepository
import com.blameo.chatsdk.repositories.ChannelRemoteRepositoryImpl
import com.blameo.chatsdk.viewmodels.ChannelListener

interface ChannelRepository {
    fun getChannels()
    fun createChannel(body: CreateChannelBody)
    fun getUsersInChannel(id: String)
    fun putTypingInChannel(cId: String)
    fun putStopTypingInChannel(cId: String)
    fun updateLastMessage(channelId: String, messageId: String)
    fun addNewChannel(channel: Channel)
}

interface ChannelResultListener {
    fun onGetRemoteChannelsSuccess(channels: ArrayList<Channel>)
    fun onGetRemoteChannelsFailed(error: String)
    fun onCreateChannelSuccess(channel: Channel)
    fun onCreateChannelFailed(error: String)
    fun onGetUsersInChannelSuccess(channelId: String, ids: ArrayList<String>)
    fun onGetUsersInChannelFailed(error: String)
}

class ChannelRepositoryImpl(
    private val channelListener: ChannelListener,
    private val localChannelRepository: LocalChannelRepository,
    private val localUserInChannels: LocalUserInChannelRepository
) : ChannelRepository, ChannelResultListener {

    var remoteChannels: ChannelRemoteRepository =
        ChannelRemoteRepositoryImpl(APIProvider.userAPI, this)
    private val TAG = "CHANNEL_REPO"
    private var localChannels: ArrayList<Channel> = arrayListOf()

    private val localUIC: LocalUserInChannelRepository
            = LocalUserInChannelRepositoryImpl(BlameoChatSdk.getInstance().context)

    override fun getChannels() {

       val ids = localUIC.getAllChannelIds(BlameoChatSdk.getInstance().uId)
        Log.i(TAG, "local ids size: ${ids.size}")
        ids.forEach {
            val c = localChannelRepository.getChannelByID(it)
            if(c != null)
                localChannels.add(c)
        }

//        if(localChannels.size > 0){
//            channelListener.onGetChannelsSuccess(localChannels)
//        }else
            remoteChannels.getChannels()

    }

    override fun createChannel(body: CreateChannelBody) {
        remoteChannels.createChannel(body)
    }

    override fun getUsersInChannel(id: String) {
        val uIds = localUserInChannels.getAllUserIdsInChannel(id)
        if(uIds.size == 0) {
            println("get users in channel $id - REMOTE size: ${uIds.size}")
            remoteChannels.getUsersInChannel(id)
        }
        else {
            println("get users in channel $id - LOCAL")
            channelListener.onGetUsersInChannelSuccess(id, uIds)
        }
    }

    override fun putTypingInChannel(cId: String) {
        remoteChannels.putTypingInChannel(cId)
    }

    override fun putStopTypingInChannel(cId: String) {
        remoteChannels.putStopTypingInChannel(cId)
    }

    override fun updateLastMessage(channelId: String, messageId: String) {
        localChannelRepository.updateLastMessage(channelId, messageId)
    }

    override fun addNewChannel(channel: Channel) {
        localChannelRepository.addLocalChannel(channel)
    }

    override fun onGetRemoteChannelsSuccess(channels: ArrayList<Channel>) {
        channelListener.onGetChannelsSuccess(channels)
        channels.forEach { localChannelRepository.addLocalChannel(it) }
    }

    override fun onGetRemoteChannelsFailed(error: String) {
        channelListener.onGetChannelsSuccess(localChannels)
        channelListener.onGetChannelError(error)
    }

    override fun onCreateChannelSuccess(channel: Channel) {
        channelListener.onCreateChannelSuccess(channel)
        localChannelRepository.addLocalChannel(channel)
    }

    override fun onCreateChannelFailed(error: String) {

    }

    override fun onGetUsersInChannelSuccess(channelId: String, ids: ArrayList<String>) {
        channelListener.onGetUsersInChannelSuccess(channelId, ids)
        localUserInChannels.saveUserIdsToChannel(channelId, ids)
    }

    override fun onGetUsersInChannelFailed(error: String) {
        channelListener.onGetUsersInChannelFailed(error)
    }

}