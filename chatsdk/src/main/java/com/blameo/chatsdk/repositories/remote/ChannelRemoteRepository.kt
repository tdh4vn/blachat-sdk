package com.blameo.chatsdk.repositories.remote

import com.blameo.chatsdk.models.bodies.ChannelsBody
import com.blameo.chatsdk.models.bodies.CreateChannelBody

interface ChannelRemoteRepository {

    fun getChannels(pageSize: String)

    fun getNewerChannels(id: String)

    fun getUsersInChannel(id: String)

    fun createChannel(body: CreateChannelBody)

    fun putTypingInChannel(cId: String)

    fun putStopTypingInChannel(cId: String)

    fun inviteUserToChannel(userIds: ArrayList<String>, channelId: String)

    fun getMembersOfMultiChannel(ids: ArrayList<String>)

}