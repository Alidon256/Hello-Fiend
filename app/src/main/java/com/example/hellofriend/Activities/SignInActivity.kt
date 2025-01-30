package com.example.hellofriend.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hellofriend.MainActivity;
import com.example.hellofriend.Models.User;
import com.example.hellofriend.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailEditText, passwordEditText, nameEditText;
    private Button registerButton;
    private TextView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already signed in
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, navigate to MainActivity
            navigateToMainActivity();
            return;
        }

        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Bind UI elements
        emailEditText = findViewById(R.id.etEmail);
        passwordEditText = findViewById(R.id.etPass);
        nameEditText = findViewById(R.id.nameEditText);
        registerButton = findViewById(R.id.btnSignUp);
        loginButton = findViewById(R.id.tvLogIn);

        // Set up listeners
        registerButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> loginUser());
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            User newUser = new User(userId, name, email);

                            // Save user data to Firestore
                            db.collection("Users")
                                    .document(userId)
                                    .set(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show();
                                        navigateToProfileActivity();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("SignUpActivity", "Error registering user", e);
                                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Log.e("SignUpActivity", "Registration Failed", task.getException());
                        Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToProfileActivity() {
        Intent intent = new Intent(SignInActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish(); // Prevent user from coming back to the sign-in screen
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Login user with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            db.collection("Users").document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        User userData = documentSnapshot.toObject(User.class);
                                        if (userData != null) {
                                            navigateToMainActivity();
                                        }
                                    });
                        }
                    } else {
                        Log.e("SignUpActivity", "Login Failed", task.getException());
                        Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Prevent user from coming back to the sign-in screen
    }
}
