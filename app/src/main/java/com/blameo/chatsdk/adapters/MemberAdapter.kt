package com.blameo.chatsdk.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blameo.chatsdk.R
import com.blameo.chatsdk.models.pojos.User
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType

class MemberAdapter(val context: Context, private val users: ArrayList<User>, private val myId: String) :
    RecyclerView.Adapter<MemberAdapter.MemberVH>() {


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
            LayoutInflater.from(context).inflate(R.layout.item_user, parent, false),
            context
        )
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: MemberVH, position: Int) {

        val member = users[position]
        holder.bindUser(member, options)

        holder.itemView.setOnClickListener {
            member.isCheck = !member.isCheck
            holder.bindCheck(member)
        }
    }

    class MemberVH(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        var tvName: TextView = view.findViewById(R.id.tvName)
        var imgCheck: ImageView = view.findViewById(R.id.imgSelected)
        var imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)

        fun bindUser(user: User, options: DisplayImageOptions) {
            if(!TextUtils.isEmpty(user.name))
                tvName.text = user.name
            if(!TextUtils.isEmpty(user.avatar))
                ImageLoader.getInstance().displayImage(user.avatar, imgAvatar, options)

            bindCheck(user)

        }
        fun bindCheck(user: User) {

            if(!user.isCheck)
                imgCheck.visibility = View.INVISIBLE
            else
                imgCheck.visibility = View.VISIBLE

        }
    }
}
