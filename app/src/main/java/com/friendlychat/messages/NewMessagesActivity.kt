package com.friendlychat.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.friendlychat.auth.RegisterActivity
import com.friendlychat.model.ChatMessage
import com.friendlychat.model.MessageItem
import com.friendlychat.model.R
import com.friendlychat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_new_messages.*

class NewMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }

    val adapter = GroupAdapter<GroupieViewHolder>()
    val latestMessagesHashMap = HashMap<String, ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messages)
        title = "Conversations"

        verifyUserLogIn()

        listenLatestMessages()
        newMessages_recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter.setOnItemClickListener { item, view ->
            val row = item as MessageItem

            val user = row.chatPartnerUser

            Log.d(NewConversationActivity.USER_KEY, item.chatPartnerUser.toString())
            val intent = Intent(this, ChatLogActivity::class.java)
            intent.putExtra(NewConversationActivity.USER_KEY, user)
            startActivity(intent)
        }
    }

    private fun listenLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesHashMap[p0.key!!] = chatMessage
                refreshRecyclerView()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesHashMap[p0.key!!] = chatMessage
                refreshRecyclerView()
            }

            override fun onCancelled(p0: DatabaseError) = Unit
            override fun onChildMoved(p0: DataSnapshot, p1: String?) = Unit
            override fun onChildRemoved(p0: DataSnapshot) = Unit

        })

        newMessages_recyclerView.adapter = adapter
    }

    private fun refreshRecyclerView() {
        adapter.clear()
        latestMessagesHashMap.forEach {
            adapter.add(MessageItem(it.value))
        }
    }


    private fun verifyUserLogIn() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            val uid = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    currentUser = p0.getValue(User::class.java)
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.new_message_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.about_menu_item -> {
                Toast.makeText(this, "About setcion will be available later", Toast.LENGTH_LONG)
                    .show()
            }
            R.id.new_message_menu_item -> {
                startActivity(Intent(this, NewConversationActivity::class.java))
            }
            R.id.signout_menu_item -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return true
    }
}
