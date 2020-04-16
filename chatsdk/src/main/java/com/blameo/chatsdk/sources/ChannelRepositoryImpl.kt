package com.blameo.chatsdk.sources

import com.blameo.chatsdk.local.LocalChannelRepository
import com.blameo.chatsdk.local.LocalUserInChannelRepository
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

    override fun getChannels() {
        localChannels = localChannelRepository.channels
        if(localChannels.size > 0){
            channelListener.onGetChannelsSuccess(localChannels)
        }else
            remoteChannels.getChannels()

    }

    override fun createChannel(body: CreateChannelBody) {
        remoteChannels.createChannel(body)
    }

    override fun getUsersInChannel(id: String) {
        remoteChannels.getUsersInChannel(id)
    }

    override fun putTypingInChannel(cId: String) {
        remoteChannels.putTypingInChannel(cId)
    }

    override fun putStopTypingInChannel(cId: String) {
        remoteChannels.putStopTypingInChannel(cId)
    }

    override fun onGetRemoteChannelsSuccess(channels: ArrayList<Channel>) {
        channelListener.onGetChannelsSuccess(localChannelRepository.channels)
        channels.forEach { localChannelRepository.addLocalChannel(it) }
    }

    override fun onGetRemoteChannelsFailed(error: String) {
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

    fun getLocalChannels(): ArrayList<Channel> {
        return this.localChannels
    }

}