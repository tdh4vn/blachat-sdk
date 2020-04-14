package com.blameo.chatsdk

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.models.pojos.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var chatSdk: BlameoChatSdk
    lateinit var adapter: ChannelAdapter
    private val TAG = "MAIN"

    private val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsIjoiJGNoYXQ6ZTk3Y2" +
            "FkMTktYjFhNC00MzY5LTljNDctMzhjODhkMjc2MGFhIiwiY2xpZW50IjoiZTk3Y2FkMTktYjFhNC00MzY5LTljNDctMzhjODhk" +
            "Mjc2MGFhIiwiZXhwIjoxNTg3OTU4ODAyLCJzdWIiOiJlOTdjYWQxOS1iMWE0LTQzNjktOWM0Ny0zOGM4OGQyNzYwYWEiLCJ1c2VyS" +
            "WQiOiJlOTdjYWQxOS1iMWE0LT" +
            "QzNjktOWM0Ny0zOGM4OGQyNzYwYWEifQ.MUpR3vyhypT-_a3qTyUZAiB1WoNXxbhRW8wu2YMFkuk"

    private val tokenWs = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsIjoiJGNoYXQ6YzUwODI2NzAtZDYwYi00YWRjLTk1NDc" +
            "tZDY1OWZmZGM0NjY5IiwiY2xpZW50IjoiYzUwODI2NzAtZDYwYi00YWRjLTk1NDctZDY1OWZmZGM0NjY5IiwiZXhwIjoxNTg5NDMxMDA4LC" +
            "JzdWIiOiJjNTA4MjY3MC1kNjBiLTRhZGMtOTU0Ny1kNjU5ZmZkYzQ2NjkiLCJ1c2VySWQiOiJjNTA4MjY3MC1kNjBiLTRhZGMtOTU0Ny1kN" +
            "jU5ZmZkYzQ2NjkifQ.cf6XdyxrCdZzsvS838FH9n0u4SA6XG2wUUPn-tLlatQ"

    private val channelId = "42618113-fa71-48e6-9d13-8dad2934ae59"
    private val currentUid = "c5082670-d60b-4adc-9547-d659ffdc4669"
    private val userId1 = "7a7c52fe-0a3f-4123-849b-3c2bcabe4f62"
    private val userId2 = "2fc5c3fc-b40b-4497-9772-9dd5b4df8ed7"
    private val IP = "159.65.2.104"
    private val ws = "ws://$IP:8001/connection/websocket?format=protobuf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

    }

    private fun initView() {


        chatSdk = BlameoChatSdk.getInstance()
        chatSdk.initSession(ws, token, tokenWs, currentUid)
        chatSdk.initContext(this)

        btn_get_channel.setOnClickListener {
            chatSdk.getChannels(object: ChatListener.GetChannelsListener{
                override fun onChannelChanged(channel: Channel) {
                    chatSdk.exportChannelDB()
                }

                override fun onGetChannelsSuccess(channels: ArrayList<Channel>) {
//                    Log.e(TAG, "size ${channels.size}")
                    adapter = ChannelAdapter(this@MainActivity, channels)
                    rv_channels.adapter = adapter
                    rv_channels.layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter.notifyDataSetChanged()
                }

            })
            btn_get_channel.visibility = View.INVISIBLE
        }

        btn_get_users.setOnClickListener {
            chatSdk.getUsersInChannel(channelId, object: ChatListener.GetUsersInChannelListener{
                override fun onGetUsersByIdsSuccess(users: ArrayList<User>) {
//                    users.forEachIndexed { index, it ->
//                        Log.e(TAG, "user: $index ${it.name}")
//                    }
                }
            })
        }

        btn_create_channel.setOnClickListener {
            chatSdk.createChannel(arrayListOf(userId1, userId2), "BlameO General",
                1, object: ChatListener.CreateChannelListener{
                    override fun createChannelSuccess(channel: Channel) {
//                        Log.e(TAG, "create channel id: ${channel.id} ${channel.name}")
                    }
                })
        }

        val messageListener = object : ChatListener.CreateMessageListener{
            override fun createMessageSuccess(message: Message) {
//                Log.e(TAG, "create success ${message.content} ${message.id}")
            }
        }

        btn_create_message.setOnClickListener {
            chatSdk.createMessage("Hello ae", 1, channelId, messageListener)
        }

        btn_get_messages.setOnClickListener {
            chatSdk.getMessages(channelId, "", object : ChatListener.GetMessagesListener{
                override fun getMessagesSuccess(messages: ArrayList<Message>) {
                    Log.i(TAG, "$channelId has ${messages.size} in total")
                }
            })
        }
    }

}
