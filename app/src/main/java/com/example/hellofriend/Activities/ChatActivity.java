package com.example.hellofriend.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellofriend.Adapters.ChatAdapter;
import com.example.hellofriend.MainActivity;
import com.example.hellofriend.Models.Message1;
import com.example.hellofriend.Models.User;
import com.example.hellofriend.Models.User1;
import com.example.hellofriend.R;
import com.example.hellofriend.ViewModel.ChatViewModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message1> messageList;
    private EditText editText;
    private ImageButton sendButton;
    private ImageView backBtn, imageUser, phoneCall, videoCall;
    private TextView recipientName;

    private FirebaseAuth mAuth;
    private String currentUserId;
    private String userId;
    private String userName;
    private String recipientImageUrl;

    private ChatViewModel chatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get user details from intent
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("name");
        recipientImageUrl = getIntent().getStringExtra("recipientImageUrl");

        Log.d("ChatActivity", "User Name " + userName);
        System.out.println("ChatActivity" + "User Name " +userName);
        // Validate required data
        if (userId == null || userName == null) {
            Toast.makeText(this, "Chat recipient information is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        chatRecyclerView = findViewById(R.id.chat_recycler);
        editText = findViewById(R.id.editT);
        sendButton = findViewById(R.id.send_message);
        backBtn = findViewById(R.id.icon_right);
        recipientName = findViewById(R.id.name_chat);
        imageUser = findViewById(R.id.image_user);
        phoneCall = findViewById(R.id.phone_call);
        videoCall = findViewById(R.id.video_call);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUserId = mAuth.getCurrentUser().getUid();

        // Set up RecyclerView
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter((ArrayList<Message1>) messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        setTitle("Chat with " + userName);

        // Initialize Chat ViewModel
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        //Observe messages from ViewModel in real-time
       //chatViewModel.loadMessagesRealTime(currentUserId, userId);
        chatViewModel.getMessagesLiveData(currentUserId, userId).observe(this, messages -> {
            messageList.clear();
            if (messages != null) {
                messageList.addAll(messages);
            }
            chatAdapter.notifyDataSetChanged();
            chatRecyclerView.scrollToPosition(messageList.size() - 1);
        });

        // Set recipient info
        recipientName.setText(userName != null ? userName : "Unknown User");
        if (recipientImageUrl != null && !recipientImageUrl.isEmpty()) {
            Picasso.get()
                    .load(recipientImageUrl)
                    .placeholder(R.drawable.ic_me)
                    .into(imageUser);
        } else {
            imageUser.setImageResource(R.drawable.ic_me);
        }

        videoCall.setOnClickListener(v -> videoCallHandling());
        phoneCall.setOnClickListener(v -> phoneCallHandling());
        backBtn.setOnClickListener(v -> backHome());


        sendButton.setOnClickListener(v -> sendMessage());

        // Make drawable icons clickable
        editText.setOnTouchListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.LEFT) {
                   openMediaPicker();
                } else if (target == DrawablePosition.RIGHT) {
                    openMediaPicker();
                }
            }
        });
    }

    private void openMediaPicker() {
        ImagePicker.with(this)
                .crop()
                .maxResultSize(1080, 1080)
                .compress(1024)
                .start(1);
    }

    private void videoCallHandling() {
    }

    private void phoneCallHandling() {
    }

    private void backHome() {
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        intent.putExtra("name", userName);
        intent.putExtra("userID", userId);
        intent.putExtra("recipientImageUrl", recipientImageUrl);
        startActivity(intent);
    }

    private void sendMessage() {
        String messageText = editText.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserName = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        if (currentUserName == null) {
            currentUserName = "Unknown User";
        }


       // User user = new User();
       // String myName = user.getName();
        // Create a new message
        Message1 message = new Message1();
        message.setText(messageText);
        message.setUserId(currentUserId);
        message.setRecipientId(userId);
       //message.setUserName(myName);
        message.setFirestoreTimestamp(Timestamp.now());

        // Add to local list and notify adapter
        messageList.add(message);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);

        // Send to Firestore
        chatViewModel.sendMessage(message);

        // Clear input field
        editText.setText("");
    }
}
