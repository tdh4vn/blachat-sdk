package com.blameo.chatsdk.adapters

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.blameo.chatsdk.R
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.utils.DateFormatUtils
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessageAdapter(
    context: Context,
    private val messages: ArrayList<Message>,
    private val currentId: String
) : RecyclerView.Adapter<MessageAdapter.ItemMessageViewHolder>() {

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .considerExifParams(true)
        .cacheOnDisk(true)
        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
        .cacheOnDisc(true)
        .build()

    var users: HashMap<String, User> = hashMapOf()

    private var formatterDate = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault())
    var context: Context

    init {
        formatterDate.timeZone = TimeZone.getTimeZone("GMT")
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMessageViewHolder {
        return if (viewType == 1)
            MyMessageViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.my_message_viewholder,
                parent,
                false
            )
            )
        else OtherMessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.other_message_viewholder,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemMessageViewHolder, position: Int) {

        val message = messages[position]

        holder.fillData(message)


    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {

        return if(currentId == messages[position].author_id) 1 else 0
    }

    abstract inner class ItemMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal abstract fun fillData(m: Message)
    }

    inner class MyMessageViewHolder(itemView: View) :
        MessageAdapter.ItemMessageViewHolder(itemView) {

        private val tvContent: TextView = itemView.findViewById(R.id.txtContent)
        private val imgAvatar: ImageView = itemView.findViewById(R.id.avatarUser)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        override fun fillData(m: Message) {
            tvContent.text = m.content
            tvTime.text = DateFormatUtils.getInstance().getTime(m.created_at)
            if(!TextUtils.isEmpty(m.seen_at))
                tvTime.text = tvTime.text.toString() + " Seen"
            else{
                if(!TextUtils.isEmpty(m.sent_at))
                    tvTime.text = tvTime.text.toString() + " Sent"
            }


            if (!TextUtils.isEmpty(users[m.author_id]?.avatar))
                ImageLoader.getInstance().displayImage(users[m.author_id]?.avatar,imgAvatar, options)
        }
    }

    inner class OtherMessageViewHolder(itemView: View) :
        MessageAdapter.ItemMessageViewHolder(itemView) {
        private val tvContent: TextView = itemView.findViewById(R.id.txtContent)
        private val imgAvatar: ImageView = itemView.findViewById(R.id.avatarUser)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        override fun fillData(m: Message) {
            tvContent.text = m.content
            tvTime.text = DateFormatUtils.getInstance().getTime(m.created_at)

            if (!TextUtils.isEmpty(users[m.author_id]?.avatar))
                ImageLoader.getInstance().displayImage(users[m.author_id]?.avatar,imgAvatar, options)
        }
    }
}