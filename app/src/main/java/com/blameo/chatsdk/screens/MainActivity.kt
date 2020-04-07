package com.blameo.chatsdk.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.ChatSdkApplication
import com.blameo.chatsdk.R
import com.blameo.data.models.pojos.Channel
import com.blameo.data.net.CustomInterceptor
import com.blameo.view.screens.BlameoChatSdk
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BlameoChatSdk.GetChannelListener {

    lateinit var chatSdk: BlameoChatSdk
    lateinit var adapter: ChannelAdapter
    private val TAG = "MAIN"

    private val token =
        "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1ZGNjZDk2YWRhNmFkNTAwMDExYzg4N2YiLCJleHAiOjE1OTAzMzg1MDN9.VzmeaH_H-" +
                "ktdeit7xWpavH1ATN6HWVZeBRco0LSyucgxm0lBL8w5yIPxA_hBu9chB4vpcgC0J0tLBZR6PNgs3w"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

    }

    private fun initView() {
        chatSdk = BlameoChatSdk.getInstance()
        chatSdk.swapApplication(ChatSdkApplication.getInstance())
        chatSdk.initViewModel(this)

        btn_get_channel.setOnClickListener {
            chatSdk.getChannels(this)
            btn_get_channel.visibility = View.INVISIBLE
        }
    }

    override fun onChannelChanged(channel: Channel) {
//        adapter = ChannelAdapter(this, arrayListOf())
//        rv_channels.adapter = adapter
//        rv_channels.layoutManager = LinearLayoutManager(this)
//        adapter.notifyDataSetChanged()
        Log.e(TAG, "changed")
//        chatSdk.exportDB()
    }

    override fun onSuccess(channels: ArrayList<Channel>) {
        adapter = ChannelAdapter(this, channels)
        rv_channels.adapter = adapter
        rv_channels.layoutManager = LinearLayoutManager(this)
        adapter.notifyDataSetChanged()
        Log.e(TAG, "success")
    }
}
