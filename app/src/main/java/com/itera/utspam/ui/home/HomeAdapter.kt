package com.itera.utspam.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.itera.utspam.R
import com.itera.utspam.data.model.DataItem

class HomeAdapter(private val users: MutableList<DataItem>, var onItemClick: OnItemClick) :
    RecyclerView.Adapter<HomeAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ListViewHolder(
            view
        )
    }

    fun addUser(newUsers: DataItem) {
        users.add(newUsers)
        notifyItemInserted(users.lastIndex)
    }

    fun clear() {
        users.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = users[position]

        Glide.with(holder.itemView.context)
            .load(user.avatar)
            .apply(RequestOptions().override(80, 80).placeholder(R.drawable.baseline_person_24))
            .transform(CircleCrop())
            .into(holder.ivAvatar)

        holder.tvUserName.text = "${user.firstName} ${user.lastName}"
        holder.tvEmail.text = user.email

        holder.itemView.rootView.setOnClickListener {
            onItemClick.onItemClicked(user.id ?: 1)
        }
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUserName: TextView = itemView.findViewById(R.id.tv_name)
        var tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        var ivAvatar: ImageView = itemView.findViewById(R.id.iv_image_profile)
    }

    interface OnItemClick {
        fun onItemClicked(id: Int)
    }
}