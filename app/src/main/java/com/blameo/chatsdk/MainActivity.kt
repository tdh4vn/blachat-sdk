package com.blameo.chatsdk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.UserVMStore
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.models.results.UserStatus
import com.blameo.chatsdk.screens.CreateChannelActivity
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
    private val baseUrl = "http://$IP"
    private val ws = "ws://$IP:8001/connection/websocket?format=protobuf"
    private var channels: ArrayList<Channel> = arrayListOf()
    lateinit var userVMStore: UserVMStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getUser()

        init()

        getMembers()

    }

    private fun getMembers() {
        chatSdk.getAllMembers(object : ChatListener.GetAllMembersListener{
            override fun onSuccess(users: ArrayList<User>) {
 //               users.forEach {
//                    Log.i(TAG, "member: ${it.id} ${it.name}")
//                }
            }
        })

        chatSdk.subcribeUserStatusListener(object : ChatListener.UserStatusChangeListener{
            override fun onStatusChanged(userStatus: UserStatus) {
                Log.e(TAG, "user changed: ${userStatus.id} ${userStatus.status}")
                val user = userVMStore.getUserViewModel(userStatus)
                user.updateStatus(userStatus.status)
            }
        })
    }

    private fun getUser() {
        myId = intent.getStringExtra("USER_ID")!!
        token = intent.getStringExtra("TOKEN")!!
        tokenWs = intent.getStringExtra("TOKEN_WS")!!
    }


    private fun init() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        userVMStore = UserVMStore.getInstance()

        val channelVMStore = ChannelVMlStore.getInstance()
        channelVMStore.newChannel.observeForever {
            runOnUiThread {
                adapter.channels.add(0, it)
                adapter.notifyDataSetChanged()
            }
        }

        chatSdk = BlameoChatSdk.getInstance()
        chatSdk.initContext(this)
        chatSdk.initSession(baseUrl, ws, token, tokenWs, myId)

        //call sync message to resent unsent message to server
        chatSdk.syncMessage()

        adapter = ChannelAdapter(this@MainActivity)
        rv_channels.adapter = adapter
        rv_channels.layoutManager = LinearLayoutManager(this@MainActivity)

        chatSdk.getChannels(object: ChatListener.GetChannelsListener{
            override fun onChannelChanged(channel: Channel) {

            }

            override fun onGetChannelsSuccess(channels: ArrayList<Channel>) {
                Log.e(TAG, "size ${channels.size}")

                adapter.channels.addAll(channels)
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
            startActivity(Intent(this, CreateChannelActivity::class.java))
        }


    }

}
