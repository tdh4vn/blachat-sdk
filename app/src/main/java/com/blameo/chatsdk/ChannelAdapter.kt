package com.blameo.chatsdk

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.screens.ChatActivity
import com.blameo.chatsdk.utils.DateFormatUtils
import com.blameo.chatsdk.controllers.ChannelVMlStore
import com.blameo.chatsdk.controllers.ConversationViewModel
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
                .putExtra("CHANNEL", channel))
        }
    }

    class ChannelVH(view: View) : RecyclerView.ViewHolder(view) {

        var tvName: TextView = view.findViewById(R.id.tvName)
        var tvContent: TextView = view.findViewById(R.id.tvContent)
        var imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        var tvTime: TextView = view.findViewById(R.id.tvTime)

        fun bindChannel(channelVM: ConversationViewModel, options: DisplayImageOptions) {

            Log.i("adapter", "avatar: ${channelVM.channel.avatar}")

            if (!TextUtils.isEmpty(channelVM.channel_avatar.value.toString()))
                ImageLoader.getInstance().displayImage(channelVM.channel_avatar.value.toString(), imgAvatar, options)
            else
                imgAvatar.setImageResource(R.mipmap.ic_launcher)
            tvName.text = channelVM.channel_name.value.toString()
            tvContent.text = channelVM.last_message.value.toString()

            tvTime.text = channelVM.channel_updated.value.toString()

//            channelVM.channel_name.observeForever {
//                tvName.text = it.toString()
//            }
//
//            channelVM.channel_avatar.observeForever {
//                if (!TextUtils.isEmpty(channelVM.channel_avatar.value))
//                    ImageLoader.getInstance().displayImage(channelVM.channel_avatar.value.toString(), imgAvatar, options)
//            }
        }
    }
}
