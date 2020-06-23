package com.blameo.chatsdk.controllers

import com.blameo.chatsdk.models.results.UserStatus


class UserVMStore private constructor(){

    private var userVMMap: HashMap<String, UserViewModel>? = null

    init {
        if (userVMMap == null)
           userVMMap = HashMap()
    }

    private object Holder { val INSTANCE = UserVMStore() }

    companion object {
        @JvmStatic
        fun getInstance(): UserVMStore{
            return Holder.INSTANCE
        }
    }

    fun getUserViewModel(user: UserStatus): UserViewModel {
        var vm = userVMMap?.get(user.id)
        if (vm != null) return vm
        vm = UserViewModel(user)
        userVMMap?.set(user.id, vm)
        return vm
    }

}