package com.blameo.chatsdk.sources

import android.util.Log
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.local.Constant
import com.blameo.chatsdk.local.LocalChannelRepositoryImpl
import com.blameo.chatsdk.local.LocalMessageRepository
import com.blameo.chatsdk.local.LocalMessageRepositoryImpl
import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.net.APIProvider
import com.blameo.chatsdk.repositories.MessageRemoteRepository
import com.blameo.chatsdk.repositories.MessageRemoteRepositoryImpl
import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil
import com.blameo.chatsdk.viewmodels.MessageListener

interface MessageRepository {
    fun getMessages(channelId: String, lastMessageId: String)
    fun getMessageById(id: String)
    fun createMessage(body: CreateMessageBody)
    fun sendSeenMessageEvent(channelId: String, messageId: String, authorId: String)
    fun sendReceivedMessageEvent(channelId: String, messageId: String, authorId: String)
    fun receiveEventNewMessage(message: Message)
    fun receiveEventSeenMessage(messageId: String)
    fun receiveEventReceiveMessage(messageId: String)
}

interface MessageResultListener {
    fun onGetRemoteMessagesSuccess(messages: ArrayList<Message>)
    fun onGetRemoteMessagesFailed(error: String)
    fun onGetMessageByIdSuccess(message: Message)
    fun onCreateMessageSuccess(temID: String, message: Message)
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
        MessageRemoteRepositoryImpl(APIProvider.messageAPI, this)
    private val TAG = "MESS_REPO"
    private var localMessages: ArrayList<Message> = arrayListOf()
    private val localMessageRepository: LocalMessageRepository
    = LocalMessageRepositoryImpl(BlameoChatSdk.getInstance().context)

    private val localChannel = LocalChannelRepositoryImpl(BlameoChatSdk.getInstance().context)

    override fun getMessages(channelId: String, lastMessageId: String) {
        localMessages = localMessageRepository.getAllMessagesInChannel(channelId, lastMessageId)
        Log.i(TAG, "local size: ${localMessages.size}")
        messageRemoteRepository.getMessages(channelId, lastMessageId)
    }

    override fun getMessageById(id: String) {
        var message: Message? = null
        if (localMessageRepository.allLocalMessages.size > 0)
            message = localMessageRepository.getMessageByID(id)
        if (message != null) {
            Log.i(TAG, "get local $id")
            messageListener.onGetMessageByIdSuccess(message)
        } else {
            Log.i(TAG, "get remote $id")
            messageRemoteRepository.getMessageById(id)
        }
    }

    override fun createMessage(body: CreateMessageBody) {
        val time = ChatSdkDateFormatUtil.getInstance().currentTimeUTC
        val message = Message(time, userID, body.channel_id, body.content, body.type)
        localMessageRepository.addLocalMessage(message)
        messageRemoteRepository.createMessage(time, body)
    }

    override fun sendSeenMessageEvent(channelId: String, messageId: String, authorId: String) {
        messageRemoteRepository.sendSeenMessageEvent(channelId, messageId, authorId)
    }

    override fun sendReceivedMessageEvent(channelId: String, messageId: String, authorId: String) {
        messageRemoteRepository.sendReceivedMessageEvent(channelId, messageId, authorId)
    }

    override fun receiveEventNewMessage(message: Message) {
        localMessageRepository.addLocalMessage(message)
        messageRemoteRepository.sendReceivedMessageEvent(message.id, message.channel_id, message.author_id)
    }

    override fun receiveEventSeenMessage(messageId: String) {
        localMessageRepository.updateStatusMessage(messageId, Constant.MESSAGE_SEEN_AT)
    }

    override fun receiveEventReceiveMessage(messageId: String) {

    }

    override fun onGetRemoteMessagesSuccess(messages: ArrayList<Message>) {
        messageListener.onGetMessagesSuccess(messages)
        Log.i(TAG, "remote size: ${messages.size}")
        messages.forEach { localMessageRepository.addLocalMessage(it) }
    }

    override fun onGetRemoteMessagesFailed(error: String) {
        Log.i(TAG, "remote size failed: ${localMessages.size} $error")
        messageListener.onGetMessagesSuccess(localMessages)
        messageListener.onGetMessagesError(error)
    }

    override fun onGetMessageByIdSuccess(message: Message) {
        messageListener.onGetMessageByIdSuccess(message)
        localMessageRepository.addLocalMessage(message)
    }

    override fun onCreateMessageSuccess(temID: String, message: Message) {
        message.sent_at = temID
        messageListener.onCreateMessageSuccess(message)
        localMessageRepository.updateMessage(temID, message)
        localChannel.updateLastMessage(message.channel_id, message.id)
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

    fun getLocalMessages(): ArrayList<Message> {
        return this.localMessages
    }

}