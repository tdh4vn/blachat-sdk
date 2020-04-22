package com.blameo.chatsdk.repositories.remote

import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.models.results.GetMessageByIDResult
import com.blameo.chatsdk.models.results.GetMessagesResult
import io.reactivex.Single

interface MessageRemoteRepository {

    fun getMessageById(id: String)

    fun createMessage(temID: String, body: CreateMessageBody)

    fun resentMessage(message: Message, onSuccess: (m: Message) -> Unit, onFailed: (t: Throwable) -> Unit)

    fun getMessages(channelId: String, lastId: String)

    fun sendSeenMessageEvent(channelId: String, messageId: String, authorId: String)

    fun sendReceivedMessageEvent(channelId: String, messageId: String, authorId: String)

}