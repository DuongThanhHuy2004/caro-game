package com.example.caro.model

data class OnlineRoom(
    val roomId: String = "",
    val hostId: String = "",
    val guestId: String = "",
    val board: List<List<Int>> = List(15) { List(15) { 0 } }, // 0=empty,1=X,2=O
    val currentPlayer: Int = 1, // 1=X(host), 2=O(guest)
    val winner: Int = 0, // 0=none,1=X,2=O,3=draw
    val lastMoveRow: Int = -1,
    val lastMoveCol: Int = -1,
    val status: String = "waiting" // waiting, playing, finished
)

data class ChatMessage(
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)