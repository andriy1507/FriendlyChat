package com.friendlychat.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.friendlychat.messages.NewMessagesActivity
import com.friendlychat.model.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sign_in_button.setOnClickListener {
            val email = email_editText_login.text.toString()
            val pass = pass_editText_login.text.toString()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener {

            }.addOnSuccessListener {
                val intent = Intent(this, NewMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this@LoginActivity,"Failed to log in", Toast.LENGTH_LONG).show()
            }
        }

        signUp_textView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}