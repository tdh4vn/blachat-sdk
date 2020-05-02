package com.blameo.chatsdk.repositories

import android.text.TextUtils
import android.util.Log
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.controllers.MessageListener
import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.repositories.local.Constant
import com.blameo.chatsdk.repositories.local.LocalChannelRepositoryImpl
import com.blameo.chatsdk.repositories.local.LocalMessageRepository
import com.blameo.chatsdk.repositories.local.LocalMessageRepositoryImpl
import com.blameo.chatsdk.repositories.remote.MessageRemoteRepository
import com.blameo.chatsdk.repositories.remote.MessageRemoteRepositoryImpl
import com.blameo.chatsdk.repositories.remote.net.APIProvider
import java.util.*
import kotlin.collections.ArrayList

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

interface MessageResultListener {
    fun onGetRemoteMessagesSuccess(messages: ArrayList<Message>)
    fun onGetRemoteMessagesFailed(error: String)
    fun onGetNewerMessagesSuccess(messages: ArrayList<Message>)
    fun onGetNewerMessagesFailed(error: String)
    fun onCreateMessageSuccess(temID: String, message: Message)
    fun onCreateMessageFailed(message: Message)
    fun onMarkSeenMessageSuccess(messageId: String)
    fun onMarkSeenMessageFail(error: String)
    fun onMarkReceiveMessageSuccess(messageId: String)
    fun onMarkReceiveMessageFail(error: String)
}

class MessageRepositoryImpl(
    private val messageListener: MessageListener,
    private val userID: String
) : MessageRepository, MessageResultListener {

    private var messageRemoteRepository: MessageRemoteRepository =
        MessageRemoteRepositoryImpl(
            APIProvider.messageAPI,
            this
        )
    private val TAG = "MESS_REPO"
    private var localMessages: ArrayList<Message> = arrayListOf()
    private val localMessageRepository: LocalMessageRepository = LocalMessageRepositoryImpl(BlameoChatSdk.getInstance().context)
    private val localChannel = LocalChannelRepositoryImpl(BlameoChatSdk.getInstance().context)

    override fun getMessages(channelId: String, lastMessageId: String) {
        localMessages = localMessageRepository.getAllMessagesInChannel(channelId, lastMessageId)
        Log.i(TAG, "local size: ${localMessages.size}")

        if (localMessages.size > 0)
            messageListener.onGetMessagesSuccess(localMessages)
        else {
            messageRemoteRepository.getMessages(channelId, lastMessageId)
        }
    }

    override fun getMessageByIdLocal(id: String): Message {
        return localMessageRepository.getMessageByID(id)
    }

    override fun createMessage(body: CreateMessageBody) {

        val tempId = Date().time.toString()
        val message = Message(
            tempId,
            userID,
            body.channelId,
            body.content,
            body.type,
            null
        )
        localMessageRepository.addLocalMessage(message)
        messageRemoteRepository.createMessage(tempId, body, message)
    }

    override fun sendSeenMessageEvent(channelId: String, messageId: String, authorId: String) {
        messageRemoteRepository.sendSeenMessageEvent(channelId, messageId, authorId)
    }

    override fun sendReceivedMessageEvent(
        channelId: String,
        messageId: String,
        authorId: String
    ) {
        messageRemoteRepository.sendReceivedMessageEvent(channelId, messageId, authorId)
    }

    override fun receiveEventNewMessage(message: Message) {
        localMessageRepository.addLocalMessage(message)
        messageRemoteRepository.sendReceivedMessageEvent(
            message.id,
            message.channelId,
            message.authorId
        )
    }

    override fun receiveEventSeenMessage(messageId: String) {
        localMessageRepository.updateStatusMessage(messageId, Constant.MESSAGE_SEEN_AT)
    }

    override fun receiveEventReceiveMessage(messageId: String) {

    }

    override fun syncUnsentMessage() {
        val messageUnsent = localMessageRepository.unsentMessage

        for (message in messageUnsent) {
            messageRemoteRepository.resentMessage(
                message,
                onSuccess = { messageSent ->
                    localMessageRepository.updateMessage(message.id, messageSent)
                },
                onFailed = { throwable ->
                    throwable.printStackTrace()
                }

            )
        }
    }

    override fun onGetRemoteMessagesSuccess(messages: ArrayList<Message>) {
        messageListener.onGetMessagesSuccess(messages)

        Log.i(TAG, "remote size: ${messages.size}")
        messages.forEach { localMessageRepository.addLocalMessage(it) }
    }

    override fun onGetRemoteMessagesFailed(error: String) {
        Log.i(TAG, "remote size failed: $error")
        messageListener.onGetMessagesError(error)
    }

    override fun onGetNewerMessagesSuccess(messages: ArrayList<Message>) {
        messages.forEach { localMessageRepository.addLocalMessage(it) }
        messageListener.onNewMessages(messages)
    }

    override fun onGetNewerMessagesFailed(error: String) {

    }

    override fun onCreateMessageSuccess(temID: String, message: Message) {
        message.sentAt = Date(temID.toLong())
        messageListener.onCreateMessageSuccess(message)
        localMessageRepository.updateMessage(temID, message)
        localChannel.updateLastMessage(message.channelId, message.id)
    }

    override fun onCreateMessageFailed(localMessage: Message) {
        messageListener.onCreateMessageSuccess(localMessage)
    }

    override fun onMarkSeenMessageSuccess(messageId: String) {
        messageListener.onMarkSeenMessageSuccess(messageId)
        localMessageRepository.updateStatusMessage(messageId, Constant.MESSAGE_SEEN_AT)
    }

    override fun onMarkSeenMessageFail(error: String) {
        messageListener.onMarkSeenMessageFail(error)
    }

    override fun onMarkReceiveMessageSuccess(messageId: String) {
        localMessageRepository.updateStatusMessage(messageId, Constant.MESSAGE_SENT_AT)
    }

    override fun onMarkReceiveMessageFail(error: String) {
        messageListener.onMarkReceiveMessageFail(error)
    }

}