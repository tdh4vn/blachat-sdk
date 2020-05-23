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
import com.blameo.chatsdk.*
import com.blameo.chatsdk.adapters.*
import com.blameo.chatsdk.blachat.BlaChannelEventListener
import com.blameo.chatsdk.blachat.BlaChatSDK
import com.blameo.chatsdk.blachat.BlaMessageListener
import com.blameo.chatsdk.blachat.Callback
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.UserVMStore
import com.blameo.chatsdk.models.CustomMessage
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
import java.lang.Exception
import java.util.*


class ChatActivity : AppCompatActivity(), ChatListener.MarkSeenMessageListener,
    MessagesListAdapter.OnLoadMoreListener, TypingListener, MessageInput.AttachmentsListener,
    DialogInterface.OnClickListener, ContentChecker<CustomMessage> {

    lateinit var adapter: MessagesListAdapter<CustomMessage?>
    lateinit var channel: Channel
    lateinit var chatSdk: BlaChatSDK
    private val TAG = "CHAT"
    private var lastMessageId = ""
    private var usersMap: HashMap<String, BlaUser> = hashMapOf()
    private var isLoading = true
    private val defaultImageUrl = "https://picsum.photos/seed/picsum/200/300"
    private var myID = ""
    private var partner: User? = null
    private var count = 0
    private val CONTENT_TYPE_VOICE: Byte = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_demo)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        myID = UserSP.getInstance().id

        init()
    }

    private fun markSeenMessage(message: Message) {
        chatSdk.markSeenMessage(message.id, message.channelId, null)
    }

    private fun getMessages(lastId: String) {

        Log.i(TAG, "load message with id: $lastId")

        chatSdk.getMessages(channel.id, lastId, 20, object : Callback<List<BlaMessage>> {
            override fun onSuccess(result: List<BlaMessage>?) {
                Log.i(TAG, "${channel.id} has ${result?.size} in total")
                val myMessages = arrayListOf<CustomMessage?>()
                result?.forEach { it ->
                    Log.i(TAG, "${it.content} ${it.id} ${it.createdAt} ${it.sentAt}")
                    val customMessage = CustomMessage(it)
                    val user = usersMap[it.authorId]
                    Log.i(TAG, ""+user?.id + " "+user?.avatar)
                    var customUser = CustomUser(user)
                    if(it.isSystemMessage){
                        customUser = CustomUser(usersMap[UserSP.getInstance().id])
                        customMessage.setSystem(
                            CustomMessage.System(
                                "http://example.com",
                                250
                            )
                        )
                    }

//                    var seenBy = "Seen by "
//                    it.seenBy.forEach {
//                        val name = it.name.split(Regex(" "))
//                        seenBy+= name[0]+", "
//                    }
//
//                    Log.e(TAG, "size: ${it.seenBy.size}")
////                    if(it.isSystemMessage)
////                        message.system = com.blameo.chatsdk.models.Message.System("",1)
//                    if(it.seenBy.size > 0)
//                        customMessage.messageStatus = CustomMessage.Status(seenBy.substring(0, seenBy.length - 2))
                    customMessage.myCustomUser = customUser
                    myMessages.add(customMessage)
                }


                isLoading = result?.size!! > 0

                Log.i(TAG, "" + myMessages.size)

                runOnUiThread {
                    adapter.addToEnd(myMessages, false)
                }
                if(myMessages.size > 0)
                    lastMessageId = myMessages[myMessages.size -1]?.id!!
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

        chatSdk.addMessageListener(object : BlaMessageListener {
            override fun onNewMessage(blaMessage: BlaMessage?) {
                Log.i(TAG, "get new message ${blaMessage?.id}")
                addNewMessage(blaMessage!!, 1)
                markSeenMessage(blaMessage)
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
        })

        val handler = Handler()

        chatSdk.addEventChannelListener(object : BlaChannelEventListener {
            override fun onMemberLeave(channel: BlaChannel?, blaUser: BlaUser?) {

            }

            override fun onUserReceiveMessage(
                channel: BlaChannel?,
                user: BlaUser?,
                message: BlaMessage?
            ) {

            }

            override fun onDeleteChannel(channel: BlaChannel?) {

            }

            override fun onTyping(
                channel: BlaChannel?,
                blaUser: BlaUser?,
                blaTypingEvent: BlaTypingEvent?
            ) {
                if (blaTypingEvent == BlaTypingEvent.START) {
                    runOnUiThread {
                        txtTyping.visibility = View.VISIBLE
                    }

                    handler.postDelayed({
                        runOnUiThread {
                            txtTyping.visibility = View.GONE
                        }
                    }, 3000)
                } else {
                    runOnUiThread {
                        txtTyping.visibility = View.GONE
                    }
                    handler.removeCallbacksAndMessages(null)
                }

            }

            override fun onNewChannel(channel: BlaChannel?) {

            }

            override fun onUserSeenMessage(
                channel: BlaChannel?,
                user: BlaUser?,
                message: BlaMessage?
            ) {

            }

            override fun onUpdateChannel(channel: BlaChannel?) {

            }

            override fun onMemberJoin(channel: BlaChannel?, blaUser: BlaUser?) {

            }
        })

        input.setInputListener {
            if (!TextUtils.isEmpty(input.inputEditText.text.toString().trim()))
                chatSdk.createMessage(
                    input.inputEditText.text.toString(),
                    channel.id,
                    BlaMessageType.TEXT,
                    null,
                    object : Callback<BlaMessage> {
                        override fun onSuccess(result: BlaMessage?) {
//                            Log.i(TAG, "create success " + result?.id + result?.createdAt)

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


//        chatSdk.addOnCursorChangeListener(object : OnCursorChangeListener {
//            override fun onCursorChange(type: String, cursor: CursorEvent) {
//                Log.i(TAG, "type: $type ${cursor.channel_id}")
//                if (channel.id != cursor.channel_id) return
//                if (type == "SEEN") {
//                    runOnUiThread {
//                        allMessages.forEach {
//                            if (it.id == cursor.message_id) {
//                                Log.i(TAG, "message id: ${it.id}")
//                                it.seenAt = ChatSdkDateFormatUtil.parse(cursor.time)
//                                val m: com.blameo.chatsdk.models.Message = com.blameo.chatsdk.models.Message(it)
//                                adapter.update(m)
//                                return@forEach
//                            }
//                        }
//                    }
//                }
//            }
//        })
    }

    private fun addNewMessage(message: BlaMessage, type: Int) {
        val m = CustomMessage(message)
        val customUser = CustomUser(usersMap[message.authorId])
        m.myCustomUser = customUser

        if (type == 2)
            m.setImage(CustomMessage.Image(defaultImageUrl))
        else if (type == 3) {
            m.setSystem(
                CustomMessage.System(
                    "http://example.com",
                    250
                )
            )
        }

        runOnUiThread {
            adapter.addToStart(m, true)
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
