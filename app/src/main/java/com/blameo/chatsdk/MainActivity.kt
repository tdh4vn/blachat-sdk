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

    private var token = ""

    private var tokenWs = ""

    private var myId = ""
    private val userId1 = "7a7c52fe-0a3f-4123-849b-3c2bcabe4f62"
    private val IP = "159.65.2.104"
    private val baseUrl = "http://$IP:9000"
    private val ws = "ws://$IP:8001/connection/websocket?format=protobuf"
    private var channels: ArrayList<Channel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getUser()

        init()

    }

    private fun getUser() {
        myId = intent.getStringExtra("USER_ID")!!
        token = intent.getStringExtra("TOKEN")!!
        tokenWs = intent.getStringExtra("TOKEN_WS")!!
    }


    private fun init() {

        setSupportActionBar(toolbar)
        supportActionBar?.title = "ChatSDK Demo"
        chatSdk = BlameoChatSdk.getInstance()
        chatSdk.initContext(this)
        chatSdk.initSession(baseUrl, ws, token, tokenWs, myId)

        //call sync message to resent unsent message to server
        chatSdk.syncMessage()

//        chatSdk.exportChannelDB()

        adapter = ChannelAdapter(this@MainActivity)
        rv_channels.adapter = adapter
        rv_channels.layoutManager = LinearLayoutManager(this@MainActivity)

        chatSdk.getChannels(object: ChatListener.GetChannelsListener{
            override fun onChannelChanged(channel: Channel) {

            }

            override fun onGetChannelsSuccess(channels: ArrayList<Channel>) {
                adapter.channels = channels
                adapter.notifyDataSetChanged()
            }

        })
        chatSdk.addOnNewChannelListener(object : OnNewChannelListener{
            override fun onNewChannel(channel: Channel) {
                Log.i(TAG, "new channel id: ${channel.id}")
                adapter.channels.add(0, channel)
                adapter.notifyDataSetChanged()
            }
        })

        btn_create_channel.setOnClickListener {
            chatSdk.createChannel(arrayListOf(userId1), "BlameO General",
                1, object: ChatListener.CreateChannelListener{
                    override fun createChannelSuccess(channel: Channel) {
                        adapter.channels.add(0, channel)
                        adapter.notifyDataSetChanged()
                    }
                }
            )
        }
    }
}
