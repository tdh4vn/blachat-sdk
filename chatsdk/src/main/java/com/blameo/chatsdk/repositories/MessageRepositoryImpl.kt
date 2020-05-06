package com.blameo.chatsdk.repositories

import android.util.Log
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.controllers.MessageListener
import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.repositories.local.*
import com.blameo.chatsdk.repositories.remote.MessageRemoteRepository
import com.blameo.chatsdk.repositories.remote.MessageRemoteRepositoryImpl
import com.blameo.chatsdk.repositories.remote.net.APIProvider
import java.util.*
import kotlin.collections.ArrayList

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
    private val localChannel:LocalChannelRepository = LocalChannelRepositoryImpl(BlameoChatSdk.getInstance().context)

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

        val date = Date()
        val tempId = date.time.toString()
        val message = Message(
            tempId,
            userID,
            body.channelId,
            body.content,
            body.type,
            null
        )

        message.createdAt = date
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

        Log.e(TAG, "sync message :${messageUnsent.size}")

        for (message in messageUnsent) {
            Log.e(TAG, "unsent message :${message.id} ${message.content} ${message.channelId}")
            messageRemoteRepository.resentMessage(
                message,
                onSuccess = { messageSent ->
                    Log.e(TAG, "unsent message success:${messageSent.id} ${messageSent.content} ${messageSent.channelId}")
                    localMessageRepository.updateMessage(message.id, messageSent)
                    localChannel.updateLastMessage(messageSent.channelId, messageSent.id)
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

        localMessageRepository.exportMessageDB()
    }

    override fun onCreateMessageFailed(message: Message) {
        messageListener.onCreateMessageSuccess(message)
        localChannel.updateLastMessage(message.channelId, message.id)

        localMessageRepository.exportMessageDB()
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