package com.example.hellofriend.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hellofriend.Adapters.ChatAdapter
import com.example.hellofriend.MainActivity
import com.example.hellofriend.Models.Message1
import com.example.hellofriend.R
import com.example.hellofriend.ViewModel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: ArrayList<Message1>
    private lateinit var sendButton: ImageButton
    private lateinit var backBtn: ImageView
    private lateinit var recipientName: TextView
    private lateinit var imageUser: ImageView
    private lateinit var editText: EditText

    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var userId: String
    private lateinit var userName: String
    private var recipientImageUrl: String? = null

    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser == null) {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        currentUserId = mAuth.currentUser!!.uid

        // Get intent data
        userId = intent.getStringExtra("userId") ?: ""
        userName = intent.getStringExtra("name") ?: ""
        recipientImageUrl = intent.getStringExtra("recipientImageUrl")

        if (userId.isEmpty() || userName.isEmpty()) {
            Toast.makeText(this, "Chat recipient information is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        initializeViews()
        setUpRecipientDetails()
        setUpRecyclerView()
        initializeViewModel()
        setUpListeners()
    }

    private fun initializeViews() {
        chatRecyclerView = findViewById(R.id.chat_recycler)
        editText = findViewById(R.id.editT)
        sendButton = findViewById(R.id.send_message)
        backBtn = findViewById(R.id.icon_right)
        recipientName = findViewById(R.id.name_chat)
        imageUser = findViewById(R.id.image_user)
    }

    private fun setUpRecipientDetails() {
        recipientName.text = userName
        if (!recipientImageUrl.isNullOrEmpty()) {
            Picasso.get().load(recipientImageUrl).placeholder(R.drawable.ic_me).into(imageUser)
        } else {
            imageUser.setImageResource(R.drawable.ic_me)
        }
    }

    private fun setUpRecyclerView() {
        messageList = ArrayList()
        chatAdapter = ChatAdapter(messageList)
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
    }

    private fun initializeViewModel() {
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        // Observe messages in real-time
        chatViewModel.getMessagesLiveData(currentUserId, userId).observe(this, Observer { messages ->
            if (messages != null&& messages.isNotEmpty()) {
                messageList.clear()
                messageList.addAll(messages)
                chatAdapter.notifyDataSetChanged()
                chatRecyclerView.post {
                    chatRecyclerView.smoothScrollToPosition(messageList.size - 1)
                }
            }
        })
    }

    private fun setUpListeners() {
        sendButton.setOnClickListener { sendMessage() }
        backBtn.setOnClickListener { navigateBack() }
    }

    private fun sendMessage() {
        val messageText = editText.text.toString().trim()
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val message = Message1(
            text = messageText,
            userId = currentUserId,
            recipientId = userId,
            userName = userName,
            firestoreTimestamp = com.google.firebase.Timestamp.now()
        )

        // Send the message using ViewModel
        chatViewModel.sendMessage(message)

        // Clear the EditText
        editText.text.clear()
    }

    private fun navigateBack() {
        val lastMessage = messageList.lastOrNull()
        val lastMessageText = lastMessage?.text ?: "No messages yet"
        val lastMessageTimestamp = lastMessage?.firestoreTimestamp?.toDate()?.time ?: 0

        Log.d("ChatActivity", "Navigating back with Last Message: $lastMessageText, Timestamp: $lastMessageTimestamp")

        val resultIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("lastMessage", lastMessageText)
            putExtra("lastMessageTimestamp", lastMessageTimestamp)
            putExtra("userId", intent.getStringExtra("userId"))
        }
        startActivity(resultIntent)
        finish()
    }



}
