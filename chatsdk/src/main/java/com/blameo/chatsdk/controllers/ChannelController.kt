package com.blameo.chatsdk.controllers

import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.repositories.local.LocalUserInChannelRepository
import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.RemoteUserChannel
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.models.results.MembersInChannel
import com.blameo.chatsdk.models.results.UserStatus
import com.blameo.chatsdk.repositories.ChannelRepository
import com.blameo.chatsdk.repositories.ChannelRepositoryImpl
import com.blameo.chatsdk.repositories.UserRepository
import com.blameo.chatsdk.repositories.UserRepositoryImpl
import com.blameo.chatsdk.repositories.local.LocalUserInChannelRepositoryImpl


interface ChannelListener {
    fun onGetChannelsSuccess(channels: ArrayList<Channel>)
    fun onGetNewChannelsSuccess(channels: ArrayList<Channel>)
    fun onGetChannelError(error: String)
    fun onCreateChannelSuccess(channel: Channel)
    fun onCreateChannelFailed(error: String)
    fun onGetUsersInChannelSuccess(channelId: String, uic: ArrayList<RemoteUserChannel>)
    fun onGetUsersInChannelFailed(error: String)
    fun onInviteUsersToChannelSuccess(channelId: String, userIds: ArrayList<String>)
    fun onGetMembersOfMultiChannelSuccess(data: ArrayList<MembersInChannel>)
}

interface SdkChannelListener {
    fun onGetChannelsSuccess(channels: ArrayList<Channel>)
    fun onNewChannels(channels: ArrayList<Channel>)
    fun onCreateChannelSuccess(channel: Channel)
    fun onCreateChannelFailed(error: String)
    fun onGetUsersInChannelSuccess(channelId: String, uic: ArrayList<RemoteUserChannel>)
    fun onGetUsersInChannelFailed(error: String)
    fun onInviteUsersToChannelSuccess(channelId: String, userIds: ArrayList<String>)
}

class ChannelController(private val sdkListener: SdkChannelListener) : ChannelListener {

    private val channelRepository: ChannelRepository = ChannelRepositoryImpl(this)
    private val localUIC: LocalUserInChannelRepository =
        LocalUserInChannelRepositoryImpl(BlameoChatSdk.getInstance().context)
    private val userId = BlameoChatSdk.getInstance().uId
    private val usersMap: HashMap<String, User> = hashMapOf()
    private var membersInChannels: ArrayList<MembersInChannel> = arrayListOf()
    private var uicMap: HashMap<String, ArrayList<RemoteUserChannel>> = hashMapOf()
    private lateinit var channels: ArrayList<Channel>
    private var newerChannels: ArrayList<Channel> = arrayListOf()

    fun getUicMap(): HashMap<String, ArrayList<RemoteUserChannel>> {
        return uicMap
    }

    fun getUsersMap(): HashMap<String, User> {
        return usersMap
    }

    fun addUsersToMap(users: ArrayList<User>) {
        users.forEach {
            usersMap[it.id] = it
        }
    }

    private val userListener = object : UserListener {
        override fun onUsersByIdsSuccess(channelId: String, users: ArrayList<User>) {
            addUsersToMap(users)
            addUsersToChannels()
        }

        override fun onGetUsersByIdsError(error: String) {

        }

        override fun onGetAllMembersSuccess(users: ArrayList<User>) {

        }

        override fun onGetAllMembersError(error: String) {

        }

        override fun onUserStatusChanged(user: UserStatus) {

        }
    }

    private fun addUsersToChannels() {

        channels.forEach { channel ->
            membersInChannels.forEach { membersInChannel ->
                if (channel.id == membersInChannel.channelId) {
                    localUIC.saveUserIdsToChannel(channel.id, membersInChannel.userChannels)
                    if (membersInChannel.userChannels.size == 2) {
                        membersInChannel.userChannels.forEach {
                            if (it.memberId != userId) {
                                val partner = usersMap[it.memberId]
                                if (partner != null) {
                                    channel.name = partner.name
                                    channel.avatar = partner.avatar
                                }
                            }
                        }
                    }
                    return@forEach
                }
            }
        }

        if (newerChannels.size == 0)
            sdkListener.onGetChannelsSuccess(channels)
        else
            sdkListener.onNewChannels(channels)

    }

    private val userRepository: UserRepository = UserRepositoryImpl(userListener)

    private val TAG = "CHANNEL_VM"

    fun getChannels() {
        channelRepository.getChannels()
    }

    fun getLocalUsers(channelId: String): ArrayList<User> {
        val uic = uicMap[channelId]
        val users = arrayListOf<User>()
        uic?.forEach {
            val user = usersMap[it.memberId]
            if (user != null)
                users.add(user)
        }

        return users
    }

    fun getLocalUsersInChannel(channelId: String): ArrayList<RemoteUserChannel> {
        val ram = uicMap[channelId]
        if (ram != null) return ram
        return channelRepository.getLocalUsersInChannel(channelId)
    }

    fun createChannel(ids: ArrayList<String>, name: String, type: Int) {
        val body = CreateChannelBody(ids, name, type)
        channelRepository.createChannel(body)
    }

    fun putTypingInChannel(cId: String) {
        channelRepository.putTypingInChannel(cId)
    }

    fun putStopTypingInChannel(cId: String) {
        channelRepository.putStopTypingInChannel(cId)
    }

    fun updateLastMessage(channelId: String, messageId: String) {
        channelRepository.updateLastMessage(channelId, messageId)
    }

    fun addNewChannel(channel: Channel) {
        channelRepository.addNewChannel(channel)
    }

    fun inviteUsersToChannel(channelId: String, userIds: ArrayList<String>) {
        channelRepository.inviteUsersToChannel(channelId, userIds)
    }

    override fun onGetChannelsSuccess(channels: ArrayList<Channel>) {

        this.channels = channels
    }

    override fun onGetNewChannelsSuccess(channels: ArrayList<Channel>) {
        newerChannels = channels
    }

    override fun onGetChannelError(error: String) {
//        listener.onGetChannelError(error)
    }

    override fun onCreateChannelSuccess(channel: Channel) {
        sdkListener.onCreateChannelSuccess(channel)
    }

    override fun onCreateChannelFailed(error: String) {
        sdkListener.onCreateChannelFailed(error)
    }

    override fun onGetUsersInChannelSuccess(channelId: String, uic: ArrayList<RemoteUserChannel>) {
        sdkListener.onGetUsersInChannelSuccess(channelId, uic)
    }

    override fun onGetUsersInChannelFailed(error: String) {
        sdkListener.onGetUsersInChannelFailed(error)
    }

    override fun onInviteUsersToChannelSuccess(channelId: String, userIds: ArrayList<String>) {
        sdkListener.onInviteUsersToChannelSuccess(channelId, userIds)
    }

    override fun onGetMembersOfMultiChannelSuccess(data: ArrayList<MembersInChannel>) {
        val map: HashMap<String, String> = hashMapOf()
        membersInChannels.addAll(data)
        data.forEach {
            uicMap[it.channelId] = it.userChannels
            it.userChannels.forEach { user ->
                map[user.memberId] = user.memberId
            }
        }

        userRepository.getUsersByIds("", ArrayList(map.keys))
    }
}