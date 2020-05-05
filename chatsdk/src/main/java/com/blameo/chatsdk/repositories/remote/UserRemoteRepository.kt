package com.blameo.chatsdk.repositories.remote

import com.blameo.chatsdk.models.bodies.PostIDsBody
import com.blameo.chatsdk.models.bodies.UpdateStatusBody
import com.blameo.chatsdk.models.bodies.UsersBody

interface UserRemoteRepository {

    fun getUsersByIds(channelID: String, body: UsersBody)

    fun getAllMembers()

    fun getUsersStatus(body: PostIDsBody)

    fun updateStatus(body: UpdateStatusBody)

}