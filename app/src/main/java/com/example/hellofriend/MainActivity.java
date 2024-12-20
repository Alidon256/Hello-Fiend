package com.example.hellofriend;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellofriend.Adapters.UserAdapter;
import com.example.hellofriend.Models.User1;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User1> userList;
    private FirebaseFirestore db;
    private ImageView cameraHome, menuHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Initialize UI components
            cameraHome = findViewById(R.id.camera_home);
            menuHome = findViewById(R.id.menu_home);
            recyclerView = findViewById(R.id.recyclerView);

            // Initialize RecyclerView
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Initialize Firestore and data list
            db = FirebaseFirestore.getInstance();
            userList = new ArrayList<>();

            // Set up Adapter
            userAdapter = new UserAdapter(this, userList);
            recyclerView.setAdapter(userAdapter);

            // Fetch user data from Firestore
            fetchUsers();

            // Set listeners for UI interactions
            setupClickListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing MainActivity", e);
            Toast.makeText(this, "An error occurred during initialization", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sets up click listeners for the camera and menu buttons.
     */
    private void setupClickListeners() {
        cameraHome.setOnClickListener(v -> {
            Log.d(TAG, "Camera icon clicked");
            try {
                ImagePicker.with(this)
                        .cameraOnly()
                        .start();
            } catch (Exception e) {
                Log.e(TAG, "Error launching camera", e);
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show();
            }
        });

        menuHome.setOnClickListener(v -> {
            Log.d(TAG, "Menu icon clicked");
            Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Fetches users from the Firestore collection and updates the RecyclerView.
     */
    private void fetchUsers() {
        Log.d(TAG, "Fetching users from Firestore");
        db.collection("userProfileInfo")
                .get()
                .addOnSuccessListener(this::handleSuccess)
                .addOnFailureListener(this::handleFailure);
    }

    /**
     * Handles the successful fetch of user data.
     *
     * @param queryDocumentSnapshots The Firestore query results.
     */
    private void handleSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
        Log.d(TAG, "Successfully fetched users from Firestore");
        try {
            userList.clear();
            userList.addAll(queryDocumentSnapshots.toObjects(User1.class));
            userAdapter.notifyDataSetChanged();
            Log.d(TAG, "User list updated with " + userList.size() + " users");
        } catch (Exception e) {
            Log.e(TAG, "Error parsing user data", e);
            Toast.makeText(this, "Error parsing user data", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles errors when fetching user data.
     *
     * @param e The exception thrown during the fetch.
     */
    private void handleFailure(@NonNull Exception e) {
        Log.e(TAG, "Error fetching users", e);
        if (e instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException firestoreException = (FirebaseFirestoreException) e;
            Toast.makeText(this, "Firestore Error: " + firestoreException.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show();
        }
    }
}
