package com.blameo.chatsdk

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blameo.chatsdk.controllers.*
import com.blameo.chatsdk.models.events.*
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.models.pojos.RemoteUserChannel
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.repositories.local.*
import com.blameo.chatsdk.repositories.remote.net.APIProvider
import com.google.gson.Gson
import io.github.centrifugal.centrifuge.*
import io.github.centrifugal.centrifuge.EventListener
import okhttp3.*
import okio.ByteString
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


private var shareInstance: BlameoChatSdk? = null

interface OnTypingListener {
    fun onTyping()
    fun onStopTyping()
}

interface OnEventListener {
    fun onNewMessage(message: Message)
}

interface OnCursorChangeListener {
    fun onCursorChange(type: String, cursor: CursorEvent)
}

interface OnNewChannelListener {
    fun onNewChannel(channel: Channel)
}

class BlameoChatSdk : ChatListener() {

    lateinit var token: String
    lateinit var tokenWs: String
    lateinit var uId: String
    lateinit var wsURL: String
    lateinit var channel: String
    lateinit var currentChannelID: String
    lateinit var context: Context

    val TAG = "BlameoChat"

    private var getChannelsListener: GetChannelsListener? = null
    lateinit var getMessagesListener: GetMessagesListener
    lateinit var createChannelListener: CreateChannelListener
    lateinit var createMessageListener: CreateMessageListener
    lateinit var markSeenMessageListener: MarkSeenMessageListener
    lateinit var markReceiveMessageListener: MarkReceiveMessageListener
    lateinit var channelController: ChannelController
    lateinit var messageController: MessageController
    lateinit var userController: UserController
    lateinit var typingListener: OnTypingListener
    lateinit var onEventListener: OnEventListener
    lateinit var onCursorChangeListener: OnCursorChangeListener
    lateinit var onNewChannelListener: OnNewChannelListener
    lateinit var onGetAllMembersListener: GetAllMembersListener
    lateinit var onInviteUsersToChannelListener: InviteUsersToChannelListener

    lateinit var localChannels: LocalChannelRepository
    lateinit var localMessages: LocalMessageRepository
    lateinit var localUsers: LocalUserRepository
    private var channels = arrayListOf<Channel>()

    private var uicMap: HashMap<String, ArrayList<RemoteUserChannel>> = hashMapOf()
    private var usersMap: HashMap<String, User> = hashMapOf()
    private var lastChannelId = ""

    private val channelListener = object : ChannelListener {
        override fun onGetChannelsSuccess(channels: ArrayList<Channel>) {
            this@BlameoChatSdk.channels = channels
            lastChannelId = ""
            if (channels.size > 0) {

                channels.forEach { channel ->
//                    Log.i(TAG, "id ${channel.id} last_mess ${channel.lastMessageId}")
                    lastChannelId = channel.id
                    var id = channel.lastMessageId
                    if (TextUtils.isEmpty(id)){
                        if(channel.lastMessage != null) id = channel.lastMessage.id
                    }
                    if(!TextUtils.isEmpty(id))
                        messageController.getMessageById(id)
                    else{
                        val m = Message()
                        m.channelId = channel.id
                        messageListener.onGetMessageByIdSuccess(m)
                    }

                }
            }
        }

        override fun onGetChannelError(error: String) {
            Log.i(TAG, "get channels error $error")
        }

        override fun onCreateChannelSuccess(channel: Channel) {
            createChannelListener.createChannelSuccess(channel)
        }

        override fun onCreateChannelFailed(error: String) {

        }

        override fun onGetUsersInChannelSuccess(channelId: String, uic: ArrayList<RemoteUserChannel>) {
//            Log.i(TAG, "get ids: ${uic.size}")
            getUsersByIds(channelId, convertUICToStringIds(channelId, uic))
        }

        override fun onGetUsersInChannelFailed(error: String) {

        }

        override fun onInviteUsersToChannelSuccess(channelId: String, userIds: ArrayList<String>) {
            val users = userController.getLocalUsers(userIds)
            onInviteUsersToChannelListener.onInviteSuccess(channelId, users)
        }

    }

    private val messageListener = object : MessageListener {
        override fun onGetMessagesSuccess(messages: ArrayList<Message>) {
//            Log.i(TAG, "ok : ${messages.size}")
            if(messages.size == 0)  return
            messages.forEachIndexed { index, message ->
                messages[index] = handleResultMessage(message)
            }

            getMessagesListener.getMessagesSuccess(messages)
            updateUserLastSeenInChannel(messages[messages.size -1])
        }

        override fun onGetMessagesError(error: String) {
            getMessagesListener.getMessageFailed(error)
        }

        override fun onNewMessages(messages: ArrayList<Message>) {
            Log.i(TAG, "abc ${messages.size}")
            messages.forEach {
                Log.i(TAG, "abc ${it.id}")
                onEventListener.onNewMessage(it)
                channelController.updateLastMessage(it.channelId, it.id)
            }
        }

        override fun onGetMessageByIdSuccess(message: Message) {
            if (message.channelId == lastChannelId) {
//                Log.i(TAG, "this is the message id of last channel")
                channels.forEach {
                    channelController.getUsersInChannel(it.id)
                }
            }
        }

        override fun onCreateMessageSuccess(message: Message) {
            createMessageListener.createMessageSuccess(message)
        }

        override fun onMarkSeenMessageSuccess(messageId: String) {
            markSeenMessageListener.onSuccess(messageId)
        }

        override fun onMarkSeenMessageFail(error: String) {
            markSeenMessageListener.onError(error)
        }

        override fun onMarkReceiveMessageSuccess() {
        }

        override fun onMarkReceiveMessageFail(error: String) {
            markReceiveMessageListener.onError(error)
        }
    }

    private val userListener = object : UserListener {
        override fun onUsersByIdsSuccess(channelId: String, users: ArrayList<User>) {
            Log.e(TAG, "users by ids: ${users.size} $channelId")

            users.forEach {
                usersMap[it.id] = it
            }


            if(channels.size > 0){
                if (channelId == lastChannelId) {
                    Log.e(TAG, "this is last channel $channelId")
                    responseChannels()
                }
            }else{
                // on newer channels

                val channel = channelController.getLocalChannelById(channelId)
                if (channel.lastMessage == null) {
                    if(!TextUtils.isEmpty(channel.lastMessageId)){
                        val message = messageController.getLocalMessageById(channel.lastMessageId)
                        channel.lastMessage = message
                    }

                }

                Log.e(TAG, "users by ids in newer channels: ${users.size} $channelId ${channel.avatar}")

                Log.i(TAG, "${channel.id} ${users.size}")
                if (users.size == 2) {
                    users.forEach { user ->
                        if (user.id != uId) {
                            channel.avatar = user.avatar
                            channel.name = user.name
                            return@forEach
                        }
                    }
                }
                onNewChannelListener.onNewChannel(channel)
            }
        }

        override fun onGetUsersByIdsError(error: String) {
        }

        override fun onGetAllMembersSuccess(users: ArrayList<User>) {
            users.forEach {
                usersMap[it.id] = it
            }

            onGetAllMembersListener.onSuccess(users)
        }

        override fun onGetAllMembersError(error: String) {

        }

    }

    companion object {
        fun getInstance(): BlameoChatSdk {
            if (shareInstance == null)
                shareInstance = BlameoChatSdk()
            return shareInstance!!
        }
    }

    fun addOnTypingListener(onTypingListener: OnTypingListener) {
        this.typingListener = onTypingListener
    }

    fun addOnEventListener(onEventListener: OnEventListener) {
        this.onEventListener = onEventListener
    }

    fun addOnCursorChangeListener(onCursorChangeListener: OnCursorChangeListener) {
        this.onCursorChangeListener = onCursorChangeListener
    }

    fun addOnNewChannelListener(onNewChannelListener: OnNewChannelListener) {
        this.onNewChannelListener = onNewChannelListener
    }


    private fun convertUICToStringIds(channelId: String, uic: ArrayList<RemoteUserChannel>): ArrayList<String>{
        uicMap[channelId] = uic
        val ids = arrayListOf<String>()
        uic.forEach {
            ids.add(it.memberId)
        }
        return ids
    }

    fun syncMessage() {
        this.messageController.syncMessage()
    }

    private fun handleResultMessage(message: Message) : Message{
        val uic = uicMap[message.channelId]
        uic?.forEach {
            var user = usersMap[it.memberId]
            if(user == null)    user = User(it.memberId)
            usersMap[it.memberId] = user
            if(it.lastSeen > message.createdAtString)
                message.seenBy.add(user)

            if(it.lastReceive > message.createdAtString)
                message.receiveBy.add(user)
        }
        return message
    }

    fun updateUserLastSeenInChannel(lastMessage: Message){
        userController.updateUserLastSeenInChannel(uId, lastMessage.channelId, lastMessage)
    }

    fun inviteUsersToChannel(channelId: String, userIds: ArrayList<String>, listener: InviteUsersToChannelListener){
        onInviteUsersToChannelListener = listener
        channelController.inviteUsersToChannel(channelId, userIds)
    }

    private fun responseChannels() {

        channels.forEach { channel ->
            if (channel.lastMessage == null) {
                if(!TextUtils.isEmpty(channel.lastMessageId)) {
                    val message = messageController.getLocalMessageById(channel.lastMessageId)
                    channel.lastMessage = message
                }
            }
            val uic = channelController.getLocalUsersInChannel(channel.id)
            val users = userController.getLocalUsersByIds(channel.id, convertUICToStringIds(channel.id, uic))

            Log.i(TAG, "${channel.id} ${users.size}")
            if (uic.size == 2) {
                users.forEach { user ->
                    if (user.id != uId) {
                        channel.avatar = user.avatar
                        channel.name = user.name
                        return@forEach
                    }
                }
            }
        }

        getChannelsListener?.onGetChannelsSuccess(channels)
        channels.clear()
    }



    fun sendTypingEvent(is_typing: Boolean, channelID: String) {
        if (is_typing)
            channelController.putTypingInChannel(channelID)
        else
            channelController.putStopTypingInChannel(channelID)
    }

    private fun initDB() {
        localChannels = LocalChannelRepositoryImpl(context)
        localMessages = LocalMessageRepositoryImpl(context)
        localUsers = LocalUserRepositoryImpl(context)
    }

    fun initContext(activity: AppCompatActivity) {
        context = activity
        initDB()
    }

    fun getAllMembers(listener: GetAllMembersListener) {
        onGetAllMembersListener = listener
        userController.getAllMembers()
    }

    fun getChannels(getChannelsListener: GetChannelsListener) {
        this.getChannelsListener = getChannelsListener
        channelController.getChannels()
    }

    fun exportDB() {
        localChannels.exportChannelDB()
        localMessages.exportMessageDB()
    }

    fun getUsersInChannel(channelId: String, usersInChannelListener: GetUsersInChannelListener) {
        val uic = channelController.getLocalUsersInChannel(channelId)
        val users = userController.getLocalUsersByIds(channelId, convertUICToStringIds(channelId, uic))
        usersInChannelListener.onGetUsersByIdsSuccess(channelId, users)
    }

    private fun getUsersByIds(channelId: String, ids: ArrayList<String>) {
        userController.getUsersByIds(channelId, ids)
    }

    fun createChannel(
        ids: ArrayList<String>, name: String, type: Int, listener: CreateChannelListener) {
        this.createChannelListener = listener
        channelController.createChannel(ids, name, type)
    }

    fun createMessage(
        content: String,
        type: Int,
        channelId: String,
        listener: CreateMessageListener
    ) {
        createMessageListener = listener
        messageController.createMessage(content, type, channelId)
    }

    fun getMessages(channelId: String, lastId: String, listener: GetMessagesListener) {
        getMessagesListener = listener
        currentChannelID = channelId
        messageController.getMessages(channelId, lastId)
    }

    fun sendSeenMessageEvent(
        channelId: String, messageId: String, authorId: String,
        listener: MarkSeenMessageListener
    ) {
        markSeenMessageListener = listener
        messageController.sendSeenMessageEvent(channelId, messageId, authorId)
    }

    fun sendReceiveMessageEvent(
        channelId: String, messageId: String, authorId: String,
        listener: MarkReceiveMessageListener
    ) {
        markReceiveMessageListener = listener
        messageController.sendReceivedMessageEvent(channelId, messageId, authorId)
    }

    fun initSession(
        baseUrl: String, ws: String,
        token: String, tokenWs: String,
        uid: String
    ) {
        APIProvider.setSession(baseUrl, token)
        this.token = token
        this.tokenWs = tokenWs
        this.wsURL = ws
        this.uId = uid
        this.channel = "chat#$uid"
        connectSocket()
        initController()
    }

    private fun initController() {
        channelController = ChannelController(channelListener, this.localChannels)
        messageController = MessageController(messageListener)
        userController = UserController(userListener, this.localUsers)
    }

    private fun connectSocket() {

        val listener: EventListener = object : EventListener() {
            override fun onConnect(client: Client?, event: ConnectEvent?) {
                println("connected ${event?.client}")
            }

            override fun onDisconnect(client: Client?, event: DisconnectEvent) {
                System.out.printf("disconnected %s, reconnect %s%n", event.reason, event.reconnect)
            }
        }

        val client = Client(wsURL, Options(), listener)
        client.setToken(tokenWs)


        val subListener: SubscriptionEventListener = object : SubscriptionEventListener() {
            override fun onSubscribeSuccess(sub: Subscription, event: SubscribeSuccessEvent) {
                Log.i(TAG, "Subscribed to " + sub.channel + "\n$event")
            }

            override fun onSubscribeError(sub: Subscription, event: SubscribeErrorEvent) {
                Log.i(TAG, "Subscribe error " + sub.channel + ": " + event.message)
            }

            override fun onPublish(sub: Subscription, event: PublishEvent) {
                val data = String(event.data, StandardCharsets.UTF_8)
                Log.i(TAG, "Message from " + sub.channel + ": " + data)
                val g = Gson()
                val p = g.fromJson(data, Event::class.java)
                when (p.type) {
                    "typing_event" -> {
                        val typing = g.fromJson(p.payload.toString(), Payload::class.java)
                        if (typing.channel_id != currentChannelID) return
                        if (typing.is_typing)
                            typingListener.onTyping()
                        else
                            typingListener.onStopTyping()
                    }
                    "new_message" -> {
                        val new_event = g.fromJson(data, NewMessageEvent::class.java)
                        val message = new_event.payload
                        if (message.authorId != uId) {
                            onEventListener.onNewMessage(message)
                            messageController.receiveEventNewMessage(message)
                            channelController.updateLastMessage(message.channelId, message.id)
                        }

                    }

                    "mark_seen" -> {
                        val event = g.fromJson(data, StatusMessageEvent::class.java)
                        onCursorChangeListener.onCursorChange("SEEN", event.payload)
                        messageController.receiveEventSeenMessage(event.payload.message_id)
                    }

                    "mark_receive" -> {
                        val event = g.fromJson(data, StatusMessageEvent::class.java)
                        messageController.receiveEventReceiveMessage(event.payload.message_id)
                        val message = Message()
                        message.id = event.payload.message_id
                        message.createdAt = Date(event.payload.time)
                        userController.updateUserLastSeenInChannel(event.payload.actor_id, event.payload.channel_id, message)
                    }

                    "new_channel" -> {
                        val event = g.fromJson(data, NewChannelEvent::class.java)
                        val c = event.payload
                        val channel = Channel(
                            c.id,
                            c.name,
                            c.avatar,
                            c.type,
                            c.updatedAt,
                            c.createdAt,
                            c.last_message_id)
                        channelController.addNewChannel(channel)
                        onNewChannelListener.onNewChannel(channel)
                    }
                }

            }

            override fun onUnsubscribe(sub: Subscription, event: UnsubscribeEvent) {
                Log.i(TAG, "Unsubscribed from " + sub.channel)
            }

            override fun onJoin(sub: Subscription?, event: JoinEvent?) {
                Log.i(TAG, "onJoin $event ${event?.info}" + sub?.channel)
            }

            override fun onLeave(sub: Subscription?, event: LeaveEvent?) {
                Log.i(TAG, "onLeave $event ${event?.info}" + sub?.channel)
            }
        }

        val subscription: Subscription
        subscription = try {
            client.newSubscription(channel, subListener)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        subscription.subscribe()

        val presenceListener: SubscriptionEventListener = object : SubscriptionEventListener() {
            override fun onSubscribeSuccess(sub: Subscription, event: SubscribeSuccessEvent) {
                Log.i("$TAG presence", "Subscribed to " + sub.channel + " $event")
            }

            override fun onSubscribeError(sub: Subscription, event: SubscribeErrorEvent) {
                Log.i("$TAG presence", "Subscribe error " + sub.channel + ": " + event.message)
            }

            override fun onPublish(sub: Subscription, event: PublishEvent) {
                val data = String(event.data, StandardCharsets.UTF_8)
                Log.i("$TAG presence", "Message from " + sub.channel + ": " + data)
            }

            override fun onUnsubscribe(sub: Subscription, event: UnsubscribeEvent) {
                Log.i("$TAG presence", "Unsubscribed from " + sub.channel)
            }

            override fun onJoin(sub: Subscription?, event: JoinEvent?) {
                Log.i("$TAG presence", "onJoin $event ${event?.info}" + sub?.channel)
            }

            override fun onLeave(sub: Subscription?, event: LeaveEvent?) {
                Log.i("$TAG presence", "onLeave $event ${event?.info}" + sub?.channel)
            }
        }

        val presenceSubscription: Subscription
        presenceSubscription = try {
            client.newSubscription("present", presenceListener)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        presenceSubscription.subscribe()

        client.connect()

    }

    private fun connectPresence() {

        val client = OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .build()
        val request: Request = Request.Builder().url("ws://159.65.2.104:9000/ws").build()
        request.headers("Authorization' : 'Bearer $token")
        val listener = EchoWebSocketListener()
        val ws: WebSocket = client.newWebSocket(request, listener)

//        client.dispatcher().executorService().shutdown()
    }

    private class EchoWebSocketListener : WebSocketListener() {
        val TAG = "ECHO"

        override fun onOpen(webSocket: WebSocket, response: Response?) {
            Log.i(TAG, "onOpen : ${response?.isSuccessful}")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.i(TAG, "Receiving : $text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.i(TAG, "Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(
            webSocket: WebSocket,
            code: Int,
            reason: String
        ) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            Log.i(TAG, "Closing : $code / $reason")
        }

        override fun onFailure(
            webSocket: WebSocket?,
            t: Throwable,
            response: Response?
        ) {
            Log.i(TAG, "Error : " + t.message)
        }

        companion object {
            private const val NORMAL_CLOSURE_STATUS = 1000
        }
    }

}
