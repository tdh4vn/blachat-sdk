package com.blameo.chatsdk.controllers

import androidx.lifecycle.MutableLiveData
import com.blameo.chatsdk.models.results.UserStatus

class UserViewModel(val user: UserStatus) {

    var status : MutableLiveData<Boolean> = MutableLiveData()

    val TAG = "UVM"

    var errorStream = MutableLiveData<String>()

    init {
        status.postValue(user.status == 2)
    }

    fun updateStatus(isOnline: Boolean){
        status.postValue(isOnline)
    }
}