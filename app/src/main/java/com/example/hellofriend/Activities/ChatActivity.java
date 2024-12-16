package com.example.hellofriend.Activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellofriend.Adapters.ChatAdapter;
import com.example.hellofriend.Models.Message1;
import com.example.hellofriend.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<Message1> messageList; // Updated to Message1
    private EditText editText;
    private ImageButton sendButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String userId; // The recipient's userId
    private String userName; // The recipient's userName
    private String currentUserId; // The sender's userId

    private MessageDao messageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Retrieve user details from intent
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");

        chatRecyclerView = findViewById(R.id.chat_recycler);
        editText = findViewById(R.id.editT);
        sendButton = findViewById(R.id.send_message);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid(); // Get the sender's userId
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList); // Use Message1 here

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        setTitle("Chat with " + userName);

        // Initialize Room Database
        messageDao = AppDatabase.getInstance(this).messageDao();

        // Load Messages
        loadMessages();

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        // Load messages from Room Database
        LiveData<List<Message1>> liveMessages = messageDao.getChatMessages(currentUserId, userId);
        liveMessages.observe(this, messages -> {
            messageList.clear();
            messageList.addAll(messages);
            chatAdapter.notifyDataSetChanged();
        });

        // Sync messages with Firestore if network is available
        if (isNetworkAvailable()) {
            syncMessagesWithFirestore();
        }
    }

    private void syncMessagesWithFirestore() {
        db.collection("Messages")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("recipientId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> Executors.newSingleThreadExecutor().execute(() -> {
                    for (var doc : querySnapshot.getDocuments()) {
                        Message1 message = doc.toObject(Message1.class);

                        if (message != null) {
                            // Insert message into Room database if it does not already exist
                            messageDao.insertMessage(message);
                        }
                    }
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to sync messages", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendMessage() {
        String messageText = editText.getText().toString();
        if (!messageText.isEmpty()) {
            String currentUserName = mAuth.getCurrentUser().getDisplayName();

            // Create a new Message1 object to be inserted into Room Database
            Message1 message = new Message1();
            message.setText(messageText);
            message.setUserId(currentUserId);
            message.setRecipientId(userId);
            message.setUserName(currentUserName);
            message.setTimestamp(System.currentTimeMillis());

            // Insert into Room Database on a background thread
            Executors.newSingleThreadExecutor().execute(() -> messageDao.insertMessage(message));

            // Sync with Firestore if network is available
            if (isNetworkAvailable()) {
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("text", messageText);
                messageMap.put("userId", currentUserId);
                messageMap.put("recipientId", userId);
                messageMap.put("userName", currentUserName);
                messageMap.put("timestamp", System.currentTimeMillis());

                db.collection("Messages").add(messageMap);
            }

            editText.setText(""); // Clear the input field
        } else {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
