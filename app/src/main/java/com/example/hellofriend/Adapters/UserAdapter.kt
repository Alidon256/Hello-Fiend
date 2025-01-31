package com.example.hellofriend.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.hellofriend.Activities.ChatActivity
import com.example.hellofriend.Models.User1
import com.example.hellofriend.R
import com.example.hellofriend.WebpTranscoder
import com.squareup.picasso.Picasso

class UserAdapter(context: Context, userList: MutableList<User1>?) :
    RecyclerView.Adapter<UserAdapter.ViewHolder?>() {
    private val context: Context
    private val userList: MutableList<User1>?

    init {
        requireNotNull(context) { "Context cannot be null in UserAdapter" }
        this.context = context
        this.userList = userList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList!![position]

        // Set user name
        holder.userName.text = if (user.name != null) user.name else "Unknown User"

        // Load user image with Picasso
        if (user.profileImageUrl != null && !user.profileImageUrl!!.isEmpty()) {
            Glide.with(context)
                .asBitmap()
                .load(user.profileImageUrl)
                .placeholder(R.drawable.ic_me)
                .apply(RequestOptions.bitmapTransform(WebpTranscoder()))
                .error(R.drawable.ic_me)
                .into(holder.userImage)
        } else {
            holder.userImage.setImageResource(R.drawable.ic_me)
        }

        // Set click listener
        holder.itemView.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId", user.userId)
            intent.putExtra("name", user.name)
            intent.putExtra("recipientImageUrl", user.profileImageUrl)
            context.startActivity(intent)

            Log.d("UserAdapter", "User clicked: " + user.name)
            Log.d("UserAdapter", "User ID: " + user.userId)
        })
    }

    override fun getItemCount(): Int {
        return if (userList != null) userList.size else 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView.findViewById<TextView>(R.id.userName)
        var userImage: ImageView = itemView.findViewById<ImageView>(R.id.userImage)
    }
}
