package com.friendlychat.model

import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatFromItem(val message: ChatMessage,val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textView_chatFromRow.text = message.text

        val uri = user.profileImageURL
        val targetImageView = viewHolder.itemView.profileImage_circleImageView_chatFromRow
        Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}class ChatToItem(val message: ChatMessage,val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textView_chatToRow.text = message.text

        val uri = user.profileImageURL
        val targetImageView = viewHolder.itemView.profileImage_circleImageView_chatToRow
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}