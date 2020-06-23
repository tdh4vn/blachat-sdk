package com.blameo.chatsdk.controllers

import androidx.lifecycle.MutableLiveData
import com.blameo.chatsdk.models.bla.BlaChannel


class ChannelVMlStore private constructor(){

    private var channelVMMap: HashMap<String, ConversationViewModel>? = null

    init {
        if (channelVMMap == null)
           channelVMMap = HashMap()
    }

    var newChannel: MutableLiveData<BlaChannel>  = MutableLiveData()

    private object Holder { val INSTANCE = ChannelVMlStore() }

    companion object {
        @JvmStatic
        fun getInstance(): ChannelVMlStore{
            return Holder.INSTANCE
        }
    }

    fun getChannelViewModel(channel: BlaChannel): ConversationViewModel {
        var vm = channelVMMap?.get(channel.id)
        if (vm != null) return vm
        vm = ConversationViewModel(channel)
        channelVMMap?.set(channel.id, vm)
        return vm
    }

    fun getChannelByID(id: String): ConversationViewModel{
        return channelVMMap?.get(id)!!
    }

    fun addNewChannel(channel: BlaChannel){
        val vm = channelVMMap?.get(channel.id)
        if(vm == null){
            newChannel.postValue(channel)
        }
        getChannelViewModel(channel)
    }
}