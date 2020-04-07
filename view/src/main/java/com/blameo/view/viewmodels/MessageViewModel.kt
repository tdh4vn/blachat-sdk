package com.blameo.view.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.blameo.data.models.pojos.Message
import com.blameo.data.models.results.GetMessageByIDResult
import com.blameo.domain.usecases.channels.GetMessageByIdUseCase
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MessageViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var getMessageByIdUseCase: GetMessageByIdUseCase

    private val TAG = "MESSAGE_VM"

    var messageRemote = MutableLiveData<Message>()

    var errorStream = MutableLiveData<String>()

    private var getMessageByIdRemoteListener = object : SingleObserver<GetMessageByIDResult> {
        override fun onSuccess(t: GetMessageByIDResult) {
            messageRemote.value = t.message
            Log.e(TAG,"s: ${t.message.content}")
        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onError(e: Throwable) {
            errorStream.value = e.message
            Log.e(TAG, ""+e.message + e.cause + e.stackTrace.toString())
        }
    }

    fun getMessageByIdRemote(id: String){
        Log.e(TAG, "get by id: $id")
        getMessageByIdUseCase.execute(id).subscribe(getMessageByIdRemoteListener)
    }
}