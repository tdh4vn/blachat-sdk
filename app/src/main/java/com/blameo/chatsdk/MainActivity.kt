package com.blameo.chatsdk

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.models.pojos.Channel
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

    private val tokenWs = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsIjoiJGNoYXQ6ZTk3Y2FkMTktYjFhNC00MzY5LTljNDctMzhjODhkMjc2MGFhIiwiY2xpZW50IjoiZTk3Y2FkMTktYjFhNC00MzY5LTljNDctMzhjODhkMjc2MGFhIiwiZXhwIjoxNTg5NjUzMjY2LCJzdWIiOiJlOTdjYWQxOS1iMWE0LTQzNjktOWM0Ny0zOGM4OGQyNzYwYWEiLCJ1c2VySWQiOiJlOTdjYWQxOS1iMWE0LTQzNjktOWM0Ny0zOGM4OGQyNzYwYWEifQ.1g799Cka7FBMyflB1sEjP2WYnA99rdJEYWci8_z_a2U"

    private val channelId = "42618113-fa71-48e6-9d13-8dad2934ae59"
    private val myId = "e97cad19-b1a4-4369-9c47-38c88d2760aa"
    private val userId1 = "7a7c52fe-0a3f-4123-849b-3c2bcabe4f62"
    private val userId2 = "2fc5c3fc-b40b-4497-9772-9dd5b4df8ed7"
    private val IP = "159.65.2.104"
    private val baseUrl = "http://$IP:9000"
    private val ws = "ws://$IP:8001/connection/websocket?format=protobuf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

    }



    private fun initView() {


        chatSdk = BlameoChatSdk.getInstance()
        chatSdk.initContext(this)
        chatSdk.initSession(baseUrl, ws, token, tokenWs, myId)

//        chatSdk.exportChannelDB()

        chatSdk.getChannels(object: ChatListener.GetChannelsListener{
            override fun onChannelChanged(channel: Channel) {

            }

            override fun onGetChannelsSuccess(channels: ArrayList<Channel>) {
                Log.e(TAG, "size ${channels.size}")
                adapter = ChannelAdapter(this@MainActivity, channels)
                rv_channels.adapter = adapter
                rv_channels.layoutManager = LinearLayoutManager(this@MainActivity)
                adapter.notifyDataSetChanged()
            }

        })
        btn_get_channel.visibility = View.INVISIBLE


        btn_get_channel.setOnClickListener {

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


    }

}
