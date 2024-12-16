package com.example.hellofriend.Activities;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hellofriend.Models.Message1;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMessage(Message1 message);

    @Query("SELECT * FROM messages WHERE (userId = :currentUserId AND recipientId = :otherUserId) OR (userId = :otherUserId AND recipientId = :currentUserId) ORDER BY timestamp ASC")
    LiveData<List<Message1>> getChatMessages(String currentUserId, String otherUserId);
}
