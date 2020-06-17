package com.blameo.chatsdk.screens

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
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
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.model.Image
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_about_channel.*
import kotlinx.android.synthetic.main.activity_create_channel.*
import kotlinx.android.synthetic.main.activity_create_channel.rv_members
import kotlinx.android.synthetic.main.activity_create_channel.toolbar
import me.gujun.android.taggroup.TagGroup
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CreateChannelActivity : AppCompatActivity() {

    lateinit var adapter: MemberAdapter
    lateinit var chatSdk: BlaChatSDK
    private val TAG = "CREATE"
    private val users: ArrayList<BlaUser> = arrayListOf()
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private var urlAvatar = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_channel)

        init()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun init() {

        val handler = Handler()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        chatSdk = BlaChatSDK.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child("channelAvatars").child("images")

        chatSdk.getAllUsers(object : Callback<List<BlaUser>> {
            override fun onSuccess(result: List<BlaUser>?) {

                handler.post {
                    adapter = MemberAdapter(this@CreateChannelActivity, result!!, UserSP.getInstance().id, 2, true)
                    adapter.setListener(object : MemberAdapter.SelectUserListener {
                        override fun onAdd(id: BlaUser) {
                            users.add(id)
                            setNameTags()
                            Log.i(TAG, "add "+users.size)
                        }

                        override fun onRemove(id: BlaUser) {
                            users.remove(id)
                            Log.i(TAG, "remove "+users.size)
                            setNameTags()
                        }
                    })
                    rv_members.layoutManager = LinearLayoutManager(this@CreateChannelActivity)
                    rv_members.adapter = adapter
                }
            }

            override fun onFail(e: Exception?) {

            }
        })

        tvDone.setOnClickListener {
            if (users.size == 0) return@setOnClickListener
            if (users.size == 1) {
                createChannel("", BlaChannelType.DIRECT)
            } else
                createGroupChannel()
//                showDialog()
        }

    }

    private fun setNameTags(){
        val names = arrayListOf<String>()
        users.forEach {
            names.add(it.name)
            Log.i(TAG, "name: "+it.name)
        }
        Log.i(TAG, "names size: "+names.size)
//        mTagGroup.setTags(names)

        mTagGroup.setTags(names.toList())
    }

    private fun createGroupChannel(){
        showNextView()
    }

    private fun showNextView() {
        setInfoChannelView.visibility = View.VISIBLE
        addMembersView.visibility = View.GONE
        tvDone.visibility = View.GONE
        btn_done.visibility = View.VISIBLE
        btn_done.setOnClickListener {
            if(!TextUtils.isEmpty(edtConversationName.text.toString()))
                createChannel(edtConversationName.text.toString(), BlaChannelType.GROUP)
        }
        imgChannelAvatar.setOnClickListener {
            openGallery()
        }

        adapter = MemberAdapter(this, users, "", 1, false)
        rv_members.adapter = adapter


    }

    private fun openGallery() {

        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL)
            .toolbarImageTitle("Tap to select")
            .toolbarArrowColor(Color.BLACK)
            .includeVideo(false)
            .single()
            .limit(1)
            .showCamera(true)
            .enableLog(false)
            .start()
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

        val uIds = arrayListOf<String>()
        users.forEach {
            uIds.add(it.id)
        }

        val map = HashMap<String, Any>()
        map["customize"] = "Nam test custom data create channel"

        chatSdk.createChannel(name, uIds, type, map, object : Callback<BlaChannel> {
            override fun onSuccess(channel: BlaChannel?) {
                Log.e(TAG, "create channel success: ${channel?.id} ${channel?.name} $urlAvatar")
                runOnUiThread {
                    ChannelVMlStore.getInstance().addNewChannel(channel!!)
                    finish()
                }

            }

            override fun onFail(e: Exception?) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val images: List<Image> = ImagePicker.getImages(data)
            images.forEach { image ->
                val date = Date().time.toString()
                val file: Uri = Uri.fromFile(File(image.path))
                val uploadTask = storageReference.child(date).putFile(file)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageReference.child(date).downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        Log.i(TAG, "upload success: "+downloadUri.toString())
                        if(!TextUtils.isEmpty(downloadUri.toString()))
                            updateChannelAvatar(downloadUri.toString())
                    }
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateChannelAvatar(url: String) {
        urlAvatar = url
        ImageLoader.getInstance().displayImage(urlAvatar, imgChannelAvatar)
    }
}
