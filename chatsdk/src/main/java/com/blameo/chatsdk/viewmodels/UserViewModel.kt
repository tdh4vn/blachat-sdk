package com.blameo.chatsdk.viewmodels

import android.util.Log
import com.blameo.chatsdk.local.LocalUserRepository
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.sources.UserRepository
import com.blameo.chatsdk.sources.UserRepositoryImpl

interface UserListener {
    fun onUsersByIdsSuccess(channelId: String, users: ArrayList<User>)
    fun onGetUsersByIdsError(error: String)
}

class UserViewModel(
    private val userListener: UserListener,
    private val localUserRepository: LocalUserRepository
) : UserListener {

    private val TAG = "USER_VM"

    private var userRepository: UserRepository = UserRepositoryImpl(this, localUserRepository)

    fun getUsersByIds(channelId: String, ids: ArrayList<String>) {
//        Log.e(TAG, "users by ids: ${ids.size}")
        val users = localUserRepository.getUsersByIds(ids)
        if(users.size == 0) {
            Log.e(TAG, "users by ids - REMOTE: ${ids.size}")
            userRepository.getUsersByIds(channelId, ids)
        }else{
            Log.e(TAG, "users by ids - LOCAL: ${ids.size}")
            userListener.onUsersByIdsSuccess(channelId, users)
        }
    }

    override fun onUsersByIdsSuccess(channelId: String, users: ArrayList<User>) {
        Log.e(TAG, "get users by ids - REMOTE success: ${users.size}")
        userListener.onUsersByIdsSuccess(channelId, users)
    }

    override fun onGetUsersByIdsError(error: String) {
        userListener.onGetUsersByIdsError(error)
    }
}