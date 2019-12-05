package com.friendlychat.model

import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row.view.*

class UserItem(val user: User) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.userName_textView_userRow.text = user.username
        Picasso.get().load(user.profileImageURL).into(viewHolder.itemView.profileImage_circleImageView_userRow)
    }
}