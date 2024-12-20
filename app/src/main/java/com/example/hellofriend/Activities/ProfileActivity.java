package com.example.hellofriend.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hellofriend.MainActivity;
import com.example.hellofriend.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri imageUri;
    private TextInputEditText userName, userEmailAddress, userAddress, userContact, userDateOfBirth;
    private ImageView userProfileImage;
    private RadioGroup genderRadioGroup;
    private Button logInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase services
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize views
        userName = findViewById(R.id.etname);
        userEmailAddress = findViewById(R.id.etemail);
        userAddress = findViewById(R.id.etUserAdress);
        userContact = findViewById(R.id.etUserContact);
        userDateOfBirth = findViewById(R.id.etUserDateOfBirth);
        userProfileImage = findViewById(R.id.Al_Doctor);
        genderRadioGroup = findViewById(R.id.radio_gender);
        logInUser = findViewById(R.id.btnLogin_user);

        // Open image picker on profile image click
        userProfileImage.setOnClickListener(v -> openFileChooser());

        // Open date picker dialog for date of birth field
        userDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        // Handle Save/Update button click
        logInUser.setOnClickListener(v -> {
            if (validateInputs()) {
                uploadImageToFirebase(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), this::saveUserInfoToFirestore);
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    userDateOfBirth.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void openFileChooser() {
        ImagePicker.with(this)
                .crop()                    // Crop image (optional)
                .compress(1024)            // Compress image size (optional)
                .maxResultSize(1080, 1080) // Max result size (optional)
                .start(PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(userProfileImage);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebase(String userId, OnSuccessListener<String> onSuccess) {
        if (imageUri != null) {
            StorageReference fileRef = storage.getReference("users/" + userId + "/" + UUID.randomUUID().toString());
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> onSuccess.onSuccess(uri.toString())))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                        Log.e("ProfileActivity", "Image upload error", e);
                    });
        } else {
            onSuccess.onSuccess(""); // No image uploaded, pass empty URL
        }
    }

    private void saveUserInfoToFirestore(String imageUrl) {
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        String gender = selectedGenderId != -1
                ? ((RadioButton) findViewById(selectedGenderId)).getText().toString()
                : "Not Specified";

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("name", Objects.requireNonNull(userName.getText()).toString().trim());
        userData.put("email", Objects.requireNonNull(userEmailAddress.getText()).toString().trim());
        userData.put("address", Objects.requireNonNull(userAddress.getText()).toString().trim());
        userData.put("contact", Objects.requireNonNull(userContact.getText()).toString().trim());
        userData.put("dateOfBirth", Objects.requireNonNull(userDateOfBirth.getText()).toString().trim());
        userData.put("gender", gender);
        userData.put("userRole", "User");
        userData.put("profileImageUrl", imageUrl);

        db.collection("userProfileInfo")
                .document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("name", userName.getText().toString());
                    intent.putExtra("profileImageUrl", imageUrl);
                    startActivity(intent);
                    Log.d("UserProfileInfo", "Profile updated successfully with ID: " + userId);
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show();
                    Log.e("UserProfileInfo", "Firestore update error", e);
                });
    }

    private boolean validateInputs() {
        if (userName.getText().toString().trim().isEmpty()) {
            userName.setError("Name is required");
            return false;
        }
        if (userEmailAddress.getText().toString().trim().isEmpty()) {
            userEmailAddress.setError("Email is required");
            return false;
        }
        if (userAddress.getText().toString().trim().isEmpty()) {
            userAddress.setError("Address is required");
            return false;
        }
        if (userContact.getText().toString().trim().isEmpty()) {
            userContact.setError("Contact is required");
            return false;
        }
        if (userDateOfBirth.getText().toString().trim().isEmpty()) {
            userDateOfBirth.setError("Date of Birth is required");
            return false;
        }
        return true;
    }
}
