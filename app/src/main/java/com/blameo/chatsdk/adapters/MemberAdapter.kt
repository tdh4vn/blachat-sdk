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
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType

class MemberAdapter(val context: Context, private val users: List<BlaUser>, private val myId: String, private val type: Int) :
    RecyclerView.Adapter<MemberAdapter.MemberVH>() {

    val userVMStore = UserVMStore.getInstance()

    interface SelectUserListener{
        fun onAdd(id: String)
        fun onRemove(id: String)
    }

    private var selectUserListener: SelectUserListener? = null
    private var itemClickListener: ItemClickListener?  = null

    fun setListener(listener: SelectUserListener){
        selectUserListener = listener
    }

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }

    fun notifyAdapterChange(position: Int){
        (users as MutableList).removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onClick(userId: String, position: Int, isLongClick: Boolean)
    }

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .considerExifParams(true)
        .cacheOnDisk(true)
        .resetViewBeforeLoading(false)
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .cacheOnDisc(true)
        .build()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberVH {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return MemberVH(
            view,
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
        val memberVM = userVMStore.getUserViewModel(UserStatus(member.id, 1))
        holder.bindUser(member, memberVM, options, context)

        if(type == 1)
        {
            holder.itemView.isClickable = false
            holder.itemView.isLongClickable = true
            holder.imgCheck.visibility = View.INVISIBLE
            holder.itemView.setOnLongClickListener {
                Log.i("Adsd", ""+position)
                itemClickListener?.onClick(member.id, position, true)
                true
            }
        }

        holder.itemView.setOnClickListener {
            if(selectUserListener != null) {
                if(holder.imgCheck.visibility == View.VISIBLE) {
                    holder.imgCheck.visibility = View.INVISIBLE
                    selectUserListener?.onRemove(member.id)
                }
                else{
                    holder.imgCheck.visibility = View.VISIBLE
                    selectUserListener?.onAdd(member.id)
                }
            }
        }
    }

    class MemberVH(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        var tvName: TextView = view.findViewById(R.id.tvName)
        var imgCheck: ImageView = view.findViewById(R.id.imgSelected)
        var imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        var imgStatus: View = view.findViewById(R.id.imgStatus)

        fun bindUser(user: User, userStatus: UserViewModel, options: DisplayImageOptions, context: Context) {
            if(!TextUtils.isEmpty(user.name))
                tvName.text = user.name
            if(!TextUtils.isEmpty(user.avatar))
                ImageLoader.getInstance().displayImage(user.avatar, imgAvatar, options)
            userStatus.status.observeForever { status ->
                if(status){
                    imgStatus.setBackgroundResource(R.drawable.shape_bubble_online)
                }else
                    imgStatus.setBackgroundResource(R.drawable.shape_bubble_offline)
            }
        }
    }
}
