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

     fun generateChatId(recipientId: String, userId: String): String {
        val ids = listOf(recipientId, userId).sorted() // Sort to make the chat ID order consistent
        return ids.joinToString("_")
    }
    fun getMessagesLiveData(currentUserId: String, recipientId: String): LiveData<MutableList<Message1>> {
        loadMessages(currentUserId, recipientId)
        return messagesLiveData
    }

     fun loadMessages(currentUserId: String, recipientId: String) {
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
    fun sendMessage(message: Message1) {
        // Generate chatId by sorting the user IDs to ensure consistent order
        val chatId = generateChatId(message.userId!!, message.recipientId!!)

        // Sort the IDs to make sure the userId is always the same order
        val sortedIds = chatId.split("_")
        val sortedUserId = sortedIds[0]  // Sorted userId (always the first part of the chatId)
        val sortedRecipientId = sortedIds[1]  // Sorted recipientId (always the second part of the chatId)

        // Determine the correct userId and recipientId based on sorted order
        val correctUserId = if (message.userId == sortedUserId) sortedUserId else sortedRecipientId
        val correctRecipientId = if (message.userId == sortedUserId) sortedRecipientId else sortedUserId

        val correctedMessage = Message1(
            text = message.text,
            userId = correctUserId,
            recipientId = correctRecipientId,
            userName = message.userName,
            firestoreTimestamp = message.firestoreTimestamp
        )


        // Save the message to Firestore under the correct chatId and message collection
        db.collection("Chats")
            .document(chatId)
            .collection("Messages")
            .add(correctedMessage)
            .addOnSuccessListener { Log.d("ChatViewModel", "Message sent successfully.") }
            .addOnFailureListener { e -> Log.e("ChatViewModel", "Error sending message", e) }
    }

}
