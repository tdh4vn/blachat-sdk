package com.blameo.chatsdk.repositories

import android.util.Log
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.repositories.local.LocalUserRepository
import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.repositories.remote.net.APIProvider
import com.blameo.chatsdk.repositories.remote.UserRemoteRepositoryImpl
import com.blameo.chatsdk.controllers.UserListener
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.repositories.local.LocalUserInChannelRepository
import com.blameo.chatsdk.repositories.local.LocalUserInChannelRepositoryImpl

interface UserRepository {
    fun getUsersByIds(channelId: String, ids: ArrayList<String>)
    fun getLocalUsersByIds(channelId: String, ids: ArrayList<String>) : ArrayList<User>
    fun getAllMembers()
    fun updateUserLastSeenInChannel(userId: String, channelId: String, lastMessage: Message)
}

interface UserResultListener {
    fun onGetUsersSuccess(channelId: String, users: ArrayList<User>)
    fun onGetUsersFailed(error: String)
    fun onGetAllMembersSuccess(users: ArrayList<User>)
    fun onGetMembersFailed(error: String)
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

    private val localUIC: LocalUserInChannelRepository
            = LocalUserInChannelRepositoryImpl(BlameoChatSdk.getInstance().context)
    private val TAG = "USER_REPO"
    private var localUsers: ArrayList<User> = arrayListOf()

    override fun getUsersByIds(channelId: String, ids: ArrayList<String>) {

        localUsers = localUserRepository.getUsersByIds(ids)
        if(localUsers.size == 0){
            userRemoteRepository.getUsersByIds(channelId, UsersBody(ids))
        }else
            onGetUsersSuccess(channelId, localUsers)
    }

    override fun getLocalUsersByIds(channelId: String, ids: ArrayList<String>): ArrayList<User> {
        return localUserRepository.getUsersByIds(ids)
    }

    override fun getAllMembers() {
        userRemoteRepository.getAllMembers()
    }

    override fun updateUserLastSeenInChannel(userId: String, channelId: String, lastMessage: Message) {
        localUIC.updateUserLastSeenInChannel(userId, channelId, lastMessage)
    }

    override fun onGetUsersSuccess(channelId: String, users: ArrayList<User>) {
        Log.i(TAG, "size: ${users.size} + ${localUsers.size}")
        userListener.onUsersByIdsSuccess(channelId, users)
        if(users.size > 0)
        users.forEach { localUserRepository.addLocalUser(it) }
    }

    override fun onGetUsersFailed(error: String) {
        Log.i(TAG, "error: $error")
        userListener.onGetUsersByIdsError(error)
    }

    override fun onGetAllMembersSuccess(users: ArrayList<User>) {
        userListener.onGetAllMembersSuccess(users)
        if(users.size > 0)
            users.forEach { localUserRepository.addLocalUser(it) }
    }

    override fun onGetMembersFailed(error: String) {
        userListener.onGetAllMembersSuccess(localUserRepository.allUsers)
        userListener.onGetAllMembersError(error)
    }

}