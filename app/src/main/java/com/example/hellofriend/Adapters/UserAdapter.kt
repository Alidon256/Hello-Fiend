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
import com.example.hellofriend.Models.Message1
import com.example.hellofriend.Models.User1
import com.example.hellofriend.R
import com.example.hellofriend.WebpTranscoder
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class UserAdapter(
    private val context: Context,
    private var userList: MutableList<User1>?,
    private var messageLast: MutableList<Message1>?
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (userList.isNullOrEmpty()) return // Prevent crash if userList is empty

        val user = userList!![position]

        // 🔥 Find the last message for this specific user
        val message = messageLast
            ?.filter { it.userId == user.userId || it.recipientId == user.userId }
            ?.maxByOrNull { it.firestoreTimestamp?.seconds ?: 0 } // Get the most recent message
            ?: Message1("", "", "", "", Timestamp.now()) // Default message




        holder.bind(user, message)

        holder.userName.text = user.name ?: "Unknown User"
        if (!user.profileImageUrl.isNullOrEmpty()) {
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

        // Set click listener to open chat
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java).apply {
                putExtra("userId", user.userId)
                putExtra("name", user.name)
                putExtra("recipientImageUrl", user.profileImageUrl)
            }
            context.startActivity(intent)

            Log.d("UserAdapter", "User clicked: ${user.name}, ID: ${user.userId}")
        }
    }

    override fun getItemCount(): Int {
        return userList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val lastMessage: TextView = itemView.findViewById(R.id.lastMessage)
        val timestamp: TextView = itemView.findViewById(R.id.status)

        fun bind(user: User1, message: Message1) {
            userName.text = user.name ?: "Unknown User"
            lastMessage.text = user.lastMessage?.ifEmpty { "No messages yet" }

            val formattedTime = formatTimestamp(user.lastMessageTimestamp)
            timestamp.text = formattedTime
            timestamp.visibility = if (formattedTime.isNotEmpty()) View.VISIBLE else View.GONE
        }

        private fun formatTimestamp(timestamp: Timestamp?): String {
            if (timestamp == null) return ""
            val date = timestamp.toDate()
            val currentDate = Calendar.getInstance()
            val messageDate = Calendar.getInstance().apply { time = date }
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            return when {
                isSameDay(currentDate, messageDate) -> timeFormat.format(date) // Show only time for today’s messages
                else -> "${dateFormat.format(date)}, ${timeFormat.format(date)}" // Show date + time for old messages
            }
        }

        private fun isSameDay(calendar: Calendar, calendar2: Calendar): Boolean {
            return calendar.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
        }
    }
    fun updateLastMessage(userId: String?, newMessage: String, newTimestamp: Long, recipientId: String) {
        if (userId == null) {
            Log.e("UserAdapter", "User ID is null, cannot update last message")
            return
        }

        Log.d("UserAdapter", "Attempting to update last message for User ID: $userId or Recipient ID: $recipientId")

        // ✅ Check if userList is null or empty
        if (userList.isNullOrEmpty()) {
            Log.e("UserAdapter", "User list is empty or null! Cannot update last message.")
            return
        }

        // ✅ Iterate over userList and update message directly
        userList?.forEachIndexed { index, user ->
            Log.d("UserAdapter", "Checking User ID at index $index: ${user.userId}")

            // ✅ Update message if the user is either the sender or the recipient
            if (user.userId?.trim() == userId.trim() || user.userId?.trim() == recipientId.trim()) {
                Log.d("UserAdapter", "Found matching user: ${user.userId} at position $index")

                // ✅ Directly update the message and timestamp
                user.lastMessage = newMessage
                user.lastMessageTimestamp = Timestamp(Date(newTimestamp))

                Log.d("UserAdapter", "Updated last message: $newMessage, Timestamp: $newTimestamp")

                // ✅ Update UI
                notifyItemChanged(index)
            }
        }

        Log.d("UserAdapter", "Finished updating last message.")
    }




}
