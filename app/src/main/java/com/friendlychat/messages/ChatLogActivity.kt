package com.friendlychat.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.friendlychat.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        toUser = intent.getParcelableExtra(NewConversationActivity.USER_KEY)
        Log.d(NewConversationActivity.USER_KEY, toUser.toString())
        supportActionBar?.title = toUser?.username

        newMessages_recyclerView.adapter = adapter


        sendMessage_button.setOnClickListener {
            performSendMessage()
        }

        listenForMessages()

        newMessages_recyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {


                val chatMessage: ChatMessage? = p0.getValue(ChatMessage::class.java)

                if (chatMessage?.fromId == FirebaseAuth.getInstance().uid) {
                    if (chatMessage == null) return
                    val currentUser = NewMessagesActivity.currentUser ?: return
                    adapter.add(ChatToItem(chatMessage, currentUser))
                } else {
                    if (chatMessage == null) return
                    adapter.add(ChatFromItem(chatMessage, toUser!!))
                }
                newMessages_recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    private fun performSendMessage() {

        if (message_editText.text.toString() == "") return

        val user = intent.getParcelableExtra<User>(NewConversationActivity.USER_KEY)
        val toId = user.uid
        val fromId = FirebaseAuth.getInstance().uid
        val text = message_editText.text.toString()
        val id = UUID.randomUUID().toString()
        if (fromId == null) return
        val chatMessage = ChatMessage(id, toId, fromId, text)
        val ref = FirebaseDatabase.getInstance()
            .getReference("/user-messages/$fromId/$toId/${chatMessage.timestamp}=$id")
        val toRef = FirebaseDatabase.getInstance()
            .getReference("/user-messages/$toId/$fromId/${chatMessage.timestamp}=$id")
        val latestMessageRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        val latestToMessageRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")

        latestToMessageRef.setValue(chatMessage)
        latestMessageRef.setValue(chatMessage)
        ref.setValue(chatMessage)
        toRef.setValue(chatMessage)
        message_editText.text = null
        message_editText.text.clear()
    }
}
