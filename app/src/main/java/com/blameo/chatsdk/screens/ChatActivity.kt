package com.blameo.chatsdk.screens

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.ChatListener
import com.blameo.chatsdk.OnTypingListener
import com.blameo.chatsdk.R
import com.blameo.chatsdk.adapters.MessageAdapter
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.Message
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    lateinit var adapter: MessageAdapter
    lateinit var channel: Channel
    lateinit var chatSdk: BlameoChatSdk
    private val TAG = "CHAT"
    private var allMessages: ArrayList<Message> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        init()

        getMessages("")
    }

    private fun markSeenMessage(message: Message) {

        chatSdk.sendSeenMessageEvent(message.channel_id, message.id, message.author_id,
            object: ChatListener.MarkSeenMessageListener{
                override fun onError(error: String) {

                }
            })

    }

    private fun markReceiveMessage(message: Message) {

        chatSdk.sendReceiveMessageEvent(message.channel_id, message.id, message.author_id,
            object: ChatListener.MarkReceiveMessageListener{
                override fun onError(error: String) {

                }
            })

    }

    private fun getMessages(lastId: String) {

        chatSdk.getMessages(channel.id, lastId, object : ChatListener.GetMessagesListener{
            override fun getMessagesSuccess(messages: ArrayList<Message>) {
                Log.i(TAG, "${channel.id} has ${messages.size} in total")
                if(messages.size > 0){
                    allMessages.addAll(0, messages)
                    adapter.notifyDataSetChanged()
                    if(allMessages[0].seen_at == null)
                        markSeenMessage(allMessages[0])
//                    markReceiveMessage(allMessages[0])
                }
            }
        })
    }

    private fun init() {
        chatSdk = BlameoChatSdk.getInstance()
        channel = intent.getSerializableExtra("CHANNEL") as Channel
        adapter = MessageAdapter(this, allMessages, BlameoChatSdk.getInstance().uId)
        val layoutManager = LinearLayoutManager(this)
        listMessage.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        listMessage.adapter = adapter

        val messageListener = object : ChatListener.CreateMessageListener{
            override fun createMessageSuccess(message: Message) {
                Log.e(TAG, "send message success ${message.content} ${message.id}")
                allMessages.add(message)
                adapter.notifyDataSetChanged()
            }
        }

        btn_send_message.setOnClickListener {
            chatSdk.createMessage(edtMessageContent.text.toString(), 1, channel.id, messageListener)
            edtMessageContent.setText("")
        }

        tvAdd.setOnClickListener {
            if(allMessages.size > 0)  getMessages(allMessages[0].id)
        }

        val handler = Handler()

        chatSdk.addOnTypingListener(object: OnTypingListener{
            override fun onTyping() {
                println("true")
                txtTitle.text = "Someone is typing..."
                handler.postDelayed({
                    txtTitle.text = ""
                },3000)
            }

            override fun onStopTyping() {
                println("false")
                txtTitle.text = ""
                handler.removeCallbacksAndMessages(null)
            }

            override fun onNewMessage(message: Message) {
                println("new message")
                allMessages.add(message)
                adapter.notifyDataSetChanged()
            }
        })

        edtMessageContent.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString().isEmpty()){
                    chatSdk.sendTypingEvent(false, channel.id)
                }else if(p0.toString().length == 1){
                    chatSdk.sendTypingEvent(true, channel.id)
                }
            }
        })
    }
}
