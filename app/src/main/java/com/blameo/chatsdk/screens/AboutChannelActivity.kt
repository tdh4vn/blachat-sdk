package com.blameo.chatsdk.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.ChatListener
import com.blameo.chatsdk.R
import com.blameo.chatsdk.adapters.MemberAdapter
import com.blameo.chatsdk.blachat.BlaChatSDK
import com.blameo.chatsdk.blachat.Callback
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.ConversationViewModel
import com.blameo.chatsdk.models.bla.BlaUser
import com.blameo.chatsdk.models.entities.Channel
import com.blameo.chatsdk.models.entities.User
import com.blameo.chatsdk.utils.UserSP
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_about_channel.*
import kotlinx.android.synthetic.main.activity_about_channel.toolbar
import java.lang.Exception

class AboutChannelActivity : AppCompatActivity() {

    lateinit var adapter: MemberAdapter
    lateinit var chatSdk: BlaChatSDK
    private val TAG = "MEMBER"
    private var channelVM: ConversationViewModel? = null
    lateinit var channel: Channel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_channel)

        init()

        getMembers()
    }

    private fun getMembers() {

        if (channelVM?.memmbers == null)
            chatSdk.getUsersInChannel(channel.id, object : Callback<List<BlaUser>> {
                override fun onSuccess(result: List<BlaUser>?) {
                    initAdapter(result!!)
                }

                override fun onFail(e: Exception?) {

                }
            })
        else
            initAdapter(channelVM?.memmbers!!)
    }


    private fun initAdapter(users: List<BlaUser>) {
        adapter = MemberAdapter(this@AboutChannelActivity, users, UserSP.getInstance().id, 1)
        val layoutManager = LinearLayoutManager(this@AboutChannelActivity)
        rv_members.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        rv_members.adapter = adapter
    }

    private fun init() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        supportActionBar?.title = "Members"
        chatSdk = BlaChatSDK.getInstance()
        channel = intent.getSerializableExtra("CHANNEL") as Channel
        channelVM = ChannelVMlStore.getInstance().getChannelByID(channel.id)
        if (!TextUtils.isEmpty(channelVM?.channel_name?.value))
            tvName.text = channelVM?.channel_name?.value
        if (!TextUtils.isEmpty(channelVM?.channel_avatar?.value))
            ImageLoader.getInstance().displayImage(channelVM?.channel_avatar?.value, imgAvatar)

        btn_invite.setOnClickListener {
            startActivity(Intent(this, InviteUsersActivity::class.java)
                .putExtra("CHANNEL_ID", channel.id))
        }

    }
}
