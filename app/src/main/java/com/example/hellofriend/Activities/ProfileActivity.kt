package com.example.hellofriend.Activities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.hellofriend.MainActivity
import com.example.hellofriend.R
import com.example.hellofriend.WebpTranscoder
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.util.Calendar
import java.util.HashMap
import java.util.Objects
import java.util.UUID

class ProfileActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var imageUri: Uri? = null
    private var userName: TextInputEditText? = null
    private var userEmailAddress: TextInputEditText? = null
    private var userAddress: TextInputEditText? = null
    private var userContact: TextInputEditText? = null
    private var userDateOfBirth: TextInputEditText? = null
    private var userProfileImage: ImageView? = null
    private var genderRadioGroup: RadioGroup? = null
    private var logInUser: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase services
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Initialize views
        userName = findViewById<TextInputEditText>(R.id.etname)
        userEmailAddress = findViewById<TextInputEditText>(R.id.etemail)
        userAddress = findViewById<TextInputEditText>(R.id.etUserAdress)
        userContact = findViewById<TextInputEditText>(R.id.etUserContact)
        userDateOfBirth = findViewById<TextInputEditText>(R.id.etUserDateOfBirth)
        userProfileImage = findViewById<ImageView>(R.id.Al_Doctor)
        genderRadioGroup = findViewById<RadioGroup>(R.id.radio_gender)
        logInUser = findViewById<Button>(R.id.btnLogin_user)

        // Open image picker on profile image click
       // userProfileImage!!.setOnClickListener(View.OnClickListener { v: View? -> openFileChooser() })

        // Open date picker dialog for date of birth field
        userDateOfBirth!!.setOnClickListener(View.OnClickListener { v: View? -> showDatePickerDialog() })

        // Handle Save/Update button click
        logInUser!!.setOnClickListener(View.OnClickListener { v: View? ->
            if (validateInputs()) {
                    Objects.requireNonNull<FirebaseUser?>(mAuth!!.currentUser).uid
                    saveUserInfoToFirestore()
            }
        })
        /*logInUser!!.setOnClickListener(View.OnClickListener { v: View? ->
            if (validateInputs()) {
                uploadImageToFirebase(
                    Objects.requireNonNull<FirebaseUser?>(mAuth!!.currentUser).uid,
                    OnSuccessListener { imageUrl: String? -> this.saveUserInfoToFirestore(imageUrl) })
            }
        })*/
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            OnDateSetListener { view: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate =
                    selectedDay.toString() + "/" + (selectedMonth + 1) + "/" + selectedYear
                userDateOfBirth!!.setText(selectedDate)
            }, year, month, day
        )

        datePickerDialog.show()
    }

   /* private fun openFileChooser() {
        ImagePicker.with(this)
            .crop() // Crop image (optional)
            .compress(1024) // Compress image size (optional)
            .maxResultSize(1080, 1080) // Max result size (optional)
            .start(PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData()
            Glide
                .with(this)
                .asBitmap()
                .load(imageUri)
                .apply(RequestOptions.bitmapTransform(WebpTranscoder()))
                .into(userProfileImage!!)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebase(userId: String?, onSuccess: OnSuccessListener<String?>) {
        if (imageUri != null) {
            val fileRef =
                storage!!.getReference("users/" + userId + "/" + UUID.randomUUID().toString())
            fileRef.putFile(imageUri!!)
                .addOnSuccessListener(OnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                    fileRef.getDownloadUrl()
                        .addOnSuccessListener(OnSuccessListener { uri: Uri? ->
                            onSuccess.onSuccess(
                                uri.toString()
                            )
                        })
                })
                .addOnFailureListener(OnFailureListener { e: Exception? ->
                    Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileActivity", "Image upload error", e)
                })
        } else {
            onSuccess.onSuccess("") // No image uploaded, pass empty URL
        }
    }*/

    private fun saveUserInfoToFirestore() {
        val selectedGenderId = genderRadioGroup!!.checkedRadioButtonId
        val gender = if (selectedGenderId != -1)
            (findViewById<View?>(selectedGenderId) as RadioButton).getText().toString()
        else
            "Not Specified"

        val userId = Objects.requireNonNull<FirebaseUser?>(mAuth!!.currentUser).uid

        val userData: MutableMap<String?, Any?> = HashMap<String?, Any?>()
        userData.put("userId", userId)
        userData.put("name", Objects.requireNonNull<Editable?>(userName!!.getText()).toString().trim { it <= ' ' })
        userData.put("email", Objects.requireNonNull<Editable?>(userEmailAddress!!.getText()).toString().trim { it <= ' ' })
        userData.put("address", Objects.requireNonNull<Editable?>(userAddress!!.getText()).toString().trim { it <= ' ' })
        userData.put("contact", Objects.requireNonNull<Editable?>(userContact!!.getText()).toString().trim { it <= ' ' })
        userData.put("dateOfBirth", Objects.requireNonNull<Editable?>(userDateOfBirth!!.getText()).toString().trim { it <= ' ' })
        userData.put("gender", gender)
        userData.put("userRole", "User")
        //userData.put("profileImageUrl", imageUrl)

        db!!.collection("userProfileInfo")
            .document(userId)
            .set(userData)
            .addOnSuccessListener(OnSuccessListener { aVoid: Void? ->
                val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                intent.putExtra("userId", userId)
                intent.putExtra("name", userName!!.getText().toString())
                //intent.putExtra("profileImageUrl", imageUrl)
                startActivity(intent)
                Log.d("UserProfileInfo", "Profile updated successfully with ID: $userId")
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            })
            .addOnFailureListener(OnFailureListener { e: Exception? ->
                Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show()
                Log.e("UserProfileInfo", "Firestore update error", e)
            })
    }

    private fun validateInputs(): Boolean {
        if (userName!!.getText().toString().trim { it <= ' ' }.isEmpty()) {
            userName!!.error = "Name is required"
            return false
        }
        if (userEmailAddress!!.getText().toString().trim { it <= ' ' }.isEmpty()) {
            userEmailAddress!!.error = "Email is required"
            return false
        }
        if (userAddress!!.getText().toString().trim { it <= ' ' }.isEmpty()) {
            userAddress!!.error = "Address is required"
            return false
        }
        if (userContact!!.getText().toString().trim { it <= ' ' }.isEmpty()) {
            userContact!!.error = "Contact is required"
            return false
        }
        if (userDateOfBirth!!.getText().toString().trim { it <= ' ' }.isEmpty()) {
            userDateOfBirth!!.error = "Date of Birth is required"
            return false
        }
        return true
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
