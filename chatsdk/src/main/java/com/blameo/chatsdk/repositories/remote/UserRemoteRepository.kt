package com.blameo.chatsdk.repositories.remote

import com.blameo.chatsdk.models.bodies.UsersBody

interface UserRemoteRepository {

    fun getUsersByIds(channelID: String, body: UsersBody)

}