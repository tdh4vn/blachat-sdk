package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.results.GetMessageByIDResult
import com.blameo.chatsdk.models.results.GetMessagesResult
import com.blameo.chatsdk.net.MessageAPI
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MessageRemoteRepositoryImpl constructor(private val messageAPI: MessageAPI) :
    MessageRemoteRepository {

    override fun getMessageById(id: String): Single<GetMessageByIDResult> {
        return messageAPI.getMessageById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Single.just(it)
            }
    }

    override fun createMessage(body: CreateMessageBody): Single<GetMessageByIDResult> {
        return messageAPI.createMessage(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Single.just(it)
            }
    }

    override fun getMessagesRemote(channelId: String, lastId: String): Single<GetMessagesResult> {
        return messageAPI.getMessagesInChannel(channelId, lastId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Single.just(it)
            }
    }

}