package com.blameo.chatsdk.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.models.results.GetMessageByIDResult
import com.blameo.chatsdk.models.results.GetMessagesResult
import com.blameo.chatsdk.net.APIProvider
import com.blameo.chatsdk.repositories.MessageRemoteRepository
import com.blameo.chatsdk.repositories.MessageRemoteRepositoryImpl
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

private var shareInstance: MessageViewModel? = null

class MessageViewModel {

    companion object {
        fun getInstance(): MessageViewModel {
            if (shareInstance == null)
                shareInstance = MessageViewModel()
            return shareInstance!!
        }
    }

    private val TAG = "MESSAGE_VM"

    var messageRepository: MessageRemoteRepository =
        MessageRemoteRepositoryImpl(APIProvider.messageAPI)

    var messageRemote = MutableLiveData<Message>()
    var createMessage = MutableLiveData<Message>()
    var listMessages = MutableLiveData<ArrayList<Message>>()

    var errorStream = MutableLiveData<String>()

    private var getMessageByIdRemoteListener = object : SingleObserver<GetMessageByIDResult> {
        override fun onSuccess(t: GetMessageByIDResult) {
            messageRemote.value = t.message
            Log.e(TAG, "s: ${t.message.content}")
        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onError(e: Throwable) {
            errorStream.value = e.message
            Log.e(TAG, "" + e.message + e.cause + e.stackTrace.toString())
        }
    }

    private var createMessageListener = object : SingleObserver<GetMessageByIDResult> {
        override fun onSuccess(t: GetMessageByIDResult) {
            createMessage.value = t.message
        }

        override fun onSubscribe(d: Disposable) {
        }

        override fun onError(e: Throwable) {
        }

    }

    private var getMessagesListener = object : SingleObserver<GetMessagesResult> {
        override fun onSuccess(t: GetMessagesResult) {
            listMessages.value = t.data
        }

        override fun onSubscribe(d: Disposable) {
        }

        override fun onError(e: Throwable) {
        }
    }

    fun getMessageByIdRemote(id: String) {
        Log.e(TAG, "get by id: $id")
        messageRepository.getMessageById(id).subscribe(getMessageByIdRemoteListener)
    }

    fun createMessage(content: String, type: Int, channelId: String) {
        messageRepository.createMessage(CreateMessageBody(content, type, channelId))
            .subscribe(createMessageListener)
    }

    fun getMessages(channelId: String, lastId: String) {
        Log.e(TAG, "load messages in channel: $channelId with lasId = $lastId")
        messageRepository.getMessagesRemote(channelId, lastId).subscribe(getMessagesListener)
    }
}