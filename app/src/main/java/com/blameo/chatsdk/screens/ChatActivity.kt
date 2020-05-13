package com.blameo.chatsdk.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.blameo.chatsdk.*
import com.blameo.chatsdk.adapters.MessageAdapter
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.UserVMStore
import com.blameo.chatsdk.models.events.CursorEvent
import com.blameo.chatsdk.models.entities.Channel
import com.blameo.chatsdk.models.entities.Message
import com.blameo.chatsdk.models.entities.User
import com.blameo.chatsdk.models.results.UserStatus
import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity(), ChatListener.MarkSeenMessageListener {

    lateinit var adapter: MessageAdapter
    lateinit var channel: Channel
    lateinit var chatSdk: BlameoChatSdk
    private val TAG = "CHAT"
    private var allMessages: ArrayList<Message> = arrayListOf()
    private var isLoading = true

    lateinit var smoothScroller: SmoothScroller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        init()
    }

    private fun markSeenMessage(message: Message) {

        chatSdk.sendSeenMessageEvent(message.channelId, message.id, message.authorId, this)

    }

    private fun getMessages(lastId: String) {

        Log.i(TAG, "load message with id: $lastId")
        progressBar.visibility = View.VISIBLE

        chatSdk.getMessages(channel.id, lastId, object : ChatListener.GetMessagesListener {
            override fun getMessagesSuccess(messages: ArrayList<Message>) {
                Log.i(TAG, "${channel.id} has ${messages.size} in total")
                messages.forEach {
                    Log.i(TAG, "${it.content} ${it.id} ${it.createdAtString} ${it.sentAtString} ${it.seenAtString}")
                }


                Handler().postDelayed({
                    progressBar.visibility = View.GONE

                    if (messages.size > 0) {
                        isLoading = false
                        markSeenMessage(messages[messages.size - 1])
                        allMessages.addAll(0, messages)
                        adapter.notifyItemRangeInserted(0, messages.size)



                        Handler().postDelayed({
                            (listMessage.layoutManager as LinearLayoutManager).smoothScrollToPosition(listMessage, null, messages.size)
//                            smoothScroller.targetPosition = messages.size - 1
//                            (listMessage.layoutManager as LinearLayoutManager).startSmoothScroll(smoothScroller)

                        }, 100
                        )
                    }
                }, 1000)
            }

            override fun getMessageFailed(error: String) {
                Handler().postDelayed({
                    progressBar.visibility = View.GONE
                }, 1000)

            }
        })
    }

    private fun init() {

        smoothScroller = object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        chatSdk = BlameoChatSdk.getInstance()
        val id = intent.getStringExtra("CHANNEL")?:""
        val channelVM = ChannelVMlStore.getInstance().getChannelByID(id)
        channel = channelVM.channel
        val userStatus = UserVMStore.getInstance().getUserViewModel(UserStatus(channelVM.partnerId.value?:"", 1))
        userStatus.status.observeForever {
            if(it){
                imgStatus.setColorFilter(this.resources.getColor(android.R.color.holo_green_light))
            }else
                imgStatus.setColorFilter(this.resources.getColor(android.R.color.holo_red_light))
        }

        txtTitle.text = if (!TextUtils.isEmpty(channel.name)) channel.name else ""
        if (!TextUtils.isEmpty(channel.avatar))
            ImageLoader.getInstance().displayImage(channel.avatar, imgAvatar)
        adapter = MessageAdapter(this, allMessages, BlameoChatSdk.getInstance().uId)
        val layoutManager = LinearLayoutManager(this)
        listMessage.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        listMessage.adapter = adapter

        val messageListener = object : ChatListener.CreateMessageListener {
            override fun createMessageSuccess(message: Message) {
                allMessages.add(message)
                adapter.notifyDataSetChanged()
                listMessage.smoothScrollToPosition(allMessages.size - 1)
            }
        }

        btn_send_message.setOnClickListener {
            chatSdk.createMessage(edtMessageContent.text.toString(), 1, channel.id, messageListener)
            edtMessageContent.setText("")
        }

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
                Log.i(TAG, " stop typing")
                runOnUiThread {
                    txtTyping.visibility = View.GONE
                }
                handler.removeCallbacksAndMessages(null)
            }
        })

        chatSdk.addOnEventListener(object : OnEventListener {
            override fun onNewMessage(message: Message) {
                println("client got new message")
                runOnUiThread {
                    Handler().postDelayed({
                        allMessages.add(message)
                        adapter.notifyDataSetChanged()
                        Handler().postDelayed({
                            listMessage.smoothScrollToPosition(allMessages.size - 1)
                        }, 10)

                    }, 100)
                }
                markSeenMessage(message)
            }
        })

        chatSdk.getUsersInChannel(channel.id, object : ChatListener.GetUsersInChannelListener {
            override fun onGetUsersByIdsSuccess(channelId: String, users: ArrayList<User>) {
                val map: HashMap<String, User> = hashMapOf()
                users.forEach {
                    map[it.id] = it
                }
                adapter.users = map
                getMessages("")
//                users.forEachIndexed { index, it ->
//                    Log.e(TAG, "users in channel: $index ${it.name} ${it.id}")
//                }
            }
        })

        edtMessageContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) {
                    chatSdk.sendTypingEvent(false, channel.id)
                } else if (p0.toString().length == 1) {
                    chatSdk.sendTypingEvent(true, channel.id)
                }
            }
        })

        listMessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!isLoading) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        isLoading = true
                        loadMoreMessages()
                    }
                }
            }
        })

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
                                adapter.notifyDataSetChanged()
                                return@forEach
                            }
                        }
                    }

                }
            }
        })


    }

    fun loadMoreMessages() {
        if (allMessages.size > 0) getMessages(allMessages[0].id)
    }

    override fun onSuccess(messageId: String) {

    }

    override fun onError(error: String) {

    }
}
