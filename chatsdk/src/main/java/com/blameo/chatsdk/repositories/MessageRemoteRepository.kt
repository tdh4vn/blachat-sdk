package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.results.GetMessageByIDResult
import com.blameo.chatsdk.models.results.GetMessagesResult
import io.reactivex.Single

interface MessageRemoteRepository {

    fun getMessageById(id: String): Single<GetMessageByIDResult>

    fun createMessage(body: CreateMessageBody): Single<GetMessageByIDResult>

    fun getMessagesRemote(channelId: String, lastId: String): Single<GetMessagesResult>

}