package com.blameo.data.repositories

import android.util.Log
import com.blameo.data.di.DataScope
import com.blameo.data.models.results.GetMessageByIDResult
import com.blameo.data.net.MessageAPI
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@DataScope
class MessageRemoteRepositoryImpl @Inject constructor(private val messageAPI: MessageAPI) : MessageRemoteRepository {

    override fun getMessageById(id: String): Single<GetMessageByIDResult> {
        return messageAPI.getMessageById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Single.just(it)
            }
    }

}