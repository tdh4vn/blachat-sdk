package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.pojos.Message

interface MessageRepository {
    fun getMessages(channelId: String, lastMessageId: String)
    fun getMessageByIdLocal(id: String): Message
    fun createMessage(body: CreateMessageBody)
    fun sendSeenMessageEvent(channelId: String, messageId: String, authorId: String)
    fun sendReceivedMessageEvent(channelId: String, messageId: String, authorId: String)
    fun receiveEventNewMessage(message: Message)
    fun receiveEventSeenMessage(messageId: String)
    fun receiveEventReceiveMessage(messageId: String)
    fun syncUnsentMessage()
}