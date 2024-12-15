package com.example.hellofriend.Activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellofriend.Adapters.ChatAdapter;
import com.example.hellofriend.Models.Message;
import com.example.hellofriend.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messageList;
    private EditText editText;
    private ImageButton sendButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chat_recycler);
        editText = findViewById(R.id.editText);
        sendButton = findViewById(R.id.send_message);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        loadMessages();

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        CollectionReference messagesRef = db.collection("Messages");
        messagesRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }

            if (queryDocumentSnapshots != null) {
                messageList.clear();
                messageList.addAll(queryDocumentSnapshots.toObjects(Message.class));
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    private void sendMessage() {
        String messageText = editText.getText().toString();
        if (!messageText.isEmpty()) {
            String userId = mAuth.getCurrentUser().getUid();
            String userName = mAuth.getCurrentUser().getDisplayName();

            Map<String, Object> message = new HashMap<>();
            message.put("text", messageText);
            message.put("userId", userId);
            message.put("userName", userName);
            message.put("timestamp", System.currentTimeMillis());

            db.collection("Messages").add(message);
            editText.setText("");
        }
    }
}
