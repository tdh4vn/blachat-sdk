package com.blameo.chatsdk.repositories.remote

import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.pojos.Message

interface MessageRemoteRepository {

    fun createMessage(temID: String, body: CreateMessageBody, localMessage: Message)

    fun resentMessage(message: Message, onSuccess: (m: Message) -> Unit, onFailed: (t: Throwable) -> Unit)

    fun getMessages(channelId: String, lastId: String)

    fun getMessageBeforeAMessage(channelId: String, messageId: String)

    fun sendSeenMessageEvent(channelId: String, messageId: String, authorId: String)

    fun sendReceivedMessageEvent(channelId: String, messageId: String, authorId: String)

}