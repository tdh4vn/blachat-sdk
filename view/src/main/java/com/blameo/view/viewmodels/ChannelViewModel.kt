package com.blameo.view.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.blameo.data.models.pojos.Channel
import com.blameo.data.models.results.GetChannelResult
import com.blameo.data.models.results.LikeResult
import com.blameo.domain.usecases.channels.GetChannelRemoteUseCase
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ChannelViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var getChannelsRemoteUseCase: GetChannelRemoteUseCase

    var channelsRemote = MutableLiveData<ArrayList<Channel>>()
    var channelRemote = MutableLiveData<Channel>()

    var errorStream = MutableLiveData<String>()

    private var getChannelsRemoteListener = object : SingleObserver<GetChannelResult> {
        override fun onSuccess(t: GetChannelResult) {
            channelsRemote.value = t.data
            Log.e("cccc","s: ${t.data.size}")
        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onError(e: Throwable) {
            errorStream.value = e.message
            Log.e("aaa", ""+e.message + e.cause + e.stackTrace)
        }
    }

    private var channelRemoteListener = object : SingleObserver<Channel> {
        override fun onSuccess(t: Channel) {
            channelRemote.value = t
            Log.e("cccc","s: ${t.id}")
        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onError(e: Throwable) {
            errorStream.value = e.message
            Log.e("aaa", ""+e.message + e.cause + e.stackTrace)
        }
    }

    fun getChannelsRemote(){
        Log.e("c", "abc")
        getChannelsRemoteUseCase.execute().subscribe(getChannelsRemoteListener)
    }
}