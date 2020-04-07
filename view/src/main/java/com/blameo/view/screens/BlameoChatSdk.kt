package com.blameo.view.screens

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.blameo.data.di.DaggerDataComponent
import com.blameo.data.di.DataComponent
import com.blameo.data.local.LocalChannelRepository
import com.blameo.data.local.LocalChannelRepositoryImpl
import com.blameo.data.models.pojos.Channel
import com.blameo.data.net.APIProvider
import com.blameo.domain.di.DaggerDomainComponent
import com.blameo.domain.di.DomainComponent
import com.blameo.view.di.AppComponent
import com.blameo.view.di.DaggerAppComponent
import com.blameo.view.viewmodels.ChannelViewModel
import com.blameo.view.viewmodels.MessageViewModel

private var shareInstance: BlameoChatSdk? = null

class BlameoChatSdk {

    lateinit var appComponent: AppComponent
    private lateinit var domainComponent: DomainComponent
    private lateinit var dataComponent: DataComponent
    lateinit var application: Application

    lateinit var channelViewModel: ChannelViewModel
    lateinit var messageViewModel: MessageViewModel
    private var count = 0
    private val TAG = "BlameoChat"

    lateinit var getChannelListener: GetChannelListener

    lateinit var localChannels: LocalChannelRepository
    private var channels = arrayListOf<Channel>()

    private val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsIjoiJGNoYXQ6ZTk3Y2" +
            "FkMTktYjFhNC00MzY5LTljNDctMzhjODhkMjc2MGFhIiwiY2xpZW50IjoiZTk3Y2FkMTktYjFhNC00MzY5LTljNDctMzhjODhk" +
            "Mjc2MGFhIiwiZXhwIjoxNTg3OTU4ODAyLCJzdWIiOiJlOTdjYWQxOS1iMWE0LTQzNjktOWM0Ny0zOGM4OGQyNzYwYWEiLCJ1c2VyS" +
            "WQiOiJlOTdjYWQxOS1iMWE0LT" +
            "QzNjktOWM0Ny0zOGM4OGQyNzYwYWEifQ.MUpR3vyhypT-_a3qTyUZAiB1WoNXxbhRW8wu2YMFkuk"

    companion object {
        fun getInstance(): BlameoChatSdk {
            if (shareInstance == null)
                shareInstance = BlameoChatSdk()
            return shareInstance!!
        }
    }


    fun swapApplication(application: Application){
        this.application = application
        initDI()

    }

    private fun initDB(activity: AppCompatActivity) {
        localChannels = LocalChannelRepositoryImpl(activity)
    }

    private fun initDI(){

        dataComponent = DaggerDataComponent.builder()
            .context(application)
            .build()

        domainComponent = DaggerDomainComponent.builder()
            .dataComponent(dataComponent)
            .build()

        appComponent = DaggerAppComponent.builder()
            .appInstance(application)
            .domainComponent(domainComponent)
            .build()
    }


    fun initViewModel(activity: AppCompatActivity) : ChannelViewModel{
        channelViewModel = ViewModelProviders.of(activity).get(ChannelViewModel::class.java)
        messageViewModel = ViewModelProviders.of(activity).get(MessageViewModel::class.java)
        APIProvider.setSession(token)
        injectViewModels()
        initDB(activity)
        return channelViewModel
    }

    private fun injectViewModels(){
        appComponent.inject(channelViewModel)
        appComponent.inject(messageViewModel)
    }

    fun getChannels(channelListener: GetChannelListener){
        getChannelListener = channelListener
        observeMessage()
        channelViewModel.getChannelsRemote()
        channelViewModel.channelsRemote.observeForever {
            if(it.size > 0){
                channels = it
//                getChannelListener.onSuccess(it)
                it.forEach { channel ->
                    if(!TextUtils.isEmpty(channel.last_message_id))
                        messageViewModel.getMessageByIdRemote(channel.last_message_id)
                    localChannels.addLocalChannel(channel)
                }
            }
        }
    }

    fun exportDB(){
        localChannels.exportChannelDB()
    }

    private fun observeMessage(){
        messageViewModel.messageRemote.observeForever {
            if(it != null){
                channels.forEach { channel ->
                    if(channel.id == it.channel_id){
                        channel.last_message = it
                            getChannelListener.onSuccess(channels)
                        return@forEach
                    }
                }
            }
        }
    }

    interface GetChannelListener{
        fun onChannelChanged(channel: Channel)
        fun onSuccess(channels: ArrayList<Channel>)
    }

}
