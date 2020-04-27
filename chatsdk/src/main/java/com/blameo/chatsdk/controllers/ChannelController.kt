package com.blameo.chatsdk.controllers

import android.util.Log
import com.blameo.chatsdk.repositories.local.LocalChannelRepository
import com.blameo.chatsdk.repositories.local.LocalUserInChannelRepository
import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.RemoteUserChannel
import com.blameo.chatsdk.models.results.MembersInChannel
import com.blameo.chatsdk.repositories.ChannelRepository
import com.blameo.chatsdk.repositories.ChannelRepositoryImpl
import com.blameo.chatsdk.repositories.UserRepository
import com.blameo.chatsdk.repositories.UserRepositoryImpl


interface ChannelListener{
    fun onGetChannelsSuccess(channels: ArrayList<Channel>)
    fun onGetChannelError(error: String)
    fun onCreateChannelSuccess(channel: Channel)
    fun onCreateChannelFailed(error: String)
    fun onGetUsersInChannelSuccess(channelId: String, uic: ArrayList<RemoteUserChannel>)
    fun onGetUsersInChannelFailed(error: String)
    fun onInviteUsersToChannelSuccess(channelId: String, userIds: ArrayList<String>)
    fun onGetMembersOfMultiChannelSuccess(data: ArrayList<MembersInChannel>)
}

interface SDKResultListener{

}

interface ChannelResultListener{
    fun abc()
}

interface UserResultListener{
    fun  \
}

private val channelListener = object : ChannelResultListener{
    override fun abc() {

    }
}

class ChannelController(
    private val listener: SDKResultListener,
    localChannelRepository: LocalChannelRepository
)
    : ChannelListener,  {

    private val channelRepository: ChannelRepository =
        ChannelRepositoryImpl(
            this,
            localChannelRepository
        )

    private val userRepository: UserRepository = UserRepositoryImpl(this, )

    private val TAG = "CHANNEL_VM"

    fun getChannels() {
        channelRepository.getChannels()
    }

    fun getUsersInChannel(channelId: String) {
        channelRepository.getUsersInChannel(channelId)
    }

    fun getLocalUsersInChannel(channelId: String): ArrayList<RemoteUserChannel>{
        return channelRepository.getLocalUsersInChannel(channelId)
    }

    fun getLocalChannelById(id: String): Channel{
        return channelRepository.getLocalChannelById(id)
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

    fun inviteUsersToChannel(channelId: String, userIds: ArrayList<String>){
        channelRepository.inviteUsersToChannel(channelId, userIds)
    }

    override fun onGetChannelsSuccess(channels: ArrayList<Channel>) {

        Log.e(TAG, "result")
//        var channelsResult = (channelRepository as ChannelRepositoryImpl).getLocalChannels()
//        if(channels.size == 0){
//            channelsResult = channels
//        }

 //       listener.onGetChannelsSuccess(channels)

        channels.forEachIndexed { index, channel ->
            Log.e(TAG, "$index: ${channel.id}")
        }
    }

    override fun onGetChannelError(error: String) {
//        listener.onGetChannelError(error)
    }

    override fun onCreateChannelSuccess(channel: Channel) {
//        listener.onCreateChannelSuccess(channel)
    }

    override fun onCreateChannelFailed(error: String) {
//        listener.onCreateChannelFailed(error)
    }

    override fun onGetUsersInChannelSuccess(channelId: String, uic: ArrayList<RemoteUserChannel>) {
//        listener.onGetUsersInChannelSuccess(channelId, uic)
    }

    override fun onGetUsersInChannelFailed(error: String) {
 //       listener.onGetUsersInChannelFailed(error)
    }

    override fun onInviteUsersToChannelSuccess(channelId: String, userIds: ArrayList<String>) {
//        listener.onInviteUsersToChannelSuccess(channelId, userIds)
    }

    override fun onGetMembersOfMultiChannelSuccess(data: ArrayList<MembersInChannel>) {

    }
}