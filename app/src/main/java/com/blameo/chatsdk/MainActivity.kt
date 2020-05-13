package com.blameo.chatsdk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.blachat.BlaChatSDK
import com.blameo.chatsdk.blachat.Callback
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.UserVMStore
import com.blameo.chatsdk.models.bla.BlaChannel
import com.blameo.chatsdk.models.entities.Channel
import com.blameo.chatsdk.models.entities.User
import com.blameo.chatsdk.models.results.UserStatus
import com.blameo.chatsdk.screens.CreateChannelActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var chatSdk: BlaChatSDK
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

        chatSdk = BlaChatSDK.getInstance()
        chatSdk.init(applicationContext, myId, token);

        //call sync message to resent unsent message to server
        adapter = ChannelAdapter(this@MainActivity)
        rv_channels.adapter = adapter
        rv_channels.layoutManager = LinearLayoutManager(this@MainActivity)

        chatSdk.getChannels(null, 20, object: Callback<List<BlaChannel>> {
            override fun onSuccess(result: List<BlaChannel>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onFail(e: Exception?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        btn_create_channel.setOnClickListener {
            startActivity(Intent(this, CreateChannelActivity::class.java))
        }


    }

}
