package com.friendlychat.messages

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.friendlychat.model.R
import com.friendlychat.model.User
import com.friendlychat.model.UserItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_new_conversation.*


class NewConversationActivity : AppCompatActivity() {
    companion object{
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_conversation)
        title = "New conversation"




        fetchUsers()
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    val user = it.getValue(User::class.java)

                    if(user!=null)
                       adapter.add(UserItem(user))
                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(this@NewConversationActivity, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)

                    finish()
                }

                newConversation_recyclerView.adapter = adapter
            }
        })
    }
}
