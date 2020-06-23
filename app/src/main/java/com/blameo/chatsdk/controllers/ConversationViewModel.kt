package com.blameo.chatsdk.controllers

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blameo.chatsdk.blachat.BlaChatSDK
import com.blameo.chatsdk.blachat.Callback
import com.blameo.chatsdk.models.bla.BlaChannel
import com.blameo.chatsdk.models.bla.BlaChannelType
import com.blameo.chatsdk.models.bla.BlaMessage
import com.blameo.chatsdk.models.bla.BlaUser
import com.blameo.chatsdk.utils.DateFormatUtils
import com.blameo.chatsdk.utils.UserSP

class ConversationViewModel(val channel: BlaChannel) {

    var last_message : MutableLiveData<String> = MutableLiveData()
    var channel_name: MutableLiveData<String> = MutableLiveData()
    var channel_updated: MutableLiveData<String> = MutableLiveData()
    var channel_avatar: MutableLiveData<String> = MutableLiveData()
    var memmbers : List<BlaUser>? = null
    var chatSdk: BlaChatSDK = BlaChatSDK.getInstance()
    var userSeenMessage: MutableLiveData<Boolean> = MutableLiveData()
    var partnerId: MutableLiveData<String> = MutableLiveData()

    val TAG = "CVM"

    init {
        if(!TextUtils.isEmpty(channel.name))
            channel_name.value = channel.name

        if(!TextUtils.isEmpty(channel.avatar))
            channel_avatar.value = channel.avatar

        channel_updated.value = DateFormatUtils.getInstance().getNewDateFormat(channel.updatedAt)

        Log.i(TAG, "last: "+channel.lastMessage)

        last_message.value = if(channel.lastMessage == null){ "" }
             else{ channel.lastMessage.content }

        if(channel.type == BlaChannelType.DIRECT.value)
            getUsersInChannel()

        userSeenMessage.value = userHaveSeenMessage()
    }


    fun updateNewMessage(message: BlaMessage, seen: Boolean){
        last_message.postValue(message.content)
        channel_updated.postValue(DateFormatUtils.getInstance().getNewDateFormat(message.createdAt))
        userSeenMessage.postValue(seen)
    }

    fun markUserHaveSeenMessage(){
        userSeenMessage.postValue(true)
    }

    fun updateChannel(avatar: String, name: String){
        channel_avatar.postValue(avatar)
        channel_name.postValue(name)
    }

    private fun userHaveSeenMessage(): Boolean {
        if(channel.lastMessage == null) return false
        for (user in channel.lastMessage.seenBy) {
            Log.i(TAG, ""+user.id)
            if (user.id == UserSP.getInstance().id) return true
        }
        return false
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