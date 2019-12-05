package com.friendlychat.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.friendlychat.messages.NewMessagesActivity
import com.friendlychat.model.R
import com.friendlychat.model.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {
    companion object{
        val EMPTY_USER_PIC ="https://firebasestorage.googleapis.com/v0/b/friendly-chat-99119.appspot.com/o/empty-user-pic.png?alt=media&token=8b35febd-9719-47b8-953c-88677e6190ea"
    }

    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sign_up_button.setOnClickListener {
            if (performRegister()) return@setOnClickListener
        }

        signIn_textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        choose_image_button_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            profile_circleImageView.setImageBitmap(bitmap)
            choose_image_button_register.alpha = 0f


        }
    }

    private fun performRegister(): Boolean {
        val email = email_edittext_register.text.toString()
        val pass = pass_editText_register.text.toString()
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Enter e-mail and password", Toast.LENGTH_LONG).show()
            return true
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener {

            if (!it.isSuccessful)
                return@addOnCompleteListener

            if (it.isSuccessful) {
                Log.d("Firebase","Successfully created user with UID:${FirebaseAuth.getInstance().uid}")
                if(selectedPhotoUri!=null) {
                    uploadImageToFirebaseStorage()
                }else{
                    saveUserToFirebase(EMPTY_USER_PIC)
                }
                Log.d("RegisterActivity", "Successfully created user with UID:${it.result?.user?.uid}")
                val intent = Intent(this@RegisterActivity, NewMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }.addOnFailureListener {
            Log.d("Firebase", "Failed to create user")
            Toast.makeText(this@RegisterActivity, "Failed to create user${it.message}", Toast.LENGTH_LONG).show()

        }
        return false
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("Firebase","Successfully uploaded profile image with URI: $ref")
            ref.downloadUrl.addOnSuccessListener {
                saveUserToFirebase(it.toString())
            }
        }
    }

    private fun saveUserToFirebase(profileImageURL: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(
            uid,
            username_editText_register.text.toString(),
            profileImageURL
        )
        ref.setValue(user).addOnSuccessListener {
            Log.d("Firebase","Successfully added user with UID: $uid")
        }.addOnFailureListener {
            Log.d("Firebase","${it.message}")
        }
    }
}


