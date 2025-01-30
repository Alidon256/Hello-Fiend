package com.example.hellofriend

import android.Manifest
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
import com.example.hellofriend.Models.User1
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var userList: MutableList<User1?>? = null
    private var db: FirebaseFirestore? = null
    private var cameraHome: ImageView? = null
    private var menuHome: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        initializeUI()

        // Set up RecyclerView
        setupRecyclerView()

        // Fetch users from Firestore
        fetchUsers()

        // Set up click listeners
        setupClickListeners()
    }

    private fun initializeUI() {
        cameraHome = findViewById<ImageView>(R.id.camera_home)
        menuHome = findViewById<ImageView>(R.id.menu_home)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        db = FirebaseFirestore.getInstance()
        userList = ArrayList<User1?>()
    }

    private fun setupRecyclerView() {
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.setLayoutManager(LinearLayoutManager(this))
        userAdapter = UserAdapter(this, userList)
        recyclerView!!.setAdapter(userAdapter)
    }

    private fun setupClickListeners() {
        cameraHome!!.setOnClickListener(View.OnClickListener { v: View? ->
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {
                ImagePicker.with(this).cameraOnly().start()
            }
        })

        menuHome!!.setOnClickListener(View.OnClickListener { v: View? ->
            Toast.makeText(
                this,
                "Menu clicked",
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    private fun fetchUsers() {
        db!!.collection("userProfileInfo")
            .get()
            .addOnSuccessListener(OnSuccessListener { queryDocumentSnapshots: QuerySnapshot? ->
                this.updateUserList(
                    queryDocumentSnapshots!!
                )
            })
            .addOnFailureListener(OnFailureListener { e: Exception? ->
                Log.e(TAG, "Error fetching users", e)
                Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show()
            })
    }

    private fun updateUserList(queryDocumentSnapshots: QuerySnapshot) {
        userList!!.clear()
        userList!!.addAll(queryDocumentSnapshots.toObjects<User1?>(User1::class.java))
        userAdapter!!.notifyDataSetChanged()
        Log.d(TAG, "User list updated with " + userList!!.size + " users")
    }


    companion object {
        private const val TAG = "MainActivity"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }
}
