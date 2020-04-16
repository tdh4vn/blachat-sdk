package com.blameo.chatsdk.viewmodels

import android.util.Log
import com.blameo.chatsdk.local.LocalUserRepository
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.sources.UserRepository
import com.blameo.chatsdk.sources.UserRepositoryImpl

interface UserListener {
    fun onUsersByIdsSuccess(users: ArrayList<User>)
    fun onGetUsersByIdsError(error: String)
}

class UserViewModel(
    private val userListener: UserListener,
    private val localUserRepository: LocalUserRepository
) : UserListener {

    private val TAG = "USER_VM"

    private var userRepository: UserRepository = UserRepositoryImpl(this, localUserRepository)

    fun getUsersByIds(ids: ArrayList<String>) {
        Log.e(TAG, "users by ids: ${ids.size}")
        userRepository.getUsersByIds(ids)
    }

    override fun onUsersByIdsSuccess(users: ArrayList<User>) {
        userListener.onUsersByIdsSuccess(users)
    }

    override fun onGetUsersByIdsError(error: String) {
        userListener.onGetUsersByIdsError(error)
    }
}