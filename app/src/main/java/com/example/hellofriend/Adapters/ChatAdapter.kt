package com.example.hellofriend.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hellofriend.Adapters.ChatAdapter.ChatViewHolder
import com.example.hellofriend.Models.Message1
import com.example.hellofriend.R
import java.util.ArrayList

class ChatAdapter(private var messageList: ArrayList<Message1>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder?>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageTextView: TextView = itemView.findViewById<TextView>(R.id.message_text)
        var userNameTextView: TextView? = itemView.findViewById<TextView?>(R.id.user_name)
        fun bind(message: Message1) {
            messageTextView.text = message.text
            userNameTextView?.text = message.userName
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int) {

    }

    override fun getItemCount(): Int {
        return messageList.size
    }


}
