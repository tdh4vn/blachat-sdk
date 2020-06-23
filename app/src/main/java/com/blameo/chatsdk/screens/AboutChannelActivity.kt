package com.blameo.chatsdk.screens

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.blameo.chatsdk.R
import com.blameo.chatsdk.adapters.MemberAdapter
import com.blameo.chatsdk.blachat.BlaChatSDK
import com.blameo.chatsdk.blachat.Callback
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.ConversationViewModel
import com.blameo.chatsdk.models.bla.*
import com.blameo.chatsdk.utils.UserSP
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.model.Image
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_about_channel.*
import kotlinx.android.synthetic.main.activity_about_channel.toolbar
import java.io.File
import java.lang.Exception
import java.util.*

class AboutChannelActivity : AppCompatActivity() {

    lateinit var adapter: MemberAdapter
    lateinit var chatSdk: BlaChatSDK
    private val TAG = "MEMBER"
    private var channelId = ""
    private var channelVM: ConversationViewModel? = null
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_channel)

        init()

        getMembers()
    }

    private fun getMembers() {

        if (channelVM?.memmbers == null)
            chatSdk.getUsersInChannel(channelVM?.channel?.id, object : Callback<List<BlaUser>> {
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
        adapter = MemberAdapter(this@AboutChannelActivity, users, UserSP.getInstance().id, 1, false)
        val layoutManager = LinearLayoutManager(this@AboutChannelActivity)
//        val layoutManager = GridLayoutManager(this, 2)
        adapter.setItemClickListener(object : MemberAdapter.ItemClickListener{
            override fun onClick(userId: String, position: Int, isLongClick: Boolean) {
                Log.i(TAG, "click "+userId + " "+ position)
                if(isLongClick){
                    showRemoveUserChannelDialog(position, userId)
                }
            }
        })
        rv_members.layoutManager = layoutManager
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
        channelId= intent.getStringExtra("CHANNEL_ID")!!
        channelVM = ChannelVMlStore.getInstance().getChannelByID(channelId)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child(channelId).child("images")
        if (!TextUtils.isEmpty(channelVM?.channel_name?.value))
            tvName.text = channelVM?.channel_name?.value
        if (!TextUtils.isEmpty(channelVM?.channel_avatar?.value))
            ImageLoader.getInstance().displayImage(channelVM?.channel_avatar?.value, imgAvatar)

        btn_invite.setOnClickListener {
            startActivity(Intent(this, InviteUsersActivity::class.java)
                .putExtra("CHANNEL_ID", channelId))
        }


        if(channelVM?.channel?.type == BlaChannelType.GROUP.value){
            imgAvatar.setOnClickListener {
                openGallery()
            }

            tvName.setOnClickListener {
                showDialog()
            }
        }

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
            .setTitle("Change conversation name")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, which ->
                channelVM?.channel?.name = editText.text.toString().trim()
                updateChannel()
            }
            .setNegativeButton("Cancel") { dialog, which ->

            }.create()

        dialog.show()

        editText = dialog.findViewById(R.id.edtName)!!
    }

    private fun showRemoveUserChannelDialog(position: Int, userId: String) {


        val dialog = AlertDialog.Builder(this)
            .setTitle("Do you want to remove this user?")
            .setPositiveButton("OK") { dialog, which ->
                removeUserFromChannel(position, userId)
            }
            .setNegativeButton("Cancel") { dialog, which ->

            }.create()

        dialog.show()
    }

    private fun removeUserFromChannel(position: Int, userId: String) {

        chatSdk.removeUserFromChannel(userId, channelId, object : Callback<Boolean>{
            override fun onSuccess(result: Boolean?) {
                Log.i(TAG, "remove user $userId success")

                runOnUiThread {
                    adapter.notifyAdapterChange(position)
                    Toast.makeText(this@AboutChannelActivity, "User has been removed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFail(e: Exception) {
            }
        })
    }

    private fun updateChannel() {
        val channel = channelVM?.channel
        Log.i(TAG, "avatar: "+channelVM?.channel?.avatar)
        chatSdk.updateChannel(channel, object : Callback<BlaChannel>{
            override fun onSuccess(result: BlaChannel?) {

                Log.i(TAG, "avater: "+result?.avatar + " "+result?.name)
                runOnUiThread {
                    ImageLoader.getInstance().displayImage(result?.avatar, imgAvatar)
                    tvName.text = result?.name
                }

                channelVM?.updateChannel(result?.avatar!!, result.name)
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
        channelVM?.channel?.setAvatar(url)
        Log.i(TAG, " aaaa "+channelVM?.channel?.avatar+ "\n"+url)
        updateChannel()
    }
}
