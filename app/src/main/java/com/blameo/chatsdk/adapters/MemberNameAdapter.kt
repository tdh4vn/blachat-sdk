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

class MemberNameAdapter(val context: Context) :
    RecyclerView.Adapter<MemberNameAdapter.MemberVH>() {

    var users: List<BlaUser> = arrayListOf()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberVH {
        val view = LayoutInflater.from(context).inflate(R.layout.item_member_name, parent, false)
        return MemberVH(view, context)
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
        if(!TextUtils.isEmpty(member.name))
            holder.tvName.text = member.name
    }

    class MemberVH(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        var tvName: TextView = view.findViewById(R.id.tvName)
    }
}
