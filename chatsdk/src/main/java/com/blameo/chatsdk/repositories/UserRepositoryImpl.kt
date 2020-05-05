package com.blameo.chatsdk.repositories

import android.util.Log
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.repositories.local.LocalUserRepository
import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.repositories.remote.net.APIProvider
import com.blameo.chatsdk.repositories.remote.UserRemoteRepositoryImpl
import com.blameo.chatsdk.controllers.UserListener
import com.blameo.chatsdk.models.bodies.PostIDsBody
import com.blameo.chatsdk.models.bodies.UpdateStatusBody
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.models.results.UserStatus
import com.blameo.chatsdk.repositories.local.LocalUserInChannelRepository
import com.blameo.chatsdk.repositories.local.LocalUserInChannelRepositoryImpl
import com.blameo.chatsdk.repositories.local.LocalUserRepositoryImpl

interface UserRepository {
    fun getUsersByIds(channelId: String, ids: ArrayList<String>)
    fun getLocalUsersByIds(channelId: String, ids: ArrayList<String>) : ArrayList<User>
    fun getAllMembers()
    fun updateUserLastSeenInChannel(userId: String, channelId: String, lastMessage: Message)
    fun getUsersStatus()
    fun updateStatus()
}

interface UserResultListener {
    fun onGetUsersSuccess(channelId: String, users: ArrayList<User>)
    fun onGetUsersFailed(error: String)
    fun onGetAllMembersSuccess(users: ArrayList<User>)
    fun onGetMembersFailed(error: String)
    fun onGetUsersStatusSuccess(data: ArrayList<UserStatus>)
}

class UserRepositoryImpl(
    private val userListener: UserListener
) : UserRepository,
    UserResultListener {

    var userRemoteRepository: UserRemoteRepositoryImpl =
        UserRemoteRepositoryImpl(
            APIProvider.userAPI, APIProvider.presenceAPI,
            this
        )
    private val localUserRepository: LocalUserRepository = LocalUserRepositoryImpl(BlameoChatSdk.getInstance().context)
    private val localUIC: LocalUserInChannelRepository = LocalUserInChannelRepositoryImpl(BlameoChatSdk.getInstance().context)
    private val userStatusMap: HashMap<String, UserStatus> = hashMapOf()

    private val TAG = "USER_REPO"
    private var localUsers: ArrayList<User> = arrayListOf()

    override fun getUsersByIds(channelId: String, ids: ArrayList<String>) {

        val idsNotAvailable = arrayListOf<String>()
        localUsers = arrayListOf()
        ids.forEach {
            val user = localUserRepository.getUserByID(it)
            if( user != null)
                localUsers.add(user)
            else
                idsNotAvailable.add(it)
        }

        if(idsNotAvailable.size > 0)
            userRemoteRepository.getUsersByIds("", UsersBody(idsNotAvailable))
        else
            userListener.onUsersByIdsSuccess("", localUsers)
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

    override fun getUsersStatus() {
        val users = localUserRepository.allUsers
        var userIds = ""
        users.forEach {
            userIds+="${it.id},"
        }
        val body = PostIDsBody()
        body.ids = userIds
        userRemoteRepository.getUsersStatus(body)
    }

    override fun updateStatus() {
        userRemoteRepository.updateStatus(UpdateStatusBody(BlameoChatSdk.getInstance().uId, "2"))
    }

    override fun onGetUsersSuccess(channelId: String, users: ArrayList<User>) {
        Log.i(TAG, "size: ${users.size} + ${localUsers.size} channelId: $channelId")
        localUsers.addAll(users)
        userListener.onUsersByIdsSuccess(channelId, localUsers)
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
//        userListener.onGetAllMembersError(error)
    }

    override fun onGetUsersStatusSuccess(data: ArrayList<UserStatus>) {
        Log.e(TAG, "get update status success ${data.size}")

        data.forEach {

            if(userStatusMap[it.id] != null){

                val userStatus = userStatusMap[it.id]
                if(userStatus?.status != it.status){
                    userStatusMap[it.id] = it
                    userListener.onUserStatusChanged(it)
                }
            }else
            {
                userStatusMap[it.id] = it
                userListener.onUserStatusChanged(it)
            }
        }
    }



}