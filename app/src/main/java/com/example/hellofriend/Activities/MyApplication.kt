package com.example.hellofriend.Activities

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Enable Firestore offline persistence
        FirebaseFirestore.getInstance().setFirestoreSettings(
            FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        )
    }
}
