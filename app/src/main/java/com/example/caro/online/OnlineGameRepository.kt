package com.example.caro.online

import com.example.caro.model.ChatMessage
import com.example.caro.model.OnlineRoom
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class OnlineGameRepository {
    private val db = Firebase.database("https://caro-6ba01-default-rtdb.asia-southeast1.firebasedatabase.app").reference
    private val playerId = UUID.randomUUID().toString().take(8)

    fun getPlayerId() = playerId

    fun generateRoomId(): String = (100000..999999).random().toString()

    suspend fun createRoom(roomId: String): Result<String> = runCatching {
        val room = OnlineRoom(
            roomId = roomId,
            hostId = playerId,
            status = "waiting"
        )
        db.child("rooms").child(roomId).setValue(room).await()
        playerId
    }

    suspend fun joinRoom(roomId: String): Result<String> = runCatching {
        val snapshot = db.child("rooms").child(roomId).get().await()
        val room = snapshot.getValue(OnlineRoom::class.java)
            ?: error("Phòng không tồn tại")
        if (room.status != "waiting") error("Phòng đã đầy")
        db.child("rooms").child(roomId).child("guestId").setValue(playerId).await()
        db.child("rooms").child(roomId).child("status").setValue("playing").await()
        playerId
    }

    fun observeRoom(roomId: String): Flow<OnlineRoom> = callbackFlow {
        val ref = db.child("rooms").child(roomId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(OnlineRoom::class.java)?.let { trySend(it) }
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeChat(roomId: String): Flow<List<ChatMessage>> = callbackFlow {
        val ref = db.child("chats").child(roomId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(ChatMessage::class.java)
                }
                trySend(messages)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun makeMove(roomId: String, row: Int, col: Int, room: OnlineRoom) {
        val newBoard = room.board.map { it.toMutableList() }.toMutableList()
        newBoard[row][col] = room.currentPlayer
        val nextPlayer = if (room.currentPlayer == 1) 2 else 1
        val winner = checkWin(newBoard, row, col, room.currentPlayer)
        val isDraw = winner == 0 && newBoard.all { r -> r.all { it != 0 } }

        db.child("rooms").child(roomId).apply {
            child("board").setValue(newBoard).await()
            child("currentPlayer").setValue(nextPlayer).await()
            child("winner").setValue(if (isDraw) 3 else winner).await()
            child("lastMoveRow").setValue(row).await()
            child("lastMoveCol").setValue(col).await()
            if (winner != 0 || isDraw) child("status").setValue("finished").await()
        }
    }

    suspend fun sendMessage(roomId: String, text: String, senderName: String) {
        val msg = ChatMessage(
            senderId = playerId,
            senderName = senderName,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        db.child("chats").child(roomId).push().setValue(msg).await()
    }

    suspend fun deleteRoom(roomId: String) {
        db.child("rooms").child(roomId).removeValue().await()
        db.child("chats").child(roomId).removeValue().await()
    }

    private fun checkWin(board: List<MutableList<Int>>, row: Int, col: Int, player: Int): Int {
        val dirs = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)
        for ((dr, dc) in dirs) {
            var count = 1
            var r = row + dr; var c = col + dc
            while (r in 0..14 && c in 0..14 && board[r][c] == player) { count++; r += dr; c += dc }
            r = row - dr; c = col - dc
            while (r in 0..14 && c in 0..14 && board[r][c] == player) { count++; r -= dr; c -= dc }
            if (count >= 5) return player
        }
        return 0
    }

    suspend fun resetRoom(roomId: String) {
        val newBoard = List(15) { List(15) { 0 } }
        db.child("rooms").child(roomId).apply {
            child("board").setValue(newBoard).await()
            child("currentPlayer").setValue(1).await()
            child("winner").setValue(0).await()
            child("lastMoveRow").setValue(-1).await()
            child("lastMoveCol").setValue(-1).await()
            child("status").setValue("playing").await()
        }
    }
}