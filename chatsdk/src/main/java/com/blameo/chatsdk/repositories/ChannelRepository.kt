package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.RemoteUserChannel

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