package com.example.hellofriend.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hellofriend.Models.Message1
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatViewModel : ViewModel() {

    private val messagesLiveData = MutableLiveData<MutableList<Message1>>()
    private val db = FirebaseFirestore.getInstance()

    // Generate a unique chat ID based on user IDs
    private fun generateChatId(userId: String, recipientId: String): String {
        val ids = listOf(userId, recipientId).sorted() // Sort to make the chat ID order consistent
        return ids.joinToString("_")
    }

    // Fetch messages for a specific chat
    fun getMessagesLiveData(currentUserId: String, recipientId: String): LiveData<MutableList<Message1>> {
        loadMessages(currentUserId, recipientId)
        return messagesLiveData
    }

    // Load messages from Firestore for a specific chat
    private fun loadMessages(currentUserId: String, recipientId: String) {
        val chatId = generateChatId(currentUserId, recipientId)

        db.collection("Chats")
            .document(chatId) // Use the generated chatId
            .collection("Messages")
            .orderBy("firestoreTimestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.e("ChatViewModel", "Error fetching messages", e)
                    return@addSnapshotListener
                }
                val messages = mutableListOf<Message1>()
                querySnapshot?.documents?.forEach { doc ->
                    doc.toObject(Message1::class.java)?.let { messages.add(it) }
                }
                messagesLiveData.value = messages
            }
    }

    // Send message to FireStore under a specific chat
    fun sendMessage(message: Message1) {
        val chatId = generateChatId(message.userId!!, message.recipientId!!)

        db.collection("Chats") // Chats collection
            .document(chatId) // Use chatId as the document name
            .collection("Messages") // Store messages in a subcollection
            .add(message)
            .addOnSuccessListener { Log.d("ChatViewModel", "Message sent successfully.") }
            .addOnFailureListener { e -> Log.e("ChatViewModel", "Error sending message", e) }
    }
}
