package com.example.hellofriend.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hellofriend.Adapters.UserAdapter
import com.example.hellofriend.Models.Message1
import com.example.hellofriend.Models.User1
import com.example.hellofriend.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception
import java.util.ArrayList

class Main : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var userList: MutableList<User1?>? = null
    private var messageLast: MutableList<Message1> = ArrayList()
    private var db: FirebaseFirestore? = null
    private var cameraHome: ImageView? = null
    private var menuHome: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            // Initialize UI components
            cameraHome = findViewById<ImageView>(R.id.camera_home)
            menuHome = findViewById<ImageView>(R.id.menu_home)
            recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

            // Initialize RecyclerView
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.setLayoutManager(LinearLayoutManager(this))

            // Initialize Firestore and data list
            db = FirebaseFirestore.getInstance()
            userList = ArrayList<User1?>()

            // Set up Adapter
            userAdapter = UserAdapter(this, userList as MutableList<User1>?, messageLast)
            recyclerView!!.setAdapter(userAdapter)

            // Fetch user data from Firestore
            fetchUsers()

            // Set listeners for UI interactions
            setupClickListeners()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MainActivity", e)
            Toast.makeText(this, "An error occurred during initialization", Toast.LENGTH_LONG)
                .show()
        }
    }

    /**
     * Sets up click listeners for the camera and menu buttons.
     */
    private fun setupClickListeners() {
        cameraHome!!.setOnClickListener(View.OnClickListener { v: View? ->
            Log.d(TAG, "Camera icon clicked")
            try {
                ImagePicker.with(this)
                    .cameraOnly()
                    .start()
            } catch (e: Exception) {
                Log.e(TAG, "Error launching camera", e)
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        })

        menuHome!!.setOnClickListener(View.OnClickListener { v: View? ->
            Log.d(TAG, "Menu icon clicked")
            Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show()
        })
    }

    /**
     * Fetches users from the Firestore collection and updates the RecyclerView.
     */
    private fun fetchUsers() {
        Log.d(TAG, "Fetching users from Firestore")
        db!!.collection("userProfileInfo")
            .get()
            .addOnSuccessListener(OnSuccessListener { queryDocumentSnapshots: QuerySnapshot? ->
                this.handleSuccess(
                    queryDocumentSnapshots!!
                )
            })
            .addOnFailureListener(OnFailureListener { e: Exception? -> this.handleFailure(e!!) })
    }

    /**
     * Handles the successful fetch of user data.
     *
     * @param queryDocumentSnapshots The Firestore query results.
     */
    private fun handleSuccess(queryDocumentSnapshots: QuerySnapshot) {
        Log.d(TAG, "Successfully fetched users from Firestore")
        try {
            userList!!.clear()
            userList!!.addAll(queryDocumentSnapshots.toObjects<User1?>(User1::class.java))
            userAdapter!!.notifyDataSetChanged()
            Log.d(TAG, "User list updated with " + userList!!.size + " users")
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing user data", e)
            Toast.makeText(this, "Error parsing user data", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Handles errors when fetching user data.
     *
     * @param e The exception thrown during the fetch.
     */
    private fun handleFailure(e: Exception) {
        Log.e(TAG, "Error fetching users", e)
        if (e is FirebaseFirestoreException) {
            val firestoreException = e
            Toast.makeText(
                this,
                "Firestore Error: " + firestoreException.message,
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
