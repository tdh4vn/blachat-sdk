package com.blameo.chatsdk.controllers

import android.text.TextUtils
import android.util.Log
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.repositories.MessageRepository
import com.blameo.chatsdk.repositories.MessageRepositoryImpl

interface MessageListener {
    fun onGetMessagesSuccess(messages: ArrayList<Message>)
    fun onGetMessagesError(error: String)
    fun onGetMessageByIdSuccess(message: Message)
    fun onCreateMessageSuccess(message: Message)
    fun onMarkSeenMessageSuccess(id: String)
    fun onMarkSeenMessageFail(error: String)
    fun onMarkReceiveMessageSuccess()
    fun onMarkReceiveMessageFail(error: String)
}

class MessageController(private val listener: MessageListener) : MessageListener {

    private val TAG = "MESSAGE_VM"
    var messageRepository: MessageRepository =
        MessageRepositoryImpl(
            this,
            BlameoChatSdk.getInstance().uId
        )

    fun getMessageById(id: String) {
        if(TextUtils.isEmpty(id))   return
        Log.e(TAG, "get by id: $id")
        messageRepository.getMessageById(id)
    }

    fun createMessage(content: String, type: Int, channelId: String) {
        messageRepository.createMessage(CreateMessageBody(content, type, channelId))
    }

    fun getMessages(channelId: String, lastId: String) {
        Log.e(TAG, "load messages in channel: $channelId with lastId = $lastId")
        messageRepository.getMessages(channelId, lastId)
    }

    fun sendSeenMessageEvent(channelId: String, messageId: String, authorId: String){
        messageRepository.sendSeenMessageEvent(channelId, messageId, authorId)
    }

    fun sendReceivedMessageEvent(channelId: String, messageId: String, authorId: String){
        messageRepository.sendReceivedMessageEvent(channelId, messageId, authorId)
    }

    fun receiveEventNewMessage(message: Message){
        messageRepository.receiveEventNewMessage(message)
    }

    fun receiveEventSeenMessage(messageId: String){
        messageRepository.receiveEventSeenMessage(messageId)
    }

    fun receiveEventReceiveMessage(messageId: String){
        messageRepository.receiveEventReceiveMessage(messageId)
    }

    override fun onGetMessagesSuccess(messages: ArrayList<Message>) {
        messages.reverse()
        listener.onGetMessagesSuccess(messages)
    }

    override fun onGetMessagesError(error: String) {

    }

    override fun onGetMessageByIdSuccess(message: Message) {
        listener.onGetMessageByIdSuccess(message)
    }

    override fun onCreateMessageSuccess(message: Message) {
        listener.onCreateMessageSuccess(message)
    }

    override fun onMarkSeenMessageSuccess(id: String) {
        listener.onMarkSeenMessageSuccess(id)
    }

    override fun onMarkSeenMessageFail(error: String) {
        listener.onMarkSeenMessageFail(error)
    }

    override fun onMarkReceiveMessageSuccess() {
    }

    override fun onMarkReceiveMessageFail(error: String) {
        listener.onMarkReceiveMessageFail(error)
    }

    fun syncMessage() {
        messageRepository.syncUnsentMessage()
    }
}