package com.blameo.chatsdk.screens

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blameo.chatsdk.ChatListener
import com.blameo.chatsdk.R
import com.blameo.chatsdk.adapters.*
import com.blameo.chatsdk.blachat.BlaChannelEventListener
import com.blameo.chatsdk.blachat.BlaChatSDK
import com.blameo.chatsdk.blachat.BlaMessageListener
import com.blameo.chatsdk.blachat.Callback
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.UserVMStore
import com.blameo.chatsdk.models.CustomMessage
import com.blameo.chatsdk.models.CustomMessage.MESSAGE_STATUS
import com.blameo.chatsdk.models.CustomUser
import com.blameo.chatsdk.models.bla.*
import com.blameo.chatsdk.models.entities.Channel
import com.blameo.chatsdk.models.entities.Message
import com.blameo.chatsdk.models.entities.User
import com.blameo.chatsdk.models.results.UserStatus
import com.blameo.chatsdk.utils.UserSP
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageHolders.ContentChecker
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessageInput.TypingListener
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.activity_chat.imgAvatar
import kotlinx.android.synthetic.main.activity_chat.imgStatus
import kotlinx.android.synthetic.main.activity_chat.toolbar
import kotlinx.android.synthetic.main.activity_chat.txtTitle
import kotlinx.android.synthetic.main.activity_chat_demo.*
import kotlinx.android.synthetic.main.activity_chat_demo.tvAdd
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity(), ChatListener.MarkSeenMessageListener,
    MessagesListAdapter.OnLoadMoreListener, TypingListener, MessageInput.AttachmentsListener,
    DialogInterface.OnClickListener, ContentChecker<CustomMessage> {

    lateinit var adapter: MessagesListAdapter<CustomMessage?>
    lateinit var channel: Channel
    lateinit var chatSdk: BlaChatSDK
    private val TAG = "CHAT"
    private var lastMessageId = ""
    private var usersMap: HashMap<String, BlaUser> = hashMapOf()
    private var messagesMap: HashMap<String, CustomMessage> = hashMapOf()
    private var isLoading = true
    private val defaultImageUrl = "https://picsum.photos/seed/picsum/200/300"
    private var myID = ""
    private var partner: User? = null
    private var count = 0
    private val CONTENT_TYPE_VOICE: Byte = 1
    private val handler = Handler()
    private val handlerTyping = Handler()
    private var messageListener = object : BlaMessageListener {
        override fun onNewMessage(blaMessage: BlaMessage?) {
            Log.i(TAG, "get new message ${blaMessage?.id}")
            if(blaMessage?.channelId == channel.id){
                addNewMessage(blaMessage!!, 1)
                Log.i(TAG, ""+blaMessage.type + " "+blaMessage.isSystemMessage)
                markSeenMessage(blaMessage)
            }

        }

        override fun onUpdateMessage(blaMessage: BlaMessage?) {

        }

        override fun onDeleteMessage(blaMessage: BlaMessage?) {

        }

        override fun onUserSeen(blaMessage: BlaMessage?, blaUser: BlaUser?, seenAt: Date?) {

        }

        override fun onUserReceive(
            blaMessage: BlaMessage?,
            blaUser: BlaUser?,
            receivedAt: Date?
        ) {

        }
    }

    private val channelListener = object : BlaChannelEventListener {
        override fun onMemberLeave(channel: BlaChannel?, blaUser: BlaUser?) {

        }

        override fun onUserReceiveMessage(
            channel: BlaChannel?,
            user: BlaUser?,
            message: BlaMessage?
        ) {
            Log.i(TAG, "user receive "+channel?.id + " "+ user?.id + " "+message?.id)
            if(this@ChatActivity.channel.id == channel?.id){

                val newMessage = messagesMap[message?.id]
                if(newMessage?.messageStatus == null){
                    newMessage?.messageStatus = CustomMessage.Status("Received by ${user?.name}", null, true)
                }else{
                    if(TextUtils.isEmpty(newMessage.messageStatus.receivedBy))
                        newMessage.messageStatus.receivedBy = "Received by ${user?.name}"
                    else
                    {
                        Log.i(TAG, "user received before "+newMessage.messageStatus.receivedBy)
                        if(!userIsInList(user?.id!!, newMessage.message.receivedBy))
                            newMessage.messageStatus.receivedBy = newMessage.messageStatus.receivedBy.plus(", ${user.name}")
                        Log.i(TAG, "user received after "+newMessage.messageStatus.receivedBy)

                    }
                }

                messagesMap[message?.id!!] = newMessage!!

                handler.post {
                    adapter.update(message.id, newMessage)
                }
            }
        }

        override fun onDeleteChannel(channel: BlaChannel?) {

        }

        override fun onTyping(
            channel: BlaChannel?,
            blaUser: BlaUser?,
            blaTypingEvent: BlaTypingEvent?
        ) {
            if(channel?.id != this@ChatActivity.channel.id) return
            if (blaTypingEvent == BlaTypingEvent.START) {
                handlerTyping.post {
                    txtTyping.visibility = View.VISIBLE
                }

                handlerTyping.postDelayed({
                    runOnUiThread {
                        txtTyping.visibility = View.GONE
                    }
                }, 3000)
            } else {
                handlerTyping.post {
                    txtTyping.visibility = View.GONE
                }
                handlerTyping.removeCallbacksAndMessages(null)
            }

        }

        override fun onNewChannel(channel: BlaChannel?) {

        }

        override fun onUserSeenMessage(
            channel: BlaChannel?,
            user: BlaUser?,
            message: BlaMessage?
        ) {
            if(message?.channelId != channel?.id)   return
            val newMessage = messagesMap[message?.id]
            Log.i(TAG, "user seen "+channel?.id + " "+ user?.id + " "+message?.id+ " "+newMessage?.id+ " "+newMessage?.message?.content)
            if(newMessage?.messageStatus == null){
                newMessage?.messageStatus = CustomMessage.Status(null, "Seen by ${user?.name}", true)
            }else{
                newMessage.messageStatus.isShowing = true
                if(TextUtils.isEmpty(newMessage.messageStatus.seenBy))
                    newMessage.messageStatus.seenBy = "Seen by ${user?.name}"
                else
                {
                    Log.i(TAG, "user seen before "+newMessage.messageStatus.seenBy)
                    if(!userIsInList(user?.id!!, newMessage.message.seenBy))
                        newMessage.messageStatus.seenBy = newMessage.messageStatus.seenBy.plus(", ${user?.name}")
                    Log.i(TAG, "user seen after "+newMessage.messageStatus.seenBy)
                }
            }

            messagesMap[message?.id!!] = newMessage!!

            handler.post {
                adapter.update(message.id, newMessage)
            }
        }

        override fun onUpdateChannel(channel: BlaChannel?) {

        }

        override fun onMemberJoin(channel: BlaChannel?, blaUser: BlaUser?) {

        }
    }

    private fun userIsInList(targetUserId: String, users: ArrayList<BlaUser>): Boolean{
        users.forEach {
            if( targetUserId == it.id)
                return true
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_demo)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
            chatSdk.removeMessageListener(messageListener)
            chatSdk.removeChannelListener(channelListener)
        }

        myID = UserSP.getInstance().id

        init()
    }

    private fun markSeenMessage(message: Message) {
        Log.i(TAG, "mark seen "+message.id + " "+message.content)
        chatSdk.markSeenMessage(message.id, message.channelId, object: Callback<Void>{
            override fun onSuccess(result: Void?) {
            }

            override fun onFail(e: Exception?) {
            }
        })
    }

    private fun getMessages(lastId: String) {

        Log.i(TAG, "load message with id: $lastId")

        chatSdk.getMessages(channel.id, lastId, 20, object : Callback<List<BlaMessage>> {
            override fun onSuccess(result: List<BlaMessage>?) {
                val myMessages = arrayListOf<CustomMessage?>()
                result?.forEach { it ->
                    Log.i(TAG, "${it.content} ${it.id} ${it.createdAt} ${it.sentAt}")
                    val customMessage = CustomMessage(it)
                    val user = usersMap[it.authorId]
                    Log.i(TAG, "" + user?.id + " " + user?.avatar)
                    var customUser = CustomUser(user)
                    if (it.isSystemMessage) {
                        customUser = CustomUser(usersMap[UserSP.getInstance().id])
                        customMessage.setSystem(
                            CustomMessage.System(
                                "http://example.com",
                                250
                            )
                        )
                    }

                    val status = CustomMessage.Status(null, null, false)


                    if(it.authorId == UserSP.getInstance().id){
                        var receivedBy = "Received by "
                        var seenBy = "Seen by "
                        it.receivedBy.forEach {
                            receivedBy+="${it.name}, "
                        }

                        it.seenBy.forEach {
                            seenBy+="${it.name}, "
                        }

                        if(it.receivedBy.size > 0)
                            status.receivedBy = receivedBy.substring(0, receivedBy.length - 2)

                        if(it.seenBy.size > 0)
                            status.seenBy = seenBy.substring(0, seenBy.length - 2)
                    }

                    customMessage.messageStatus = status
                    customMessage.myCustomUser = customUser
                    myMessages.add(customMessage)
                    messagesMap[customMessage.id] = customMessage

                }

                isLoading = result?.size!! > 0

                handler.post {
                    adapter.addToEnd(myMessages, false)
                }
                if(myMessages.size > 0) {
                    lastMessageId = myMessages[myMessages.size -1]?.id!!
                    markSeenMessage(myMessages[0]?.message!!)
                }
            }

            override fun onFail(e: Exception?) {

            }
        })
    }

    private fun init() {


        val imageLoader = ImageLoader { imageView, url, payload ->
            Picasso.with(this@ChatActivity).load(url).into(imageView)
        }

        val holdersConfig = MessageHolders()
        holdersConfig.setIncomingTextConfig(
            CustomIncomingTextMessageViewHolder::class.java,
            R.layout.item_custom_incoming_text_message
        )
        holdersConfig.setOutcomingTextConfig(
            CustomOutcomingTextMessageViewHolder::class.java,
            R.layout.item_custom_outcoming_text_message
        )

        holdersConfig.setIncomingImageConfig(
            CustomIncomingImageMessageViewHolder::class.java,
            R.layout.item_custom_incoming_image_message
        )
        holdersConfig.setOutcomingImageConfig(
            CustomOutcomingImageMessageViewHolder::class.java,
            R.layout.item_custom_outcoming_image_message
        )

        val byte: Byte = 1
        holdersConfig.registerContentType(
            CONTENT_TYPE_VOICE, IncomingVoiceMessageViewHolder::class.java,
            R.layout.item_custom_incoming_voice_message,
            OutcomingVoiceMessageViewHolder::class.java,
            R.layout.item_custom_outcoming_voice_message,
            this
        )

        adapter = MessagesListAdapter<CustomMessage?>(myID, holdersConfig, imageLoader)
        messagesList.setAdapter(adapter)

        chatSdk = BlaChatSDK.getInstance()
        val id = intent.getStringExtra("CHANNEL") ?: ""
        val channelVM = ChannelVMlStore.getInstance().getChannelByID(id)
        channel = channelVM.channel
        val userStatus = UserVMStore.getInstance()
            .getUserViewModel(UserStatus(channelVM.partnerId.value ?: "", 1))
        userStatus.status.observeForever {
            if (it) {
                imgStatus.setBackgroundResource(R.drawable.shape_bubble_online)
            } else
                imgStatus.setBackgroundResource(R.drawable.shape_bubble_offline)
        }

        txtTitle.text = if (!TextUtils.isEmpty(channelVM.channel_name.value))
            channelVM.channel_name.value  else ""
        if (!TextUtils.isEmpty(channelVM.channel_avatar.value))
            com.nostra13.universalimageloader.core.ImageLoader.getInstance()
                .displayImage(channelVM.channel_avatar.value, imgAvatar)

        chatSdk.addMessageListener(messageListener)

        chatSdk.addEventChannelListener(channelListener)

        input.setInputListener {
            if (!TextUtils.isEmpty(input.inputEditText.text.toString().trim()))
                chatSdk.createMessage(
                    input.inputEditText.text.toString(),
                    channel.id,
                    BlaMessageType.TEXT,
                    null,
                    object : Callback<BlaMessage> {
                        override fun onSuccess(result: BlaMessage?) {
                            addNewMessage(result!!, 1)
                            channelVM.updateNewMessage(result)
                        }

                        override fun onFail(e: Exception?) {

                        }
                    })
            true
        }

        input.setTypingListener(this)
        input.setAttachmentsListener(this)
        tvAdd.setOnClickListener {
            startActivity(
                Intent(this, AboutChannelActivity::class.java).putExtra("CHANNEL", channel)
            )
        }

        adapter.setLoadMoreListener(this)
        input.setAttachmentsListener(this)
        adapter.setOnMessageClickListener {

            if (it?.messageStatus != null) {
                it.messageStatus!!.isShowing = !it.messageStatus!!.isShowing
                adapter.update(it)
            }
        }

        chatSdk.getUsersInChannel(channel.id, object : Callback<List<BlaUser>> {
            override fun onSuccess(result: List<BlaUser>?) {
                val map: HashMap<String, BlaUser> = hashMapOf()
                result?.forEach {
                    map[it.id] = it
                    if (it.id != myID && partner == null) {
                        partner = it
                    }
                }

                usersMap = map

                getMessages("")
            }

            override fun onFail(e: Exception?) {

            }
        })
    }

    private fun addNewMessage(message: BlaMessage, type: Int) {
        val m = CustomMessage(message)
        val customUser = CustomUser(usersMap[message.authorId])
        m.myCustomUser = customUser

        if (type == 2)
            m.setImage(CustomMessage.Image(defaultImageUrl))
        else if (type == 3 || message.isSystemMessage) {
            m.setSystem(
                CustomMessage.System(
                    "http://example.com",
                    250
                )
            )
            if(message.isSystemMessage)
                m.myCustomUser = CustomUser(usersMap[UserSP.getInstance().id])
        }

        handler.post {
            adapter.addToStart(m, true)
            messagesMap[m.id] = m
        }

    }

    override fun onSuccess(messageId: String) {

    }

    override fun onError(error: String) {

    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {

        Log.i(TAG, "is loading : $isLoading $lastMessageId")

        if (!isLoading) return
        getMessages(lastMessageId)
    }

    override fun onStartTyping() {
        chatSdk.sendStartTyping(channel.id, object : Callback<Void> {
            override fun onSuccess(result: Void?) {

            }

            override fun onFail(e: Exception?) {

            }
        })
    }

    override fun onStopTyping() {
        chatSdk.sendStopTyping(channel.id, object : Callback<Void> {
            override fun onSuccess(result: Void?) {

            }

            override fun onFail(e: Exception?) {

            }
        })
    }

    override fun onAddAttachments() {

        AlertDialog.Builder(this)
            .setItems(R.array.view_types_dialog, this)
            .show()

    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        val m = BlaMessage(
            "2", myID, "", "", 1,
            Date(), null, null, false, null
        )
        when (which) {
            0 -> {
                if (count % 2 == 0) {
                    m.authorId = partner?.id
                }
                count++

                addNewMessage(m, 2)
            }
            1 -> {
                m.content = "This is a System message"
                addNewMessage(m, 3)
            }
        }


    }

    override fun hasContentFor(customMessage: CustomMessage?, type: Byte): Boolean {
        when (type) {
            CONTENT_TYPE_VOICE -> return customMessage!!.system != null
        }
        return false
    }
}
