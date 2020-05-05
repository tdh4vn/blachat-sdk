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

class UserViewModel(val user: UserStatus) {

    var status : MutableLiveData<Boolean> = MutableLiveData()
    var count = 0


    val TAG = "UVM"

    var errorStream = MutableLiveData<String>()

    init {
        status.value = user.status == 2
    }

    fun updateStatus(s: Int){
        status.value = s == 2
//        count++
    }
}