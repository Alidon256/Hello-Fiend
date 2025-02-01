package com.example.hellofriend

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hellofriend.Adapters.UserAdapter
import com.example.hellofriend.Models.Message1
import com.example.hellofriend.Models.User1
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private val CHAT_REQUEST_CODE = 101
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var userList: MutableList<User1?>? = null
    private var messageLast: MutableList<Message1>? = null
    private var db: FirebaseFirestore? = null
    private var cameraHome: ImageView? = null
    private var menuHome: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeUI()
        setupRecyclerView()
        fetchUsers()
        setupClickListeners()
        val lastMessage = intent.getStringExtra("lastMessage")
        val lastMessageTimestamp = intent.getLongExtra("lastMessageTimestamp", 0)
        val userId = intent.getStringExtra("userId")
        Log.d(TAG, "Received last message update -> User ID: $userId, Message: $lastMessage, Timestamp: $lastMessageTimestamp")

        if (lastMessage != null && lastMessageTimestamp != 0L && userId != null) {
            updateLastMessage(userId, lastMessage, lastMessageTimestamp)
        }
    }

    private fun initializeUI() {
        cameraHome = findViewById(R.id.camera_home)
        menuHome = findViewById(R.id.menu_home)
        recyclerView = findViewById(R.id.recyclerView)
        db = FirebaseFirestore.getInstance()
        userList = ArrayList()
        messageLast = ArrayList()
    }

    private fun updateLastMessage(userId: String?, lastMessage: String, timestamp: Long) {
        Log.d(TAG, "Updating last message for User ID: $userId, Message: $lastMessage, Timestamp: $timestamp")
        userAdapter?.updateLastMessage(userId, lastMessage, timestamp)
    }

    private fun setupRecyclerView() {
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(this, userList as MutableList<User1>, messageLast as MutableList<Message1>)
        recyclerView!!.adapter = userAdapter
    }

    private fun setupClickListeners() {
        cameraHome!!.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                ImagePicker.with(this).cameraOnly().start()
            }
        }

        menuHome!!.setOnClickListener {
            Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUsers() {
        db!!.collection("userProfileInfo")
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                updateUserList(queryDocumentSnapshots)

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching users", e)
                Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserList(queryDocumentSnapshots: QuerySnapshot) {
        userList!!.clear()
        userList!!.addAll(queryDocumentSnapshots.toObjects(User1::class.java))
        userAdapter!!.notifyDataSetChanged()
        Log.d(TAG, "User list updated with ${userList!!.size} users")
    }


    companion object {
        private const val TAG = "MainActivity"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }
}
