package com.blameo.chatsdk

import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blameo.chatsdk.local.LocalChannelRepository
import com.blameo.chatsdk.local.LocalChannelRepositoryImpl
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.net.APIProvider
import com.blameo.chatsdk.viewmodels.ChannelViewModel
import com.blameo.chatsdk.viewmodels.MessageViewModel
import com.blameo.chatsdk.viewmodels.UserViewModel
import io.github.centrifugal.centrifuge.*
import io.github.centrifugal.centrifuge.EventListener
import okhttp3.*
import okio.ByteString
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.concurrent.TimeUnit


private var shareInstance: BlameoChatSdk? = null

class BlameoChatSdk : ChatListener() {

    lateinit var token: String
    lateinit var tokenWs: String
    lateinit var uId: String
    lateinit var wsURL: String
    lateinit var channel: String

    val TAG = "BlameoChat"

    lateinit var getChannelsListener: GetChannelsListener
    lateinit var usersInChannelListener: GetUsersInChannelListener
    lateinit var channelViewModel: ChannelViewModel
    lateinit var messageViewModel: MessageViewModel
    lateinit var userViewModel: UserViewModel

    lateinit var localChannels: LocalChannelRepository
    private var channels = arrayListOf<Channel>()

    companion object {
        fun getInstance(): BlameoChatSdk {
            if (shareInstance == null)
                shareInstance = BlameoChatSdk()
            return shareInstance!!
        }
    }

    fun initSession(ws: String, token: String, tokenWs: String, uid: String) {
        APIProvider.setSession(token)
        this.token = token
        this.tokenWs = tokenWs
        this.wsURL = ws
        this.uId = uid
        this.channel = "chat#$uid"
        connectSocket()
        initViewModels()
    }

    private fun initViewModels() {
        channelViewModel = ChannelViewModel.getInstance()
        messageViewModel = MessageViewModel.getInstance()
        userViewModel = UserViewModel.getInstance()
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

    private fun initDB(activity: AppCompatActivity) {
        localChannels = LocalChannelRepositoryImpl(activity)
    }

    fun initContext(activity: AppCompatActivity) {
        initDB(activity)
    }

    fun getChannels(getChannelsListener: GetChannelsListener) {
        this.getChannelsListener = getChannelsListener
        observeMessage()
        channelViewModel.getChannelsRemote()
        channelViewModel.channelsRemote.observeForever {
            if (it.size > 0) {
                channels = it
                it.forEach { channel ->
                    Log.i(TAG, "id ${channel.id} last_mess ${channel.last_message_id}")
                    if (!TextUtils.isEmpty(channel.last_message_id))
                        messageViewModel.getMessageByIdRemote(channel.last_message_id)
                    localChannels.addLocalChannel(channel)
                }
            }
        }
    }

    private fun observeMessage() {   // get last message by id

        messageViewModel.messageRemote.observeForever {
            if (it != null) {
                channels.forEach { channel ->
                    if (channel.id == it.channel_id) {
                        channel.last_message = it
                        getChannelsListener.onGetChannelsSuccess(channels)
                        return@forEach
                    }
                }
            }
        }
    }

    fun exportChannelDB() {
        localChannels.exportChannelDB()
    }

    fun getUsersInChannel(channelId: String, usersInChannelListener: GetUsersInChannelListener) {
        this.usersInChannelListener = usersInChannelListener
        channelViewModel.getUsersInChannel(channelId)

        channelViewModel.usersInChannel.observeForever {
            getUsersByIds(it)
        }
    }

    private fun getUsersByIds(ids: ArrayList<String>) {
        userViewModel.getUsersByIdsRemote(ids)
        userViewModel.usersByIds.observeForever {
            Log.e(TAG, "success ${it.size}")
            usersInChannelListener.onGetUsersByIdsSuccess(it)
        }
    }

    fun createChannel(
        ids: ArrayList<String>,
        name: String,
        type: Int,
        listener: CreateChannelListener
    ) {
        channelViewModel.createChannel(ids, name, type)

        channelViewModel.createChannel.observeForever {
            listener.createChannelSuccess(it)
        }
    }

    fun createMessage(
        content: String,
        type: Int,
        channelId: String,
        listener: CreateMessageListener
    ) {
        messageViewModel.createMessage(content, type, channelId)

        messageViewModel.createMessage.observeForever {
            Log.e(TAG, "create success ${it.content} ${it.id}")
            listener.createMessageSuccess(it)
        }
    }

    fun getMessages(
        channelId: String, lastId: String, listener: GetMessagesListener
    ) {

        Log.e(TAG, "start loading messages in channel $channelId $lastId")

        messageViewModel.getMessages(channelId, lastId)

        messageViewModel.listMessages.observeForever {
            if (it != null) {
                listener.getMessagesSuccess(it)
            }
        }
    }

    private fun connectPresence(){

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
