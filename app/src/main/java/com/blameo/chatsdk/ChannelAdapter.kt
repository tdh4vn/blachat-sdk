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
import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.screens.ChatActivity
import com.blameo.chatsdk.utils.DateFormatUtils
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType

class ChannelAdapter(val context: Context, private val channels: ArrayList<Channel>) :
    RecyclerView.Adapter<ChannelAdapter.ChannelVH>() {

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .considerExifParams(true)
        .cacheOnDisk(true)
        .resetViewBeforeLoading(false)
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .cacheOnDisc(true)
        .build()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelVH {
        return ChannelVH(
            LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false),
            context
        )
    }

    override fun getItemCount(): Int {
        return channels.size
    }

    override fun onBindViewHolder(holder: ChannelVH, position: Int) {

        val channel = channels[position]
        holder.bindChannel(channel, options)
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, ChatActivity::class.java)
                .putExtra("CHANNEL", channel))
        }
    }

    class ChannelVH(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        var tvName: TextView = view.findViewById(R.id.tvName)
        var tvContent: TextView = view.findViewById(R.id.tvContent)
        var imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        var tvTime: TextView = view.findViewById(R.id.tvTime)

        fun bindChannel(channel: Channel, options: DisplayImageOptions) {

            if (!TextUtils.isEmpty(channel.avatar))
                ImageLoader.getInstance().displayImage(channel.avatar, imgAvatar, options)
            tvName.text = channel.name
            if (channel.last_message != null)
                tvContent.text = channel.last_message.content

            tvTime.text = DateFormatUtils.getInstance().getTime(channel.updated_at)
        }
    }
}
