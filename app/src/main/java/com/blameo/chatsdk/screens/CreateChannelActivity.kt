package com.blameo.chatsdk.screens

import android.os.Bundle
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

class CreateChannelActivity : AppCompatActivity() {

    lateinit var adapter: MemberAdapter
    lateinit var chatSdk: BlaChatSDK
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

        chatSdk = BlaChatSDK.getInstance()

        chatSdk.getAllUsers(object : Callback<List<BlaUser>> {
            override fun onSuccess(result: List<BlaUser>?) {
                Log.i(TAG, "ok users " + result?.size)

                runOnUiThread {
                    adapter = MemberAdapter(this@CreateChannelActivity, result!!, "1", 2)
                    adapter.setListener(object : MemberAdapter.SelectUserListener {
                        override fun onAdd(id: String) {
                            uIds.add(id)
                            Log.i(TAG, "add $id ${uIds.size}")
                        }

                        override fun onRemove(id: String) {
                            uIds.remove(id)
                            Log.i(TAG, "remove : $id ${uIds.size}")
                        }
                    })
                    rv_members.layoutManager = LinearLayoutManager(this@CreateChannelActivity)
                    Log.i(TAG, "ok users 3")
                    rv_members.adapter = adapter
                    adapter.notifyDataSetChanged()
                    Log.i(TAG, "ok users 4")
                    result.forEachIndexed { index, it ->
                        Log.e(TAG, "users in channel: $index ${it.name}")
                    }
                }
            }

            override fun onFail(e: Exception?) {

            }
        })

        btn_create_channel.setOnClickListener {
            if (uIds.size == 0) return@setOnClickListener
            if (uIds.size == 1) {
                createChannel("", BlaChannelType.DIRECT)
            } else
                showDialog()
        }

    }

    private fun showDialog() {

        var editText = EditText(this)


        val dialog = AlertDialog.Builder(this)
            .setView(R.layout.custom_create_channel_dialog)
            .setTitle("Set conversation name")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, which ->
                createChannel(editText.text.toString(), BlaChannelType.GROUP)
            }
            .setNegativeButton("Cancel") { dialog, which ->

            }.create()

        dialog.show()

        editText = dialog.findViewById(R.id.edtName)!!
    }

    private fun createChannel(name: String, type: BlaChannelType) {

        chatSdk.createChannel(name, "", uIds, type, object : Callback<BlaChannel> {
            override fun onSuccess(channel: BlaChannel?) {
                Log.e(TAG, "create channel success: ${channel?.id} ${channel?.name}")
                runOnUiThread {
                    ChannelVMlStore.getInstance().addNewChannel(channel!!)
                    finish()
                }

            }

            override fun onFail(e: Exception?) {

            }
        })
    }
}
