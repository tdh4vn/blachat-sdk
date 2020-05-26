package com.blameo.chatsdk.screens

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.R
import com.blameo.chatsdk.adapters.MemberAdapter
import com.blameo.chatsdk.blachat.BlaChatSDK
import com.blameo.chatsdk.blachat.Callback
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.models.bla.BlaChannel
import com.blameo.chatsdk.models.bla.BlaChannelType
import com.blameo.chatsdk.models.bla.BlaUser
import com.blameo.chatsdk.utils.UserSP
import kotlinx.android.synthetic.main.activity_create_channel.*
import kotlinx.android.synthetic.main.activity_create_channel.rv_members
import kotlinx.android.synthetic.main.activity_create_channel.toolbar
import kotlinx.android.synthetic.main.activity_invite_users.*

class InviteUsersActivity : AppCompatActivity() {

    lateinit var adapter: MemberAdapter
    lateinit var chatSdk: BlaChatSDK
    private val TAG = "CREATE"
    private val uIds: ArrayList<String> = arrayListOf()
    private var channelId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_users)

        init()

        channelId = intent.getStringExtra("CHANNEL_ID")!!
    }

    private fun init() {

        val handler = Handler()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        chatSdk = BlaChatSDK.getInstance()

        chatSdk.getAllUsers(object : Callback<List<BlaUser>> {
            override fun onSuccess(result: List<BlaUser>?) {

                handler.post {
                    adapter = MemberAdapter(
                        this@InviteUsersActivity,
                        result!!,
                        UserSP.getInstance().id,
                        2
                    )
                    adapter.setListener(object : MemberAdapter.SelectUserListener {
                        override fun onAdd(id: String) {
                            uIds.add(id)
                        }

                        override fun onRemove(id: String) {
                            uIds.remove(id)
                        }
                    })
                    rv_members.layoutManager = LinearLayoutManager(this@InviteUsersActivity)
                    rv_members.adapter = adapter
                }
            }

            override fun onFail(e: Exception?) {

            }
        })

        btn_done.setOnClickListener {
            if (uIds.size > 0)
                inviteUsersToChannel()
        }
    }

    private fun inviteUsersToChannel() {
        chatSdk.inviteUserToChannel(uIds, channelId, object : Callback<Void>{
            override fun onSuccess(result: Void?) {
                Log.i(TAG, "invite success")
                finish()
            }

            override fun onFail(e: Exception?) {
                e?.printStackTrace()
            }
        })
    }
}
