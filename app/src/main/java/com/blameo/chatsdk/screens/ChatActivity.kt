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
import androidx.recyclerview.widget.RecyclerView
import com.blameo.chatsdk.*
import com.blameo.chatsdk.adapters.MessageAdapter
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.events.CursorEvent
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.models.pojos.User
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

        chatSdk.getMessages(channel.id, lastId, object : ChatListener.GetMessagesListener {
            override fun getMessagesSuccess(messages: ArrayList<Message>) {
                Log.i(TAG, "${channel.id} has ${messages.size} in total")
                messages.forEach {
                    Log.i(TAG, "${it.content}")
                }

                if (messages.size > 0) {
                    val pos = allMessages.size - 1
                    isLoading = false
                    markSeenMessage(messages[0])
                    allMessages.addAll(0, messages)
                    adapter.notifyDataSetChanged()
                    Handler().postDelayed({
                        if (pos > 0)
                            listMessage.smoothScrollToPosition(messages.size)
                        }, 1
                    )

//                    if (allMessages[0].seen_at == null)
//                        markSeenMessage(allMessages[0])
                }
            }
        })
    }

    private fun init() {

        chatSdk = BlameoChatSdk.getInstance()
        channel = intent.getSerializableExtra("CHANNEL") as Channel
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
                Log.e(TAG, "send message success ${message.content} ${message.id}")
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
                Intent(this, AboutChannelActivity::class.java)
                    .putExtra("CHANNEL", channel))
        }

        val handler = Handler()

        chatSdk.addOnTypingListener(object : OnTypingListener {
            override fun onTyping() {
               Log.i(TAG," typingaddad")
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
                println("stop typing")
                Log.i(TAG," stop typingaddad")
                runOnUiThread {
                    txtTyping.visibility = View.GONE
                }
                handler.removeCallbacksAndMessages(null)
            }

            override fun onNewMessage(message: Message) {
                println("client got new message")
                runOnUiThread {
                    allMessages.add(message)
                    adapter.notifyDataSetChanged()
                    listMessage.smoothScrollToPosition(allMessages.size - 1)
                    markSeenMessage(message)
                }

            }
        })

        chatSdk.getUsersInChannel(channel.id, object : ChatListener.GetUsersInChannelListener {
            override fun onGetUsersByIdsSuccess(channelId: String, users: ArrayList<User>) {
                val map: HashMap<String, User> = hashMapOf()
                users.forEach {
                    map[it.id] = it
                }
                adapter.users = map
                if (channel.lastMessage != null)
                    getMessages("")
                users.forEachIndexed { index, it ->
                    Log.e(TAG, "users in channel: $index ${it.name}")
                }
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

        listMessage.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(!isLoading){
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if(layoutManager.findFirstCompletelyVisibleItemPosition() == 0){
                        isLoading = true
                        loadMoreMessages()
                    }
                }
            }
        })

        chatSdk.addOnCursorChangeListener(object : OnCursorChangeListener{
            override fun onCursorChange(type: String, cursor: CursorEvent) {
                Log.i(TAG, "type: $type ${cursor.channel_id}")
                if(channel.id != cursor.channel_id) return
                if(type == "SEEN"){
                    allMessages.forEach {
                        if(it.id == cursor.message_id){
                            it.seenAt = ChatSdkDateFormatUtil.parse(cursor.time)
                            adapter.notifyDataSetChanged()
                            return@forEach
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
