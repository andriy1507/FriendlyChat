package com.friendlychat.model

class ChatMessage(
    val id: String,
    val toId: String,
    val fromId: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", -1)
}