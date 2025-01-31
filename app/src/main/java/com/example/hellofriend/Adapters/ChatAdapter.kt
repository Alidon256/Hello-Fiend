package com.example.hellofriend.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hellofriend.Models.Message1
import com.example.hellofriend.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private var messageList: ArrayList<Message1>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutId = when (viewType) {
            VIEW_TYPE_SENT_FIRST -> R.layout.item_message_sent
            VIEW_TYPE_SENT_CONTINUED -> R.layout.item_message_sent_continued
            VIEW_TYPE_RECEIVED_FIRST -> R.layout.item_message_received
            VIEW_TYPE_RECEIVED_CONTINUED -> R.layout.item_message_received_continued
            VIEW_TYPE_SENT_LAST -> R.layout.item_message_sent_last
            VIEW_TYPE_RECEIVED_LAST -> R.layout.item_message_recieved_last
            else -> throw IllegalArgumentException("Unknown view type")
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messageList[position]
        val isLastMessage = position == messageList.size - 1
        holder.bind(message, isLastMessage)
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return if (messageList[position].userId == currentUserId) VIEW_TYPE_SENT_FIRST else VIEW_TYPE_RECEIVED_FIRST
        }

        val previousMessage = messageList[position - 1]
        val currentMessage = messageList[position]

        if (position == messageList.size - 1) {
            return if (currentMessage.userId == currentUserId) VIEW_TYPE_SENT_LAST else VIEW_TYPE_RECEIVED_LAST
        }

        return if (currentMessage.userId == previousMessage.userId) {
            if (currentMessage.userId == currentUserId) VIEW_TYPE_SENT_CONTINUED else VIEW_TYPE_RECEIVED_CONTINUED
        } else {
            if (currentMessage.userId == currentUserId) VIEW_TYPE_SENT_FIRST else VIEW_TYPE_RECEIVED_FIRST
        }
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.message_text)
        private val timestampTextView: TextView? = itemView.findViewById(R.id.timestamp_text)

        fun bind(message: Message1, isLastMessage: Boolean) {
            messageTextView.text = message.text

            // Show timestamp only for the last message
            if (isLastMessage) {
                timestampTextView?.visibility = View.VISIBLE
                timestampTextView?.text = formatTimestamp(message.firestoreTimestamp)
            } else {
                timestampTextView?.visibility = View.GONE
            }
        }

        private fun formatTimestamp(timestamp: Timestamp?): String {
            if (timestamp == null) return ""

            val date = timestamp.toDate()
            val currentDate = Calendar.getInstance()
            val messageDate = Calendar.getInstance().apply { time = date }

            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // e.g., 10:30 AM
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) // e.g., Jan 28, 2025

            return when {
                isSameDay(currentDate, messageDate) -> timeFormat.format(date) // Show only time for today
                else -> "${dateFormat.format(date)}, ${timeFormat.format(date)}" // Show full date + time
            }
        }

        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT_FIRST = 1
        private const val VIEW_TYPE_SENT_CONTINUED = 2
        private const val VIEW_TYPE_RECEIVED_FIRST = 3
        private const val VIEW_TYPE_RECEIVED_CONTINUED = 4
        private const val VIEW_TYPE_SENT_LAST = 5
        private const val VIEW_TYPE_RECEIVED_LAST = 6
    }
}
