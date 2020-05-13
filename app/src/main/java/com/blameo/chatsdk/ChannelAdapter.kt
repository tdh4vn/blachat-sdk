package com.blameo.chatsdk

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blameo.chatsdk.models.entities.Channel
import com.blameo.chatsdk.screens.ChatActivity
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.ConversationViewModel
import com.blameo.chatsdk.controllers.UserVMStore
import com.blameo.chatsdk.models.results.UserStatus
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType

class ChannelAdapter(val context: Context) :
    RecyclerView.Adapter<ChannelAdapter.ChannelVH>() {


    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .considerExifParams(true)
        .cacheOnDisk(true)
        .resetViewBeforeLoading(false)
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .cacheOnDisc(true)
        .build()

    var channels: ArrayList<Channel> = arrayListOf()

    private val vmStore: ChannelVMlStore = ChannelVMlStore.getInstance()
    private val userStore: UserVMStore = UserVMStore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelVH {
        return ChannelVH(LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false))
    }

    override fun getItemCount(): Int {
        return channels.size
    }

    override fun onBindViewHolder(holder: ChannelVH, position: Int) {

        val channelVM = vmStore.getChannelViewModel(channels[position])

        val channel = channels[position]
        holder.bindChannel(channelVM, options)
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, ChatActivity::class.java)
                .putExtra("CHANNEL", channel.id))
        }

        channelVM.partnerId.observeForever { partnerId ->
//            Log.e("ADAPTER", "partner id: $partnerId" )
            val userStatus = userStore.getUserViewModel(UserStatus(partnerId, 1))
            userStatus.status.observeForever { status ->
                if(status){
                    holder.imgStatus.setColorFilter(context.resources.getColor(android.R.color.holo_green_light))
                }else
                    holder.imgStatus.setColorFilter(context.resources.getColor(android.R.color.holo_red_light))
            }
        }
    }

    class ChannelVH(view: View) : RecyclerView.ViewHolder(view) {

        var tvName: TextView = view.findViewById(R.id.tvName)
        var tvContent: TextView = view.findViewById(R.id.tvContent)
        var imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        var tvTime: TextView = view.findViewById(R.id.tvTime)
        var imgStatus: ImageView = view.findViewById(R.id.imgStatus)

        fun bindChannel(channelVM: ConversationViewModel, options: DisplayImageOptions) {

            if (!TextUtils.isEmpty(channelVM.channel_avatar.value.toString()))
                ImageLoader.getInstance().displayImage(channelVM.channel_avatar.value.toString(), imgAvatar, options)
            else
                imgAvatar.setImageResource(R.mipmap.ic_launcher)
            tvName.text = channelVM.channel_name.value.toString()
            tvContent.text = channelVM.last_message.value.toString()

            tvTime.text = channelVM.channel_updated.value.toString()
        }
    }
}
