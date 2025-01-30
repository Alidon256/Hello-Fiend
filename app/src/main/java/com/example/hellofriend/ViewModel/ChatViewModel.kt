package com.example.hellofriend.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hellofriend.Models.Message1
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception
import java.util.ArrayList
import java.util.List

class ChatViewModel : ViewModel() {
    private val messagesLiveData = MutableLiveData<MutableList<Message1?>?>()
    private val db = FirebaseFirestore.getInstance()

    fun getMessagesLiveData(
        currentUserId: String?,
        recipientId: String?
    ): LiveData<MutableList<Message1?>?> {
        if (currentUserId == null || recipientId == null) {
            messagesLiveData.value = ArrayList<Message1?>() // Handle null inputs gracefully
            return messagesLiveData
        }

        loadMessages(currentUserId, recipientId)
        return messagesLiveData
    }

    fun loadMessages(currentUserId: String?, recipientId: String?) {
        if (currentUserId == null || recipientId == null) {
            // Handle null inputs gracefully
            messagesLiveData.value = mutableListOf<Message1?>()
            return
        }

        db.collection("Messages")
            .whereIn(
                "userId",
                listOf<String?>(currentUserId, recipientId)
            ) // Get messages for both users
            .whereIn("recipientId", listOf<String?>(currentUserId, recipientId))
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener(EventListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                if (e != null) {
                    e.printStackTrace()
                    return@EventListener
                }
                if (querySnapshot != null) {
                    val messages: MutableList<Message1?> = ArrayList<Message1?>()
                    for (doc in querySnapshot.getDocuments()) {
                        val message = doc.toObject<Message1?>(Message1::class.java)
                        if (message != null) {
                            messages.add(message)
                        }
                    }
                    messagesLiveData.value = messages
                }
            })
    }


    fun sendMessage(message: Message1?) {
        if (message == null) {
            return  // Prevent sending a null message
        }

        db.collection("Messages").add(message)
            .addOnSuccessListener(OnSuccessListener { documentReference: DocumentReference? ->
                           Log.d("ChatViewModel", "Message sent successfully.")
            })
            .addOnFailureListener(OnFailureListener { e: Exception? ->
                e!!.printStackTrace()
                Log.e("ChatViewModel", "Error sending message: " + e.message)
            })
    }
}
