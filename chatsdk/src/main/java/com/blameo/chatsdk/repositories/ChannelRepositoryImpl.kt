package com.blameo.chatsdk.repositories

import android.util.Log
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.repositories.remote.net.APIProvider
import com.blameo.chatsdk.repositories.remote.ChannelRemoteRepository
import com.blameo.chatsdk.repositories.remote.ChannelRemoteRepositoryImpl
import com.blameo.chatsdk.controllers.ChannelListener
import com.blameo.chatsdk.models.bodies.ChannelsBody
import com.blameo.chatsdk.models.pojos.RemoteUserChannel
import com.blameo.chatsdk.models.results.GetMembersOfMultiChannelResult
import com.blameo.chatsdk.models.results.MembersInChannel
import com.blameo.chatsdk.repositories.local.*
import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil
import java.util.*
import kotlin.collections.ArrayList

interface ChannelRepository {
    fun getChannels()
    fun createChannel(body: CreateChannelBody)
    fun getUsersInChannel(id: String)
    fun getLocalUsersInChannel(channelId: String): ArrayList<RemoteUserChannel>
    fun putTypingInChannel(cId: String)
    fun putStopTypingInChannel(cId: String)
    fun updateLastMessage(channelId: String, messageId: String)
    fun addNewChannel(channel: Channel)
    fun getLocalChannelById(id: String): Channel
    fun inviteUsersToChannel(channelId: String, userIds: ArrayList<String>)
}

interface ChannelResultListener {
    fun onGetRemoteChannelsSuccess(channels: ArrayList<Channel>)
    fun onGetNewerChannelsSuccess(channels: ArrayList<Channel>)
    fun onGetRemoteChannelsFailed(error: String)
    fun onCreateChannelSuccess(channel: Channel)
    fun onCreateChannelFailed(error: String)
    fun onGetUsersInChannelSuccess(channelId: String, uic: ArrayList<RemoteUserChannel>)
    fun onGetUsersInChannelFailed(error: String)
    fun onInviteUsersToChannelSuccess(channelId: String, userIds: ArrayList<String>)
    fun onGetMembersOfMultiChannelSuccess(data: ArrayList<MembersInChannel>)
}

class ChannelRepositoryImpl(
    private val channelListener: ChannelListener
) : ChannelRepository, ChannelResultListener {

    var remoteChannels: ChannelRemoteRepository = ChannelRemoteRepositoryImpl( APIProvider.userAPI, this)
    private val TAG = "CHANNEL_REPO"
    private var localChannels: ArrayList<Channel> = arrayListOf()
    private val localChannelRepository: LocalChannelRepository = LocalChannelRepositoryImpl(BlameoChatSdk.getInstance().context)
    private val localUIC: LocalUserInChannelRepository = LocalUserInChannelRepositoryImpl(BlameoChatSdk.getInstance().context)
    private val localMessageRepository = LocalMessageRepositoryImpl(BlameoChatSdk.getInstance().context)

    override fun getChannels() {

        localChannelRepository.exportChannelDB()

        val localChannels = localChannelRepository.channels
        Log.e(TAG, "local : ${localChannels.size}")

        localChannels.forEach {
            Log.e(TAG, "local id: ${it.id} ${it.lastMessage?.content}")
        }


//       val ids = localUIC.getAllChannelIds(BlameoChatSdk.getInstance().uId)
//        ids.forEach {
//            val c = localChannelRepository.getChannelByID(it)
//            if(c != null)         localChannels.add(c)
//        }
        if(localChannels.size == 0)
            remoteChannels.getChannels("")
        else {
            channelListener.onGetChannelsSuccess(localChannels)
            getLocalMembersOfMultiChannel(localChannels)
//            remoteChannels.getNewerChannels(ids[0])
        }
    }

    override fun createChannel(body: CreateChannelBody) {
        remoteChannels.createChannel(body)
    }

    override fun getUsersInChannel(id: String) {
//        val uIds = localUIC.getAllUserIdsInChannel(id)
//        if(uIds.size == 0) {
//            println("get users in channel $id - REMOTE size: ${uIds.size}")
//            remoteChannels.getUsersInChannel(id)
//        }
//        else {
//            println("get users in channel $id - LOCAL")
//            channelListener.onGetUsersInChannelSuccess(id, uIds)
//        }
    }

    override fun getLocalUsersInChannel(channelId: String): ArrayList<RemoteUserChannel> {
        return localUIC.getAllUserIdsInChannel(channelId)
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

    override fun getLocalChannelById(id: String): Channel {
        return localChannelRepository.getChannelByID(id)
    }

    override fun inviteUsersToChannel(channelId: String, userIds: ArrayList<String>) {
        remoteChannels.inviteUserToChannel(userIds, channelId)
    }

    override fun onGetRemoteChannelsSuccess(channels: ArrayList<Channel>) {

        channelListener.onGetChannelsSuccess(channels)
        Log.e(TAG, "remote channels size: ${channels.size}")
        backupRemoteChannels(channels)
    }

    private fun backupRemoteChannels(channels: ArrayList<Channel>){

        channels.forEach {
            localChannelRepository.addLocalChannel(it)
            if(it.lastMessage != null)
                localMessageRepository.addLocalMessage(it.lastMessage)
        }
        getMembersOfMultiChannel(channels)
    }

    private fun getMembersOfMultiChannel(channels: ArrayList<Channel>){
        val ids: ArrayList<String> = arrayListOf()
        channels.forEach { ids.add(it.id) }
        remoteChannels.getMembersOfMultiChannel(ids)
    }

    private fun getLocalMembersOfMultiChannel(channels: ArrayList<Channel>){

        val data: ArrayList<MembersInChannel> = arrayListOf()
        channels.forEach {
            val membersInChannel = MembersInChannel()
            membersInChannel.channelId = it.id
            val uics = localUIC.getAllUserIdsInChannel(it.id)
            if(uics != null) {
                membersInChannel.userChannels = uics
                data.add(membersInChannel)
            }
        }

        Log.e(TAG, "local data size: ${data.size}")
        channelListener.onGetMembersOfMultiChannelSuccess(data)

    }

    override fun onGetNewerChannelsSuccess(channels: ArrayList<Channel>) {
        backupRemoteChannels(channels)
        channelListener.onGetNewChannelsSuccess(channels)
        channelListener.onGetChannelsSuccess(channels)
        getMembersOfMultiChannel(channels)
    }

    override fun onGetRemoteChannelsFailed(error: String) {
        channelListener.onGetChannelsSuccess(localChannels)
        Log.e(TAG, "get channels error: $error ${localChannels.size}")
    }

    override fun onCreateChannelSuccess(channel: Channel) {
        channelListener.onCreateChannelSuccess(channel)
        localChannelRepository.addLocalChannel(channel)
    }

    override fun onCreateChannelFailed(error: String) {

    }

    override fun onGetUsersInChannelSuccess(channelId: String, uic: ArrayList<RemoteUserChannel>) {
        channelListener.onGetUsersInChannelSuccess(channelId, uic)
        localUIC.saveUserIdsToChannel(channelId, uic)
    }

    override fun onGetUsersInChannelFailed(error: String) {
        channelListener.onGetUsersInChannelFailed(error)
    }

    override fun onInviteUsersToChannelSuccess(channelId: String, userIds: ArrayList<String>) {
        val uic = arrayListOf<RemoteUserChannel>()
        val time = ChatSdkDateFormatUtil.getPastTimeUTC()
        userIds.forEach {
            val uc = RemoteUserChannel()
            uc.memberId = it
            uc.lastSeen = time
            uc.lastReceive = time
            uic.add(uc)
        }
        localUIC.saveUserIdsToChannel(channelId, uic)
        channelListener.onInviteUsersToChannelSuccess(channelId, userIds)
    }

    override fun onGetMembersOfMultiChannelSuccess(data: ArrayList<MembersInChannel>) {
        Log.e(TAG, "get members of multi channels success ${data.size}")
        channelListener.onGetMembersOfMultiChannelSuccess(data)
    }

}