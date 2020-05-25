package com.blameo.chatsdk

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.adapters.CustomDialogViewHolder
import com.blameo.chatsdk.blachat.BlaChannelEventListener
import com.blameo.chatsdk.blachat.BlaChatSDK
import com.blameo.chatsdk.blachat.BlaMessageListener
import com.blameo.chatsdk.blachat.Callback
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.UserVMStore
import com.blameo.chatsdk.models.CustomChannel
import com.blameo.chatsdk.models.bla.BlaChannel
import com.blameo.chatsdk.models.bla.BlaMessage
import com.blameo.chatsdk.models.bla.BlaTypingEvent
import com.blameo.chatsdk.models.bla.BlaUser
import com.blameo.chatsdk.models.entities.Channel
import com.blameo.chatsdk.models.results.UserStatus
import com.blameo.chatsdk.screens.ChatActivity
import com.blameo.chatsdk.screens.CreateChannelActivity
import com.blameo.chatsdk.utils.UserSP
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(),  DialogsListAdapter.OnDialogClickListener<CustomChannel>,
DialogsListAdapter.OnDialogLongClickListener<CustomChannel>{

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

        val imageLoader = ImageLoader { imageView, url, _ ->
            if(!TextUtils.isEmpty(url))
                Picasso.with(this).load(url).into(imageView)
            else
                imageView.setImageResource(R.drawable.default_avatar)
        }

        val channelAdapter = DialogsListAdapter<CustomChannel>(
            R.layout.item_custom_dialog_view_holder,
            CustomDialogViewHolder::class.java,
            imageLoader
        )

        val handler = Handler()

        userVMStore = UserVMStore.getInstance()

        val channelVMStore = ChannelVMlStore.getInstance()
        channelVMStore.newChannel.observeForever {
            handler.post {
                channelAdapter.addItem(0, CustomChannel(it))
//                adapter.channels.add(0, it)
//                adapter.notifyDataSetChanged()
                channelVMStore.addNewChannel(it)
            }
        }

        chatSdk = BlaChatSDK.getInstance()

        chatSdk.init(applicationContext, myId, token)




        channelAdapter.setOnDialogClickListener(this)
        channelAdapter.setOnDialogLongClickListener(this)
        dialogsList.setAdapter(channelAdapter)

        //call sync message to resent unsent message to server
//        adapter = ChannelAdapter(this@MainActivity)
//        rv_channels.adapter = adapter
//        rv_channels.layoutManager = LinearLayoutManager(this@MainActivity)

        chatSdk.getChannels(null, 20, object: Callback<List<BlaChannel>> {
            override fun onSuccess(result: List<BlaChannel>?) {
 //               adapter.channels = result as ArrayList<BlaChannel>
                val dialogs = arrayListOf<CustomChannel>()

                result?.forEach {
                    Log.i(TAG, "" + it.lastMessage)
                    dialogs.add(CustomChannel(it))
                }

                channelAdapter.setItems(dialogs)

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
//                    chatSdk.markReceiveMessage(blaMessage.id, blaMessage.channelId, object : Callback<Void>{
//                        override fun onSuccess(result: Void?) {
//
//                        }
//
//                        override fun onFail(e: Exception?) {
//
//                        }
//                    })
                }catch (e: Exception){
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
                Log.i(TAG, "user receive "+channel?.id + " "+ user?.id + " "+message?.id)
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
//                adapter.channels.add(0, channel!!)
                handler.post {
                    channelAdapter.addItem(0, CustomChannel(channel))
                }


            }

            override fun onUserSeenMessage(
                channel: BlaChannel?,
                user: BlaUser?,
                message: BlaMessage?
            ) {
                Log.i(TAG, "user seen "+channel?.id + " "+ user?.id + " "+message?.id)
            }

            override fun onUpdateChannel(channel: BlaChannel?) {

            }

            override fun onMemberJoin(channel: BlaChannel?, blaUser: BlaUser?) {

            }
        })
    }

    override fun onDialogClick(channel: CustomChannel?) {
        startActivity(Intent(this, ChatActivity::class.java)
            .putExtra("CHANNEL", channel?.id))
    }

    override fun onDialogLongClick(dialog: CustomChannel?) {

    }

}
