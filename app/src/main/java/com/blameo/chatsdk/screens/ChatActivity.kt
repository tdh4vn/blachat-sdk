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
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.UserVMStore
import com.blameo.chatsdk.models.events.CursorEvent
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.models.results.UserStatus
import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageHolders.ContentChecker
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessageInput.TypingListener
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.imgAvatar
import kotlinx.android.synthetic.main.activity_chat.imgStatus
import kotlinx.android.synthetic.main.activity_chat.toolbar
import kotlinx.android.synthetic.main.activity_chat.txtTitle
import kotlinx.android.synthetic.main.activity_chat.txtTyping
import kotlinx.android.synthetic.main.activity_chat_demo.*
import kotlinx.android.synthetic.main.activity_chat_demo.tvAdd
import java.util.*


class ChatActivity : AppCompatActivity(), ChatListener.MarkSeenMessageListener,
    MessagesListAdapter.OnLoadMoreListener, TypingListener, MessageInput.AttachmentsListener,
    DialogInterface.OnClickListener, ContentChecker<com.blameo.chatsdk.models.Message>
{

    lateinit var adapter: MessagesListAdapter<com.blameo.chatsdk.models.Message?>
    lateinit var channel: Channel
    lateinit var chatSdk: BlameoChatSdk
    private val TAG = "CHAT"
    private var allMessages: ArrayList<Message> = arrayListOf()
    private var usersMap: HashMap<String, User> = hashMapOf()
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

        myID = BlameoChatSdk.getInstance().uId

        init()
    }

    private fun markSeenMessage(message: Message) {

        chatSdk.sendSeenMessageEvent(message.channelId, message.id, message.authorId, this)

    }

    private fun getMessages(lastId: String) {

        Log.i(TAG, "load message with id: $lastId")

        chatSdk.getMessages(channel.id, lastId, object : ChatListener.GetMessagesListener {
            override fun getMessagesSuccess(messages: ArrayList<Message>) {
                Log.i(TAG, "${channel.id} has ${messages.size} in total")
                val myMessages = arrayListOf<com.blameo.chatsdk.models.Message?>()
                messages.forEach { it ->
                    Log.i(TAG, "${it.content} ${it.id} ${it.createdAtString} ${it.sentAtString} ${it.seenAtString}")
                    val message: com.blameo.chatsdk.models.Message = com.blameo.chatsdk.models.Message(it)
                    val user: com.blameo.chatsdk.models.User = com.blameo.chatsdk.models.User(usersMap[it.authorId])
                    var seenBy = "Seen by "
                    it.seenBy.forEach {
                        val name = it.name.split(Regex(" "))
                        seenBy+= name[0]+", "
                    }

                    Log.e(TAG, "size: ${it.seenBy.size}")
//                    if(it.isSystemMessage)
//                        message.system = com.blameo.chatsdk.models.Message.System("",1)
                    if(it.seenBy.size > 0)
                        message.messageStatus = com.blameo.chatsdk.models.Message.Status(seenBy.substring(0, seenBy.length - 2))
                    message.myUser = user
                    myMessages.add(message)
                }

                if(messages.size > 0)
                    isLoading = true

                adapter.addToEnd(myMessages, true)

                allMessages.addAll(0, messages)
            }

            override fun getMessageFailed(error: String) {
                isLoading = false

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
        holdersConfig.registerContentType(CONTENT_TYPE_VOICE, IncomingVoiceMessageViewHolder::class.java,
            R.layout.item_custom_incoming_voice_message,
            OutcomingVoiceMessageViewHolder::class.java,
            R.layout.item_custom_outcoming_voice_message,
            this)

        adapter = MessagesListAdapter<com.blameo.chatsdk.models.Message?>(myID, holdersConfig, imageLoader)
        messagesList.setAdapter(adapter)

        chatSdk = BlameoChatSdk.getInstance()
        val id = intent.getStringExtra("CHANNEL")?:""
        val channelVM = ChannelVMlStore.getInstance().getChannelByID(id)
        channel = channelVM.channel
        val userStatus = UserVMStore.getInstance().getUserViewModel(UserStatus(channelVM.partnerId.value?:"", 1))
        userStatus.status.observeForever {
            if(it){
                imgStatus.setBackgroundResource(R.drawable.shape_bubble_online)
            }else
                imgStatus.setBackgroundResource(R.drawable.shape_bubble_offline)
        }

        txtTitle.text = if (!TextUtils.isEmpty(channel.name)) channel.name else ""
        if (!TextUtils.isEmpty(channel.avatar))
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(channel.avatar, imgAvatar)

        val messageListener = object : ChatListener.CreateMessageListener {
            override fun createMessageSuccess(message: Message) {
                addNewMessage(message, 1)
            }
        }

        input.setInputListener {
            if(!TextUtils.isEmpty(input.inputEditText.text.toString().trim()))
                chatSdk.createMessage(input.inputEditText.text.toString(), 1, channel.id, messageListener)
            true
        }

        input.setTypingListener(this)
        input.setAttachmentsListener(this)

        tvAdd.setOnClickListener {
            startActivity(
                Intent(this, AboutChannelActivity::class.java).putExtra("CHANNEL", channel)
            )
        }

        val handler = Handler()

        chatSdk.addOnTypingListener(object : OnTypingListener {
            override fun onTyping() {
                runOnUiThread {
                    txtTyping.visibility = View.VISIBLE
                }

                handler.postDelayed({
                    runOnUiThread {
                        txtTyping.visibility = View.GONE
                    }
                }, 3000)
            }
            override fun onStopTyping() {
                runOnUiThread {
                    txtTyping.visibility = View.GONE
                }
                handler.removeCallbacksAndMessages(null)
            }
        })

        chatSdk.addOnEventListener(object : OnEventListener {
            override fun onNewMessage(message: Message) {
                addNewMessage(message, 1)
                markSeenMessage(message)
            }
        })

        chatSdk.getUsersInChannel(channel.id, object : ChatListener.GetUsersInChannelListener {
            override fun onGetUsersByIdsSuccess(channelId: String, users: ArrayList<User>) {
                val map: HashMap<String, User> = hashMapOf()
                users.forEach {
                    map[it.id] = it
                    if(it.id != myID && partner == null){
                        partner = it
                    }
                }
                usersMap = map
                getMessages("")
            }
        })

        adapter.setLoadMoreListener(this)
        input.setAttachmentsListener(this)
        adapter.setOnMessageClickListener {
            if(it?.messageStatus != null){
                it.messageStatus!!.isShowing = !it.messageStatus!!.isShowing
                adapter.update(it)
            }
        }




        chatSdk.addOnCursorChangeListener(object : OnCursorChangeListener {
            override fun onCursorChange(type: String, cursor: CursorEvent) {
                Log.i(TAG, "type: $type ${cursor.channel_id}")
                if (channel.id != cursor.channel_id) return
                if (type == "SEEN") {
                    runOnUiThread {
                        allMessages.forEach {
                            if (it.id == cursor.message_id) {
                                Log.i(TAG, "message id: ${it.id}")
                                it.seenAt = ChatSdkDateFormatUtil.parse(cursor.time)
                                val m: com.blameo.chatsdk.models.Message = com.blameo.chatsdk.models.Message(it)
                                adapter.update(m)
                                return@forEach
                            }
                        }
                    }
                }
            }
        })
    }

    private fun addNewMessage(message: Message, type: Int) {
        allMessages.add(0, message)
        val m: com.blameo.chatsdk.models.Message = com.blameo.chatsdk.models.Message(message)
        val user: com.blameo.chatsdk.models.User = com.blameo.chatsdk.models.User(usersMap[message.authorId])
        m.myUser = user

        if(type == 2)
            m.setImage(com.blameo.chatsdk.models.Message.Image(defaultImageUrl))
        else if(type == 3){
            m.setSystem(
                com.blameo.chatsdk.models.Message.System(
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

        if(!isLoading) return
        if (allMessages.size > 0) getMessages(allMessages[0].id)
    }

    override fun onStartTyping() {
        chatSdk.sendTypingEvent(false, channel.id)
    }

    override fun onStopTyping() {
        chatSdk.sendTypingEvent(true, channel.id)
    }

    override fun onAddAttachments() {

        AlertDialog.Builder(this)
            .setItems(R.array.view_types_dialog, this)
            .show()

    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        val m = Message("2", myID, "", "", 1, "")
        m.sentAt = Date()
        when(which){
            0 -> {

                if(count%2==0){
                    m.authorId = partner?.id
                }
                count++

                addNewMessage(m, 2)
            }
            1 ->{
                m.content = "This is a System message"
                addNewMessage(m, 3)
            }
        }


    }

    override fun hasContentFor(message: com.blameo.chatsdk.models.Message?, type: Byte): Boolean {
        when (type) {
            CONTENT_TYPE_VOICE -> return message!!.system != null
        }
        return false
    }
}
