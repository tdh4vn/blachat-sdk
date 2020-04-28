package com.blameo.chatsdk.repositories.remote

import com.blameo.chatsdk.models.bodies.CreateChannelBody

interface ChannelRemoteRepository {

    fun getChannels()

    fun getUsersInChannel(id: String)

    fun createChannel(body: CreateChannelBody)

    fun putTypingInChannel(cId: String)

    fun putStopTypingInChannel(cId: String)

}