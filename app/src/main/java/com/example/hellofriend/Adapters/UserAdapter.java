package com.example.hellofriend.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellofriend.Activities.ChatActivity;
import com.example.hellofriend.MainActivity;
import com.example.hellofriend.Models.User1;
import com.example.hellofriend.R;
import com.squareup.picasso.Picasso;

import java.util.List;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User1> userList;

    public UserAdapter(Context context, List<User1> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User1 user = userList.get(position);

        // Set user name
        holder.userName.setText(user.getName() != null ? user.getName() : "Unknown User");

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Picasso.get()
                    .load(user.getProfileImageUrl())
                    .placeholder(R.drawable.ic_me)
                    .error(R.drawable.ic_me)
                    .into(holder.userImage);
        } else {
            holder.userImage.setImageResource(R.drawable.ic_me);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("userId", user.getUserId());
            intent.putExtra("name", user.getName());
            intent.putExtra("recipientImageUrl", user.getProfileImageUrl());
            context.startActivity(intent);
            Log.d("UserAdapter", "User clicked: " + user.getName());
            Log.d("UserAdapter", "User ID: " + user.getUserId());
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userImage = itemView.findViewById(R.id.userImage);
        }
    }
}
