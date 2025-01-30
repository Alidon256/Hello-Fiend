package com.example.hellofriend.Activities

import android.content.Intent
import com.google.firebase.Timestamp
import android.os.Bundle
import android.view.View
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
import java.util.ArrayList

class ChatActivity : AppCompatActivity() {
    private var chatRecyclerView: RecyclerView? = null
    private var chatAdapter: ChatAdapter? = null
    private var messageList: MutableList<Message1?>? = null
    private var editText: EditText? = null
    private var sendButton: ImageButton? = null
    private var backBtn: ImageView? = null
    private var imageUser: ImageView? = null
    private var phoneCall: ImageView? = null
    private var videoCall: ImageView? = null
    private var recipientName: TextView? = null

    private var mAuth: FirebaseAuth? = null
    private var currentUserId: String? = null
    private var userId: String? = null
    private var userName: String? = null
    private var recipientImageUrl: String? = null

    private var chatViewModel: ChatViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        if (mAuth!!.currentUser == null) {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        currentUserId = mAuth!!.currentUser!!.uid

        // Get intent data
        userId = intent.getStringExtra("userId")
        userName = intent.getStringExtra("name")
        recipientImageUrl = intent.getStringExtra("recipientImageUrl")

        if (userId == null || userName == null) {
            Toast.makeText(this, "Chat recipient information is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        initializeViews()

        // Set recipient info
        setUpRecipientDetails()

        // Set up RecyclerView
        setUpRecyclerView()

        // Initialize ViewModel
        initializeViewModel()

        // Set up button listeners
        setUpListeners()
    }

    private fun initializeViews() {
        chatRecyclerView = findViewById<RecyclerView>(R.id.chat_recycler)
        editText = findViewById<EditText>(R.id.editT)
        sendButton = findViewById<ImageButton>(R.id.send_message)
        backBtn = findViewById<ImageView>(R.id.icon_right)
        recipientName = findViewById<TextView>(R.id.name_chat)
        imageUser = findViewById<ImageView>(R.id.image_user)
        phoneCall = findViewById<ImageView>(R.id.phone_call)
        videoCall = findViewById<ImageView>(R.id.video_call)
    }

    private fun setUpRecipientDetails() {
        recipientName!!.text = if (userName != null) userName else "Unknown User"
        if (recipientImageUrl != null && !recipientImageUrl!!.isEmpty()) {
            Picasso.get()
                .load(recipientImageUrl)
                .placeholder(R.drawable.ic_me)
                .into(imageUser)
        } else {
            imageUser!!.setImageResource(R.drawable.ic_me)
        }
    }

    private fun setUpRecyclerView() {
        messageList = ArrayList<Message1?>()
        chatAdapter = ChatAdapter(messageList as ArrayList<Message1?>)
        chatRecyclerView!!.setLayoutManager(LinearLayoutManager(this))
        chatRecyclerView!!.setAdapter(chatAdapter)
    }

    private fun initializeViewModel() {
        chatViewModel = ViewModelProvider(this).get<ChatViewModel>(ChatViewModel::class.java)

        // Observe messages in real-time
        chatViewModel!!.loadMessages(currentUserId, userId)
        chatViewModel!!.getMessagesLiveData(currentUserId, userId)
            .observe(this, Observer { messages: MutableList<Message1?>? ->
                if (messages != null) {
                    messageList!!.clear()
                    messageList!!.addAll(messages)
                    chatAdapter!!.notifyDataSetChanged()
                    chatRecyclerView!!.scrollToPosition(messageList!!.size - 1)
                }
            })
    }

    private fun setUpListeners() {
        sendButton!!.setOnClickListener(View.OnClickListener { v: View? -> sendMessage() })
        backBtn!!.setOnClickListener(View.OnClickListener { v: View? -> navigateBack() })
        phoneCall!!.setOnClickListener(View.OnClickListener { v: View? -> handlePhoneCall() })
        videoCall!!.setOnClickListener(View.OnClickListener { v: View? -> handleVideoCall() })
    }

    private fun sendMessage() {
        val messageText = editText!!.getText().toString().trim { it <= ' ' }
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new message
        val message = Message1()
        message.text = messageText
        message.userId = currentUserId
        message.recipientId = userId
        message.firestoreTimestamp = Timestamp.now()

        // Add to local list and update UI
        messageList!!.add(message)
        chatAdapter!!.notifyItemInserted(messageList!!.size - 1)
        chatRecyclerView!!.scrollToPosition(messageList!!.size - 1)

        // Send message to Firestore
        chatViewModel!!.sendMessage(message)

        // Clear input field
        editText!!.setText("")
    }

    private fun navigateBack() {
        val intent = Intent(this@ChatActivity, MainActivity::class.java)
        intent.putExtra("name", userName)
        intent.putExtra("userID", userId)
        intent.putExtra("recipientImageUrl", recipientImageUrl)
        startActivity(intent)
    }

    private fun handlePhoneCall() {
        Toast.makeText(this, "Phone call feature coming soon!", Toast.LENGTH_SHORT).show()
    }

    private fun handleVideoCall() {
        Toast.makeText(this, "Video call feature coming soon!", Toast.LENGTH_SHORT).show()
    }
}
