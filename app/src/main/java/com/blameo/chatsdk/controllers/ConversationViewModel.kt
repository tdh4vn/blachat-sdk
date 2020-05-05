package com.blameo.chatsdk.controllers

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.ChatListener
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.models.results.UserStatus
import com.blameo.chatsdk.utils.DateFormatUtils

class ConversationViewModel(val channel: Channel) {

    var last_message : MutableLiveData<String> = MutableLiveData()
    var channel_name: MutableLiveData<String> = MutableLiveData()
    var channel_updated: MutableLiveData<String> = MutableLiveData()
    var channel_avatar: MutableLiveData<String> = MutableLiveData()
    var users : MutableLiveData<ArrayList<User>> = MutableLiveData()
    var chatSdk: BlameoChatSdk = BlameoChatSdk.getInstance()
    var statusUser: MutableLiveData<Boolean> = MutableLiveData()
    var partnerId: MutableLiveData<String> = MutableLiveData()

    val TAG = "CVM"

    var errorStream = MutableLiveData<String>()

    init {
        if(!TextUtils.isEmpty(channel.name))
            channel_name.value = channel.name

        if(!TextUtils.isEmpty(channel.avatar))
            channel_avatar.value = channel.avatar

        channel_updated.value = DateFormatUtils.getInstance().getTime(channel.createdAtString)

        last_message.value = if(channel.lastMessage == null)
            "" else channel.lastMessage.content
        getUsersInChannel()
    }

    private fun getUsersInChannel(){

        chatSdk.getUsersInChannel(channel.id, object : ChatListener.GetUsersInChannelListener{
            override fun onGetUsersByIdsSuccess(channelId: String, users: ArrayList<User>) {
//                Log.i(TAG, "channel ${channel.id} has total ${users.size} users")
                this@ConversationViewModel.users.value = users
                users.forEach {
                    if(chatSdk.uId != it.id){
                        partnerId.value = it.id
//                        channel_avatar.value = it.avatar
//                        channel_name.value = it.name
                        return@forEach
                    }
               }
            }
        })
    }
}