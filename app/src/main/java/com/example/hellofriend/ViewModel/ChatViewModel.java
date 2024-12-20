package com.example.hellofriend.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hellofriend.Models.Message1;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final MutableLiveData<List<Message1>> messagesLiveData = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Message1>> getMessagesLiveData(String currentUserId, String recipientId) {
        if (currentUserId == null || recipientId == null) {
            messagesLiveData.setValue(new ArrayList<>()); // Handle null inputs gracefully
            return messagesLiveData;
        }

        loadMessages(currentUserId, recipientId);
        return messagesLiveData;
    }

    private void loadMessages(String currentUserId, String recipientId) {
        if (currentUserId == null || recipientId == null) {
            // Handle null inputs gracefully
            messagesLiveData.setValue(Collections.emptyList());
            return;
        }

        db.collection("Messages")
                .whereIn("userId", List.of(currentUserId, recipientId))  // Get messages for both users
                .whereIn("recipientId", List.of(currentUserId, recipientId))
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }

                    if (querySnapshot != null) {
                        List<Message1> messages = new ArrayList<>();
                        for (var doc : querySnapshot.getDocuments()) {
                            Message1 message = doc.toObject(Message1.class);
                            if (message != null) {
                                messages.add(message);
                            }
                        }
                        messagesLiveData.setValue(messages);
                    }
                });
    }


    public void sendMessage(Message1 message) {
        if (message == null) {
            return; // Prevent sending a null message
        }

        db.collection("Messages").add(message)
                .addOnSuccessListener(documentReference -> {
                    // Log success if needed
                    Log.d("ChatViewModel", "Message sent successfully.");
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.e("ChatViewModel", "Error sending message: " + e.getMessage());
                });
    }

}
