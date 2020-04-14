package com.blameo.chatsdk.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.results.CreateChannelResult
import com.blameo.chatsdk.models.results.GetChannelResult
import com.blameo.chatsdk.models.results.GetUsersInChannelResult
import com.blameo.chatsdk.net.APIProvider
import com.blameo.chatsdk.repositories.ChannelRemoteRepository
import com.blameo.chatsdk.repositories.ChannelRemoteRepositoryImpl
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

private var shareInstance: ChannelViewModel? = null

class ChannelViewModel {

    companion object {
        fun getInstance(): ChannelViewModel {
            if (shareInstance == null)
                shareInstance = ChannelViewModel()
            return shareInstance!!
        }
    }

    var channelRepository: ChannelRemoteRepository =
        ChannelRemoteRepositoryImpl(APIProvider.userAPI)

    var channelsRemote = MutableLiveData<ArrayList<Channel>>()
    var usersInChannel = MutableLiveData<ArrayList<String>>()
    var createChannel = MutableLiveData<Channel>()

    var errorStream = MutableLiveData<String>()
    private val TAG = "CHANNEL_VM"

    private var getChannelsRemoteListener = object : SingleObserver<GetChannelResult> {
        override fun onSuccess(t: GetChannelResult) {
            channelsRemote.value = t.data
            Log.e("cccc", "s: ${t.data.size}")
        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onError(e: Throwable) {
            errorStream.value = e.message
            Log.e("aaa", "" + e.message + e.cause + e.stackTrace)
        }
    }

    private var getUsersInChannelListener = object : SingleObserver<GetUsersInChannelResult> {
        override fun onSuccess(t: GetUsersInChannelResult) {
            usersInChannel.value = t.data
        }

        override fun onSubscribe(d: Disposable) {
        }

        override fun onError(e: Throwable) {
        }

    }

    private var createChannelListener = object : SingleObserver<CreateChannelResult> {
        override fun onSuccess(t: CreateChannelResult) {
            createChannel.value = t.data
            Log.e(TAG, "ok ${t.data.id}")
        }

        override fun onSubscribe(d: Disposable) {
        }

        override fun onError(e: Throwable) {
            Log.e(TAG, "" + e.message + e.cause + e.stackTrace.toString())
        }
    }

    fun getChannelsRemote() {
        Log.e("c", "abc")
        channelRepository.getChannels().subscribe(getChannelsRemoteListener)
    }

    fun getUsersInChannel(channelId: String) {
        channelRepository.getUsersInChannel(channelId).subscribe(getUsersInChannelListener)
    }

    fun createChannel(ids: ArrayList<String>, name: String, type: Int) {
        val body = CreateChannelBody(ids, name, type)
        channelRepository.createChannel(body).subscribe(createChannelListener)
    }
}