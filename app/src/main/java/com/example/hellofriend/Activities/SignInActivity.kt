package com.example.hellofriend.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hellofriend.MainActivity
import com.example.hellofriend.Models.User
import com.example.hellofriend.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class SignInActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var nameEditText: EditText? = null
    private var registerButton: Button? = null
    private var loginButton: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is already signed in
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth!!.getCurrentUser()
        if (currentUser != null) {
            // User is already signed in, navigate to MainActivity
            navigateToMainActivity()
            return
        }

        setContentView(R.layout.activity_sign_in)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()

        // Bind UI elements
        emailEditText = findViewById<EditText>(R.id.etEmail)
        passwordEditText = findViewById<EditText>(R.id.etPass)
        nameEditText = findViewById<EditText>(R.id.nameEditText)
        registerButton = findViewById<Button>(R.id.btnSignUp)
        loginButton = findViewById<TextView>(R.id.tvLogIn)

        // Set up listeners
        registerButton!!.setOnClickListener(View.OnClickListener { v: View? -> registerUser() })
        loginButton!!.setOnClickListener(View.OnClickListener { v: View? -> loginUser() })
    }

    private fun registerUser() {
        val email = emailEditText!!.getText().toString().trim { it <= ' ' }
        val password = passwordEditText!!.getText().toString().trim { it <= ' ' }
        val name = nameEditText!!.getText().toString().trim { it <= ' ' }

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Register user with Firebase Authentication
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener { task: Task<AuthResult?>? ->
                if (task!!.isSuccessful()) {
                    val user = mAuth!!.getCurrentUser()
                    if (user != null) {
                        val userId = user.getUid()
                        val newUser = User(userId, name, email)

                        // Save user data to Firestore
                        db!!.collection("Users")
                            .document(userId)
                            .set(newUser)
                            .addOnSuccessListener(OnSuccessListener { aVoid: Void? ->
                                Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show()
                                navigateToProfileActivity()
                            })
                            .addOnFailureListener(OnFailureListener { e: Exception? ->
                                Log.e("SignUpActivity", "Error registering user", e)
                                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT)
                                    .show()
                            })
                    }
                } else {
                    Log.e("SignUpActivity", "Registration Failed", task.getException())
                    Toast.makeText(
                        this,
                        "Registration Failed: " + task.getException()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun navigateToProfileActivity() {
        val intent = Intent(this@SignInActivity, ProfileActivity::class.java)
        startActivity(intent)
        finish() // Prevent user from coming back to the sign-in screen
    }

    private fun loginUser() {
        val email = emailEditText!!.getText().toString().trim { it <= ' ' }
        val password = passwordEditText!!.getText().toString().trim { it <= ' ' }

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Login user with Firebase Authentication
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener { task: Task<AuthResult?>? ->
                if (task!!.isSuccessful()) {
                    val user = mAuth!!.getCurrentUser()
                    if (user != null) {
                        db!!.collection("Users").document(user.getUid()).get()
                            .addOnSuccessListener(OnSuccessListener { documentSnapshot: DocumentSnapshot? ->
                                val userData = documentSnapshot!!.toObject<User?>(User::class.java)
                                if (userData != null) {
                                    navigateToMainActivity()
                                }
                            })
                    }
                } else {
                    Log.e("SignUpActivity", "Login Failed", task.getException())
                    Toast.makeText(
                        this,
                        "Login Failed: " + task.getException()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@SignInActivity, MainActivity::class.java)
        startActivity(intent)
        finish() // Prevent user from coming back to the sign-in screen
    }
}
