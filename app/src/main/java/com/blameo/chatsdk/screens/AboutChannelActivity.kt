package com.blameo.chatsdk.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.ChatListener
import com.blameo.chatsdk.R
import com.blameo.chatsdk.adapters.MemberAdapter
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.User
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_about_channel.*
import kotlinx.android.synthetic.main.activity_about_channel.toolbar

class AboutChannelActivity : AppCompatActivity() {

    lateinit var adapter: MemberAdapter
    lateinit var chatSdk: BlameoChatSdk
    private val TAG = "MEMBER"
    lateinit var channel: Channel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_channel)

        init()

        getMembers()
    }

    private fun getMembers() {

        chatSdk.getUsersInChannel(channel.id, object : ChatListener.GetUsersInChannelListener {
            override fun onGetUsersByIdsSuccess(channelId: String, users: ArrayList<User>) {

                adapter = MemberAdapter(this@AboutChannelActivity, users, BlameoChatSdk.getInstance().uId, 1)
                val layoutManager = LinearLayoutManager(this@AboutChannelActivity)
                rv_members.layoutManager = layoutManager
                layoutManager.stackFromEnd = true
                rv_members.adapter = adapter
                users.forEachIndexed { index, it ->
                    Log.e(TAG, "users in channel: $index ${it.name}")
                }
            }
        })
    }

    private fun init() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.title = "Members"
        chatSdk = BlameoChatSdk.getInstance()
        channel = intent.getSerializableExtra("CHANNEL") as Channel
        if(!TextUtils.isEmpty(channel.name))
            tvName.text = channel.name
        if(!TextUtils.isEmpty(channel.avatar))
            ImageLoader.getInstance().displayImage(channel.avatar, imgAvatar)

    }
}
