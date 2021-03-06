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
import com.blameo.chatsdk.models.bla.BlaChannel
import com.blameo.chatsdk.models.bla.BlaChannelType
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

    var channels: ArrayList<BlaChannel> = arrayListOf()

    private val vmStore: ChannelVMlStore = ChannelVMlStore.getInstance()
    private val userStore: UserVMStore = UserVMStore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelVH {
        return ChannelVH(LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false))
    }

    override fun getItemCount(): Int {
        return channels.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
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
            val userStatus = userStore.getUserViewModel(UserStatus(partnerId, 1))
            userStatus.status.observeForever { status ->
                if(status){
                    holder.imgStatus.setBackgroundResource(R.drawable.shape_bubble_online)
                }else
                    holder.imgStatus.setBackgroundResource(R.drawable.shape_bubble_offline)
            }
        }
    }

    class ChannelVH(view: View) : RecyclerView.ViewHolder(view) {

        var tvName: TextView = view.findViewById(R.id.tvName)
        var tvContent: TextView = view.findViewById(R.id.tvContent)
        var imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        var tvTime: TextView = view.findViewById(R.id.tvTime)
        var imgStatus: View = view.findViewById(R.id.imgStatus)

        fun bindChannel(channelVM: ConversationViewModel, options: DisplayImageOptions) {

            channelVM.channel_avatar.observeForever {
                if (!TextUtils.isEmpty(it))
                    ImageLoader.getInstance().displayImage(it, imgAvatar, options)
                else
                    imgAvatar.setImageResource(R.mipmap.ic_launcher)
            }

            channelVM.channel_name.observeForever {
                tvName.text = it
            }


            channelVM.last_message.observeForever {
                tvContent.text = if(!TextUtils.isEmpty(it)) it else ""
            }

            channelVM.channel_updated.observeForever {
                tvTime.text = it
            }

        }
    }
}
