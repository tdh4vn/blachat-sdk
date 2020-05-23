package com.blameo.chatsdk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.blachat.BlaChannelEventListener
import com.blameo.chatsdk.blachat.BlaChatSDK
import com.blameo.chatsdk.blachat.BlaMessageListener
import com.blameo.chatsdk.blachat.BlaPresenceListener
import com.blameo.chatsdk.blachat.Callback
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.UserVMStore
import com.blameo.chatsdk.models.bla.BlaChannel
import com.blameo.chatsdk.models.bla.BlaMessage
import com.blameo.chatsdk.models.bla.BlaTypingEvent
import com.blameo.chatsdk.models.bla.BlaUser
import com.blameo.chatsdk.models.entities.Channel
import com.blameo.chatsdk.models.results.UserStatus
import com.blameo.chatsdk.screens.CreateChannelActivity
import com.blameo.chatsdk.utils.DateFormatUtils
import com.blameo.chatsdk.utils.UserSP
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_custom_outcoming_voice_message.view.*
import java.util.*
import kotlin.collections.ArrayList


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
//        chatSdk.getAllMembers(object : ChatListener.GetAllMembersListener{
//            override fun onSuccess(users: ArrayList<User>) {
// //               users.forEach {
////                    Log.i(TAG, "member: ${it.id} ${it.name}")
////                }
//            }
//        })
//
    }

    private fun getUser() {
        myId = intent.getStringExtra("USER_ID")!!
        token = intent.getStringExtra("TOKEN")!!
        tokenWs = intent.getStringExtra("TOKEN_WS")!!
        UserSP.getInstance().id = myId
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
                channelVMStore.addNewChannel(it)
            }
        }

        chatSdk = BlaChatSDK.getInstance()
        chatSdk.init(applicationContext, myId, token)

        //call sync message to resent unsent message to server
        adapter = ChannelAdapter(this@MainActivity)
        rv_channels.adapter = adapter
        rv_channels.layoutManager = LinearLayoutManager(this@MainActivity)

        chatSdk.getChannels(null, 20, object: Callback<List<BlaChannel>> {
            override fun onSuccess(result: List<BlaChannel>?) {
                adapter.channels = result as ArrayList<BlaChannel>

                result.forEach {
                    Log.i(TAG, ""+it.lastMessage)
                }
            }

            override fun onFail(e: Exception?) {

            }

        })

        btn_create_channel.setOnClickListener {
            startActivity(Intent(this, CreateChannelActivity::class.java))
        }

        chatSdk.addPresenceListener { user ->
            val userPresence = userVMStore.getUserViewModel(UserStatus(user?.id, 1))
            userPresence.updateStatus(user.isOnline)
        }

        chatSdk.getUsersPresence(object : Callback<List<BlaUser>>{
            override fun onSuccess(result: List<BlaUser>?) {
                result?.forEach {
                    val userPresence = userVMStore.getUserViewModel(UserStatus(it.id, 1))
                    userPresence.updateStatus(it.isOnline)
                }
            }

            override fun onFail(e: java.lang.Exception?) {

            }
        })


        chatSdk.addMessageListener(object : BlaMessageListener{
            override fun onNewMessage(blaMessage: BlaMessage?) {
                try {
                    val channelVM = channelVMStore.getChannelByID(blaMessage?.channelId!!)
                    channelVM.updateNewMessage(blaMessage)
                }catch (e: java.lang.Exception){
                    e.printStackTrace()
                }

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

        chatSdk.addEventChannelListener(object : BlaChannelEventListener{
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
            }

            override fun onNewChannel(channel: BlaChannel?) {
                adapter.channels.add(0, channel!!)
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
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
    }

}
