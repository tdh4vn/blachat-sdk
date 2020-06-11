package com.blameo.chatsdk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.blameo.chatsdk.adapters.CustomDialogViewHolder
import com.blameo.chatsdk.blachat.ChannelEventListener
import com.blameo.chatsdk.blachat.BlaChatSDK
import com.blameo.chatsdk.blachat.MessagesListener
import com.blameo.chatsdk.blachat.Callback
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.UserVMStore
import com.blameo.chatsdk.models.CustomChannel
import com.blameo.chatsdk.models.bla.*
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
    lateinit var channelAdapter: DialogsListAdapter<CustomChannel>
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
    lateinit var handler: Handler

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

        channelAdapter = DialogsListAdapter<CustomChannel>(
            R.layout.item_custom_dialog_view_holder,
            CustomDialogViewHolder::class.java,
            imageLoader
        )

        handler = Handler()

        userVMStore = UserVMStore.getInstance()

        val channelVMStore = ChannelVMlStore.getInstance()
        channelVMStore.newChannel.observeForever {
            handler.post {
                channelAdapter.addItem(0, CustomChannel(it))
                channelVMStore.addNewChannel(it)
            }
        }

        chatSdk = BlaChatSDK.getInstance()

        chatSdk.init(applicationContext, myId, token)

        channelAdapter.setOnDialogClickListener(this)
        channelAdapter.setOnDialogLongClickListener(this)
        dialogsList.setAdapter(channelAdapter)

        chatSdk.getChannels("", 20, object: Callback<List<BlaChannel>> {
            override fun onSuccess(result: List<BlaChannel>?) {
                val dialogs = arrayListOf<CustomChannel>()

                result?.forEach {
                    Log.i(TAG, "" + it.lastMessage)
                    if(it.lastMessage != null){
                        Log.i(TAG, "author: "+it.lastMessage.author.name)
                    }
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
            val userPresence = userVMStore.getUserViewModel(UserStatus(user?.blaUser?.id, 1))
            userPresence.updateStatus(user.blaUser.isOnline)
        }

        chatSdk.getUserPresence(object : Callback<List<BlaUserPresence>>{
            override fun onSuccess(result: List<BlaUserPresence>?) {
                result?.forEach {
                    val userPresence = userVMStore.getUserViewModel(UserStatus(it.blaUser.id, 1))
                    userPresence.updateStatus(it.blaUser.isOnline)
                }
            }

            override fun onFail(e: java.lang.Exception?) {

            }
        })

        chatSdk.addMessageListener(object : MessagesListener {
            override fun onNewMessage(blaMessage: BlaMessage?) {
                try {
                    val channelVM = channelVMStore.getChannelByID(blaMessage?.channelId!!)
                    channelVM.updateNewMessage(blaMessage, false)
                    chatSdk.markReceiveMessage(blaMessage.id, blaMessage.channelId, blaMessage.authorId, object : Callback<Boolean>{
                        override fun onSuccess(result: Boolean?) {

                        }

                        override fun onFail(e: Exception?) {

                        }
                    })
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

        chatSdk.addChannelListener(object :
            ChannelEventListener {
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
                eventType: EventType?
            ) {
            }

            override fun onNewChannel(channel: BlaChannel?) {
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

    override fun onDialogLongClick(channel: CustomChannel?) {

        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete channel")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, which ->
                deleteChannel(channel?.channel)
            }
            .setNegativeButton("Cancel") { dialog, which ->

            }.create()

        dialog.show()
    }

    private fun deleteChannel(channel: BlaChannel?) {

        chatSdk.deleteChannel(channel!!, object : Callback<BlaChannel>{
            override fun onSuccess(result: BlaChannel?) {
                handler.post {
                    channelAdapter.deleteById(result?.id)
                }
            }

            override fun onFail(e: java.lang.Exception?) {

            }
        })
    }

}
