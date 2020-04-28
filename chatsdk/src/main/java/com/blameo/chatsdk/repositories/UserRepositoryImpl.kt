package com.blameo.chatsdk.repositories

import android.util.Log
import com.blameo.chatsdk.repositories.local.LocalUserRepository
import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.repositories.remote.net.APIProvider
import com.blameo.chatsdk.repositories.remote.UserRemoteRepositoryImpl
import com.blameo.chatsdk.controllers.UserListener

interface UserRepository {
    fun getUsersByIds(channelId: String, ids: ArrayList<String>)
}

interface UserResultListener {
    fun onGetUsersSuccess(channelId: String, users: ArrayList<User>)
    fun onGetUsersFailed(error: String)
}

class UserRepositoryImpl(
    private val userListener: UserListener,
    private val localUserRepository: LocalUserRepository
) : UserRepository,
    UserResultListener {

    var userRemoteRepository: UserRemoteRepositoryImpl =
        UserRemoteRepositoryImpl(
            APIProvider.userAPI,
            this
        )
    private val TAG = "MESS_REPO"
    private var localUsers: ArrayList<User> = arrayListOf()

    override fun getUsersByIds(channelId: String, ids: ArrayList<String>) {
        userRemoteRepository.getUsersByIds(channelId, UsersBody(ids))
        localUsers = localUserRepository.getUsersByIds(ids)
    }

    override fun onGetUsersSuccess(channelId: String, users: ArrayList<User>) {
        Log.i(TAG, "size: ${users.size} + ${localUsers.size}")
        userListener.onUsersByIdsSuccess(channelId, users)
        if(users.size > 0)
        users.forEach { localUserRepository.addLocalUser(it) }
    }

    override fun onGetUsersFailed(error: String) {
        userListener.onGetUsersByIdsError(error)
    }

}