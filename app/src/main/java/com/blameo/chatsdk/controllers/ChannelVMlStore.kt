package com.blameo.chatsdk.controllers

import com.blameo.chatsdk.models.pojos.Channel


class ChannelVMlStore private constructor(){

    private var channelVMMap: HashMap<String, ConversationViewModel>? = null

    init {
        if (channelVMMap == null)
           channelVMMap = HashMap()
    }

    private object Holder { val INSTANCE = ChannelVMlStore() }

    companion object {
        @JvmStatic
        fun getInstance(): ChannelVMlStore{
            return Holder.INSTANCE
        }
    }

    fun getChannelViewModel(channel: Channel): ConversationViewModel {
        var vm = channelVMMap?.get(channel.id)
        if (vm != null) return vm
        vm = ConversationViewModel(channel)
        channelVMMap?.set(channel.id, vm)
        return vm
    }
}