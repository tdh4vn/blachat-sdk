package com.blameo.chatsdk.sources

import android.text.TextUtils
import android.util.Log
import com.blameo.chatsdk.local.LocalChannelRepository
import com.blameo.chatsdk.local.LocalMessageRepository
import com.blameo.chatsdk.local.LocalUserRepository
import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.net.APIProvider
import com.blameo.chatsdk.repositories.*
import com.blameo.chatsdk.viewmodels.ChannelListener
import com.blameo.chatsdk.viewmodels.MessageListener
import com.blameo.chatsdk.viewmodels.UserListener

interface UserRepository {
    fun getUsersByIds(ids: ArrayList<String>)
}

interface UserResultListener {
    fun onGetUsersSuccess(users: ArrayList<User>)
    fun onGetUsersFailed(error: String)
}

class UserRepositoryImpl(
    private val userListener: UserListener,
    private val localUserRepository: LocalUserRepository
) : UserRepository, UserResultListener {

    var userRemoteRepository: UserRemoteRepositoryImpl =
        UserRemoteRepositoryImpl(APIProvider.userAPI, this)
    private val TAG = "MESS_REPO"
    private var localUsers: ArrayList<User> = arrayListOf()

    override fun getUsersByIds(ids: ArrayList<String>) {
        userRemoteRepository.getUsersByIds(UsersBody(ids))
        localUsers = localUserRepository.getUsersByIds(ids)
    }

    override fun onGetUsersSuccess(users: ArrayList<User>) {
        Log.i(TAG, "size: ${users.size} + ${localUsers.size}")
        userListener.onUsersByIdsSuccess(users)
        if(users.size > 0)
        users.forEach { localUserRepository.addLocalUser(it) }
    }

    override fun onGetUsersFailed(error: String) {
        userListener.onGetUsersByIdsError(error)
    }

}