package com.blameo.chatsdk.adapters

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blameo.chatsdk.R
import com.blameo.chatsdk.controllers.UserVMStore
import com.blameo.chatsdk.controllers.UserViewModel
import com.blameo.chatsdk.models.bla.BlaUser
import com.blameo.chatsdk.models.entities.User
import com.blameo.chatsdk.models.results.UserStatus
import com.blameo.chatsdk.utils.UserSP
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType

class UserSeenMessageAdapter(val context: Context, private val users: List<BlaUser>, private val myId: String, private val type: Int) :
    RecyclerView.Adapter<UserSeenMessageAdapter.MemberVH>() {

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .considerExifParams(true)
        .cacheOnDisk(true)
        .resetViewBeforeLoading(false)
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .cacheOnDisc(true)
        .build()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberVH {
        return MemberVH(
            LayoutInflater.from(context).inflate(R.layout.item_user_react, parent, false),
            context
        )
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: MemberVH, position: Int) {

        val member = users[position]
        holder.bindUser(member, options, context)
    }

    class MemberVH(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        var imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)

        fun bindUser(user: User, options: DisplayImageOptions, context: Context) {
            if(!TextUtils.isEmpty(user.avatar))
                ImageLoader.getInstance().displayImage(user.avatar, imgAvatar, options)

        }
    }
}
