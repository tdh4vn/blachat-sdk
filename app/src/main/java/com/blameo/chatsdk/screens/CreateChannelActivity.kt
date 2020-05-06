package com.blameo.chatsdk.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.BlameoChatSdk
import com.blameo.chatsdk.ChatListener
import com.blameo.chatsdk.R
import com.blameo.chatsdk.adapters.MemberAdapter
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.User
import kotlinx.android.synthetic.main.activity_create_channel.*

class CreateChannelActivity : AppCompatActivity() {

    lateinit var adapter: MemberAdapter
    lateinit var chatSdk: BlameoChatSdk
    private val TAG = "CREATE"
    private val uIds: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_channel)

        init()
    }

    private fun init() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        chatSdk = BlameoChatSdk.getInstance()

        chatSdk.getAllMembers(object : ChatListener.GetAllMembersListener{
            override fun onSuccess(users: ArrayList<User>) {
                adapter = MemberAdapter(this@CreateChannelActivity, users, BlameoChatSdk.getInstance().uId, 2)
                adapter.setListener(object : MemberAdapter.SelectUserListener{
                    override fun onAdd(id: String) {
                        uIds.add(id)
                        Log.i(TAG, "add $id ${uIds.size}")
                    }

                    override fun onRemove(id: String) {
                        uIds.remove(id)
                        Log.i(TAG, "remove : $id ${uIds.size}")
                    }
                })
                val layoutManager = LinearLayoutManager(this@CreateChannelActivity)
                rv_members.layoutManager = layoutManager
                layoutManager.stackFromEnd = true
                rv_members.adapter = adapter
                users.forEachIndexed { index, it ->
                    Log.e(TAG, "users in channel: $index ${it.name}")
                }
            }
        })

        btn_create_channel.setOnClickListener {
            if(uIds.size == 0)  return@setOnClickListener
            if(uIds.size == 1){
                createChannel("")
            }else
                showDialog()
        }

    }

    private fun showDialog(){

        val editText = EditText(this)
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        editText.layoutParams = lp

        val dialog = AlertDialog.Builder(this)
            .setTitle("Set conversation name")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, which ->
                createChannel(editText.text.toString().trim())
            }
            .setNegativeButton("Cancel") { dialog, which ->

            }

        dialog.setView(editText)

        dialog.show()
    }

    private fun createChannel(name: String){

        chatSdk.createChannel(uIds, name,
            1, object: ChatListener.CreateChannelListener{
                override fun createChannelSuccess(channel: Channel) {
                    Log.e(TAG, "create channel id success: ${channel.id} ${channel.name}")
                    ChannelVMlStore.getInstance().addNewChannel(channel)
                    finish()
                }
            })
    }
}
