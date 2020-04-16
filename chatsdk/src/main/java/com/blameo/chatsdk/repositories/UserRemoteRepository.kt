package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.models.bodies.UsersBody

interface UserRemoteRepository {

    fun getUsersByIds(body: UsersBody)

}