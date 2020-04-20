package com.blameo.chatsdk

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blameo.chatsdk.local.*
import com.blameo.chatsdk.models.events.*
import com.blameo.chatsdk.models.pojos.*
import com.blameo.chatsdk.net.APIProvider
import com.blameo.chatsdk.viewmodels.*
import com.google.gson.Gson
import io.github.centrifugal.centrifuge.*
import io.github.centrifugal.centrifuge.EventListener
import okhttp3.*
import okio.ByteString
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit


private var shareInstance: BlameoChatSdk? = null

interface OnTypingListener{
    fun onTyping()
    fun onStopTyping()
    fun onNewMessage(message: Message)
}

interface OnCursorChangeListener{
    fun onCursorChange(type: String, cursor: CursorEvent)
}

interface OnNewChannelListener{
    fun onNewChannel(channel: Channel)
}

class BlameoChatSdk : ChatListener() {

    lateinit var token: String
    lateinit var tokenWs: String
    lateinit var uId: String
    lateinit var uidSocket: String
    lateinit var wsURL: String
    lateinit var channel: String
    lateinit var currentChannelID: String
    lateinit var context: Context

    val TAG = "BlameoChat"

    private var getChannelsListener: GetChannelsListener? = null
    lateinit var getMessagesListener: GetMessagesListener
    private var usersInChannelListener: GetUsersInChannelListener? = null
    lateinit var createChannelListener: CreateChannelListener
    lateinit var createMessageListener: CreateMessageListener
    lateinit var markSeenMessageListener: MarkSeenMessageListener
    lateinit var markReceiveMessageListener: MarkReceiveMessageListener
    lateinit var channelViewModel: ChannelViewModel
    lateinit var messageViewModel: MessageViewModel
    lateinit var userViewModel: UserViewModel
    lateinit var typingListener: OnTypingListener
    lateinit var onCursorChangeListener: OnCursorChangeListener
    lateinit var onNewChannelListener: OnNewChannelListener

    lateinit var localChannels: LocalChannelRepository
    lateinit var localMessages: LocalMessageRepository
    lateinit var localUserInChannels: LocalUserInChannelRepository
    lateinit var localUsers: LocalUserRepository
    private var channels = arrayListOf<Channel>()

    fun addOnTypingListener(onTypingListener: OnTypingListener){
        this.typingListener = onTypingListener
    }

    fun addOnCursorChangeListener(onCursorChangeListener: OnCursorChangeListener){
        this.onCursorChangeListener = onCursorChangeListener
    }

    fun addOnNewChannelListener(onNewChannelListener: OnNewChannelListener){
        this.onNewChannelListener = onNewChannelListener
    }

    private val channelListener = object : ChannelListener {
        override fun onGetChannelsSuccess(channels: ArrayList<Channel>) {
            if (channels.size > 0) {
                this@BlameoChatSdk.channels = channels
//                getChannelsListener.onGetChannelsSuccess(channels)

                channels.forEach { channel ->
                    if(channel.last_message == null) {
                        Log.i(TAG, "id ${channel.id} last_mess ${channel.last_message_id}")
                        if (!TextUtils.isEmpty(channel.last_message_id))
                            messageViewModel.getMessageById(channel.last_message_id)
                    }else{
                        channelViewModel.updateLastMessage(channel.id, channel.last_message.id)
                    }
                }

                channels.forEach { channel ->
                    Log.i(TAG, "get users in ${channel.id}")
                    Log.i(TAG, "channel name: ${channel.name} ${channel.id}")
                    channelViewModel.getUsersInChannel(channel.id)
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

        override fun onGetUsersInChannelSuccess(channelId: String, ids: ArrayList<String>) {
            Log.i(TAG, "get ids: ${ids.size}")
            getUsersByIds(channelId, ids)
        }

        override fun onGetUsersInChannelFailed(error: String) {

        }

    }

    private val messageListener = object : MessageListener {
        override fun onGetMessagesSuccess(messages: ArrayList<Message>) {
            Log.i(TAG, "ok : ${messages.size}")
            getMessagesListener.getMessagesSuccess(messages)
        }

        override fun onGetMessagesError(error: String) {
        }

        override fun onGetMessageByIdSuccess(message: Message) {
            Log.i(TAG, "message : ${message.id}")
            channels.forEach { channel ->
                if (channel.id == message.channel_id) {
                    channel.last_message = message
//                    getChannelsListener.onGetChannelsSuccess(channels)
                    return@forEach
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

    private val userListener = object : UserListener{
        override fun onUsersByIdsSuccess(channelId: String, users: ArrayList<User>) {
            Log.e(TAG, "success ${users.size}")
//            usersInChannelListener.onGetUsersByIdsSuccess(users)
            if(users.size == 2){
                channels.forEach { channel ->
                    if(channelId == channel.id){
                        users.forEach { user ->
                            if(user.id != uId){
                                channel.name = user.name
                                channel.avatar = user.avatar
                                return@forEach
                            }
                        }
                        return@forEach
                    }
                }
            }

            if(usersInChannelListener != null)
                usersInChannelListener?.onGetUsersByIdsSuccess(channelId, users)

            if(channels[channels.size -1].id == channelId){
                getChannelsListener?.onGetChannelsSuccess(channels)
            }
        }

        override fun onGetUsersByIdsError(error: String) {
        }

    }

    companion object {
        fun getInstance(): BlameoChatSdk {
            if (shareInstance == null)
                shareInstance = BlameoChatSdk()
            return shareInstance!!
        }
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
        initViewModels()
    }

    private fun initViewModels() {
        channelViewModel = ChannelViewModel(channelListener, this.localChannels, this.localUserInChannels)
        messageViewModel = MessageViewModel(messageListener)
        userViewModel = UserViewModel(userListener, this.localUsers)
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
                when(p.type){
                    "typing_event" -> {
                        val typing = g.fromJson(p.payload.toString(), Payload::class.java)
                        if(typing.channel_id != currentChannelID)   return
                        if(typing.is_typing)
                            typingListener.onTyping()
                        else
                            typingListener.onStopTyping()
                    }
                    "new_message" -> {
                        val new_event = g.fromJson(data, NewMessageEvent::class.java)
                        val message = new_event.payload
                        if(message.author_id != uId){
                            typingListener.onNewMessage(message)
                            messageViewModel.receiveEventNewMessage(message)
                        }

                    }

                    "mark_seen" -> {
                        val event = g.fromJson(data, StatusMessageEvent::class.java)
                        println("seen")
                        onCursorChangeListener.onCursorChange("SEEN", event.payload)
                        messageViewModel.receiveEventSeenMessage(event.payload.message_id)
                    }

                    "mark_receive" -> {
                        val event = g.fromJson(data, StatusMessageEvent::class.java)
                        println("receive")
                        messageViewModel.receiveEventReceiveMessage(event.payload.message_id)
                    }

                    "new_channel" -> {
                        val event = g.fromJson(data, NewChannelEvent::class.java)
                        println("new channel")
                        val c = event.payload
                        val channel = Channel(c.id, c.name, c.avatar, c.type, c.updated_at, c.created_at, c.last_message_id)
                        channelViewModel.addNewChannel(channel)
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
//        connectPresence()

    }

    fun sendTypingEvent(is_typing: Boolean, channelID: String){
        if(is_typing)
            channelViewModel.putTypingInChannel(channelID)
        else
            channelViewModel.putStopTypingInChannel(channelID)
    }

    private fun initDB() {
        localChannels = LocalChannelRepositoryImpl(context)
        localMessages = LocalMessageRepositoryImpl(context)
        localUserInChannels = LocalUserInChannelRepositoryImpl(context)
        localUsers = LocalUserRepositoryImpl(context)
        exportDB()
    }

    fun initContext(activity: AppCompatActivity) {
        context = activity
        initDB()
    }

    fun getChannels(getChannelsListener: GetChannelsListener) {
        this.getChannelsListener = getChannelsListener
        channelViewModel.getChannels()
    }

    fun exportDB() {
        localChannels.exportChannelDB()
        localMessages.exportMessageDB()
        localUserInChannels.exportUicDB()
    }

    fun getUsersInChannel(channelId: String, usersInChannelListener: GetUsersInChannelListener) {
        this.usersInChannelListener = usersInChannelListener
        channelViewModel.getUsersInChannel(channelId)
    }

    private fun getUsersByIds(channelId: String, ids: ArrayList<String>) {
        userViewModel.getUsersByIds(channelId, ids)
    }

    fun createChannel(ids: ArrayList<String>, name: String, type: Int, listener: CreateChannelListener) {
        this.createChannelListener = listener
        channelViewModel.createChannel(ids, name, type)
    }

    fun putTypingInChannel(cId: String){
        channelViewModel.putTypingInChannel(cId)
    }

    fun putStopTypingInChannel(cId: String){
        channelViewModel.putStopTypingInChannel(cId)
    }

    fun createMessage(content: String, type: Int, channelId: String, listener: CreateMessageListener) {
        createMessageListener = listener
        messageViewModel.createMessage(content, type, channelId)
    }

    fun getMessages(channelId: String, lastId: String, listener: GetMessagesListener) {
        getMessagesListener = listener
        currentChannelID = channelId
        messageViewModel.getMessages(channelId, lastId)
    }

    fun sendSeenMessageEvent(channelId: String, messageId: String, authorId: String,
                             listener: MarkSeenMessageListener){
        markSeenMessageListener = listener
        messageViewModel.sendSeenMessageEvent(channelId, messageId, authorId)
    }

    fun sendReceiveMessageEvent(channelId: String, messageId: String, authorId: String,
                                 listener: MarkReceiveMessageListener){
        markReceiveMessageListener = listener
        messageViewModel.sendReceivedMessageEvent(channelId, messageId, authorId)
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
