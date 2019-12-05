package com.friendlychat.model

import android.util.Log
import com.friendlychat.messages.NewConversationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.message_row.view.*
import kotlinx.android.synthetic.main.user_row.view.*

class MessageItem(val chatMessage: ChatMessage): Item<GroupieViewHolder>() {

    var chatPartnerUser:User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textView_messageRow.text = chatMessage.text

        val chatPartnerId: String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
        }else{
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                Log.d(NewConversationActivity.USER_KEY,chatPartnerUser.toString())
                viewHolder.itemView.userName_textView_messageRow.text = chatPartnerUser?.username

                val targetImageView = viewHolder.itemView.profileImage_circleImageView_messageRow
                Picasso.get().load(chatPartnerUser?.profileImageURL).into(targetImageView)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    override fun getLayout(): Int {
        return R.layout.message_row
    }
}