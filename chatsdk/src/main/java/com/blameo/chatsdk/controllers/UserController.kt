package com.blameo.chatsdk.controllers

import android.util.Log
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.repositories.local.LocalUserRepository
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.repositories.UserRepository
import com.blameo.chatsdk.repositories.UserRepositoryImpl

interface UserListener {
    fun onUsersByIdsSuccess(channelId: String, users: ArrayList<User>)
    fun onGetUsersByIdsError(error: String)
    fun onGetAllMembersSuccess(users: ArrayList<User>)
    fun onGetAllMembersError(error: String)
}

class UserController(
    private val userListener: UserListener,
    private val localUserRepository: LocalUserRepository
) : UserListener {

    private val TAG = "USER_VM"

    private var userRepository: UserRepository =
        UserRepositoryImpl(
            this
        )

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

    fun getLocalUsers(userIds: ArrayList<String>) : ArrayList<User>{
        return localUserRepository.getUsersByIds(userIds)
    }

    fun getLocalUsersByIds(channelId: String, ids: ArrayList<String>) : ArrayList<User>{
        return userRepository.getLocalUsersByIds(channelId, ids)
    }

    fun getAllMembers(){
        userRepository.getAllMembers()
    }

    fun updateUserLastSeenInChannel(userId: String, channelId: String, lastMessage: Message){
        userRepository.updateUserLastSeenInChannel(userId, channelId, lastMessage)
    }

    override fun onUsersByIdsSuccess(channelId: String, users: ArrayList<User>) {
        Log.e(TAG, "get users by ids - REMOTE success: ${users.size}")
        userListener.onUsersByIdsSuccess(channelId, users)
    }

    override fun onGetUsersByIdsError(error: String) {
        userListener.onGetUsersByIdsError(error)
    }

    override fun onGetAllMembersSuccess(users: ArrayList<User>) {
        userListener.onGetAllMembersSuccess(users)
    }

    override fun onGetAllMembersError(error: String) {
        userListener.onGetAllMembersError(error)
    }

}