package com.blameo.chatsdk.controllers

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blameo.chatsdk.ChatListener
import com.blameo.chatsdk.blachat.BlaChatSDK
import com.blameo.chatsdk.blachat.Callback
import com.blameo.chatsdk.models.bla.BlaChannel
import com.blameo.chatsdk.models.bla.BlaChannelType
import com.blameo.chatsdk.models.bla.BlaMessage
import com.blameo.chatsdk.models.bla.BlaUser
import com.blameo.chatsdk.models.entities.Channel
import com.blameo.chatsdk.models.entities.User
import com.blameo.chatsdk.utils.DateFormatUtils
import com.blameo.chatsdk.utils.UserSP
import java.lang.Exception

class ConversationViewModel(val channel: BlaChannel) {

    var last_message : MutableLiveData<String> = MutableLiveData()
    var channel_name: MutableLiveData<String> = MutableLiveData()
    var channel_updated: MutableLiveData<String> = MutableLiveData()
    var channel_avatar: MutableLiveData<String> = MutableLiveData()
    var memmbers : List<BlaUser>? = null
    var chatSdk: BlaChatSDK = BlaChatSDK.getInstance()
    var statusUser: MutableLiveData<Boolean> = MutableLiveData()
    var partnerId: MutableLiveData<String> = MutableLiveData()

    val TAG = "CVM"

    var errorStream = MutableLiveData<String>()

    init {
        if(!TextUtils.isEmpty(channel.name))
            channel_name.value = channel.name

        if(!TextUtils.isEmpty(channel.avatar))
            channel_avatar.value = channel.avatar

        channel_updated.value = DateFormatUtils.getInstance().getNewDateFormat(channel.updatedAt)

        last_message.value = if(channel.lastMessage == null){ "" }
             else{ channel.lastMessage.content }

        if(channel.type == BlaChannelType.DIRECT.value)
            getUsersInChannel()
    }


    fun updateNewMessage(message: BlaMessage){
        last_message.postValue(message.content)
        channel_updated.postValue(DateFormatUtils.getInstance().getNewDateFormat(message.createdAt))
    }

    private fun getUsersInChannel(){

        chatSdk.getUsersInChannel(channel.id, object : Callback<List<BlaUser>>{
            override fun onSuccess(users: List<BlaUser>?) {
                memmbers = users
                users?.forEach {
                    if(UserSP.getInstance().id != it.id){
                        partnerId.postValue(it.id)
                        Log.i(TAG, ""+it.avatar + " "+it.name)
                        channel_avatar.postValue(it.avatar)
                        channel_name.postValue(it.name)
                        return@forEach
                    }
               }
            }

            override fun onFail(e: Exception?) {

            }
        })
    }
}