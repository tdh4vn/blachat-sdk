package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.results.CreateChannelResult
import com.blameo.chatsdk.models.results.GetChannelResult
import com.blameo.chatsdk.models.results.GetUsersInChannelResult
import io.reactivex.Single

interface ChannelRemoteRepository {

    fun getChannels(): Single<GetChannelResult>

    fun getUsersInChannel(id: String) : Single<GetUsersInChannelResult>

    fun createChannel(body: CreateChannelBody): Single<CreateChannelResult>

}