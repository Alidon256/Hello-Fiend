package com.example.hellofriend.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase services
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize views from XML layout
        userName = findViewById(R.id.etname);
        userEmailAddress = findViewById(R.id.etemail);
        userAddress = findViewById(R.id.etUserAdress);
        userContact = findViewById(R.id.etUserContact);
        userDateOfBirth = findViewById(R.id.etUserDateOfBirth);
        userProfileImage = findViewById(R.id.Al_Doctor); // Profile image
        genderRadioGroup = findViewById(R.id.radio_gender);

        // Handle profile image click to choose a new image
        userProfileImage.setOnClickListener(v -> openFileChooser());

        userDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        // Handle update button click to save profile info
        findViewById(R.id.btnLogin_user).setOnClickListener(v -> {
            uploadImageToFirebase(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), this::saveUserInfoToFirestore);
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date and set it in the EditText
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    userDateOfBirth.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    // Open file chooser to select an image
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
            imageUri = data.getData(); // Retrieve the image URI
            Picasso.get().load(imageUri).into(userProfileImage); // Set the selected image to the ImageView
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    // Upload the selected image to Firebase Storage and get its URL
    private void uploadImageToFirebase(String userId, OnSuccessListener<String> onSuccess) {
        if (imageUri != null) {
            StorageReference fileRef = storage.getReference("users/" + userId + "/" + UUID.randomUUID().toString());
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> onSuccess.onSuccess(uri.toString())))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            onSuccess.onSuccess(""); // No image uploaded, pass empty URL
        }
    }

    // Save user information to Firestore
    private void saveUserInfoToFirestore(String imageUrl) {
        // Get selected gender from RadioGroup
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        String gender = "";
        if (selectedGenderId != -1) {
            RadioButton selectedGenderButton = findViewById(selectedGenderId);
            gender = selectedGenderButton.getText().toString();
        }
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Create a map with the user's profile information
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

        // Add the document to Firestore
        db.collection("userProfileInfo")
                .document(userId)
                .set(userData)
                .addOnSuccessListener(documentReference -> {
                    // Start the main activity with the generated profileId
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.putExtra("profileId", userId);
                    intent.putExtra("name", userName.getText().toString());
                    intent.putExtra("profileImageUrl", imageUrl);
                    startActivity(intent);
                    Log.d("UserProfileInfo", "Profile updated successfully with ID: " + userId);
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show();
                });
    }
}
