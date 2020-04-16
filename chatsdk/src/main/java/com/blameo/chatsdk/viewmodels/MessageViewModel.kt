package com.blameo.chatsdk.viewmodels

import android.text.TextUtils
import android.util.Log
import com.blameo.chatsdk.local.LocalMessageRepository
import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.sources.MessageRepository
import com.blameo.chatsdk.sources.MessageRepositoryImpl

interface MessageListener {
    fun onGetMessagesSuccess(messages: ArrayList<Message>)
    fun onGetMessagesError(error: String)
    fun onGetMessageByIdSuccess(message: Message)
    fun onCreateMessageSuccess(message: Message)
    fun onMarkSeenMessageSuccess()
    fun onMarkSeenMessageFail(error: String)
    fun onMarkReceiveMessageSuccess()
    fun onMarkReceiveMessageFail(error: String)
}

class MessageViewModel(private val listener: MessageListener,
                       localMessageRepository: LocalMessageRepository) : MessageListener {

    private val TAG = "MESSAGE_VM"
    var messageRepository: MessageRepository = MessageRepositoryImpl(this, localMessageRepository)

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

    override fun onMarkSeenMessageSuccess() {
    }

    override fun onMarkSeenMessageFail(error: String) {
        listener.onMarkSeenMessageFail(error)
    }

    override fun onMarkReceiveMessageSuccess() {
    }

    override fun onMarkReceiveMessageFail(error: String) {
        listener.onMarkReceiveMessageFail(error)
    }
}