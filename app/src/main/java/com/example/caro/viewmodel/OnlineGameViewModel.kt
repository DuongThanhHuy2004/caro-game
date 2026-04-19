package com.example.caro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caro.model.ChatMessage
import com.example.caro.model.OnlineRoom
import com.example.caro.online.OnlineGameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class OnlineUiState {
    object Idle : OnlineUiState()
    object Loading : OnlineUiState()
    data class InLobby(val roomId: String, val isHost: Boolean) : OnlineUiState()
    data class InGame(val room: OnlineRoom, val isHost: Boolean) : OnlineUiState()
    data class Error(val message: String) : OnlineUiState()
}

class OnlineGameViewModel : ViewModel() {
    private val repo = OnlineGameRepository()

    private val _uiState = MutableStateFlow<OnlineUiState>(OnlineUiState.Idle)
    val uiState: StateFlow<OnlineUiState> = _uiState

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private var currentRoomId: String = ""
    private var isHost: Boolean = false
    val playerId = repo.getPlayerId()

    fun createRoom() {
        viewModelScope.launch {
            _uiState.value = OnlineUiState.Loading
            val roomId = repo.generateRoomId()
            repo.createRoom(roomId)
                .onSuccess {
                    currentRoomId = roomId
                    isHost = true
                    _uiState.value = OnlineUiState.InLobby(roomId, true)
                    observeRoom(roomId)
                    observeChat(roomId)
                }
                .onFailure { _uiState.value = OnlineUiState.Error(it.message ?: "Lỗi tạo phòng") }
        }
    }

    fun joinRoom(roomId: String) {
        viewModelScope.launch {
            _uiState.value = OnlineUiState.Loading
            repo.joinRoom(roomId)
                .onSuccess {
                    currentRoomId = roomId
                    isHost = false
                    observeRoom(roomId)
                    observeChat(roomId)
                }
                .onFailure { _uiState.value = OnlineUiState.Error(it.message ?: "Lỗi vào phòng") }
        }
    }

    private fun observeRoom(roomId: String) {
        viewModelScope.launch {
            repo.observeRoom(roomId).collect { room ->
                _uiState.value = when (room.status) {
                    "waiting" -> OnlineUiState.InLobby(roomId, isHost)
                    else -> OnlineUiState.InGame(room, isHost)
                }
            }
        }
    }

    private fun observeChat(roomId: String) {
        viewModelScope.launch {
            repo.observeChat(roomId).collect { _messages.value = it }
        }
    }

    fun makeMove(row: Int, col: Int) {
        val state = _uiState.value as? OnlineUiState.InGame ?: return
        val room = state.room
        if (room.winner != 0) return
        val myTurn = (isHost && room.currentPlayer == 1) || (!isHost && room.currentPlayer == 2)
        if (!myTurn) return
        if (room.board[row][col] != 0) return

        viewModelScope.launch {
            repo.makeMove(currentRoomId, row, col, room)
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val name = if (isHost) "X (Host)" else "O (Guest)"
            repo.sendMessage(currentRoomId, text, name)
        }
    }

    fun leaveRoom() {
        viewModelScope.launch {
            if (isHost) repo.deleteRoom(currentRoomId)
            _uiState.value = OnlineUiState.Idle
            _messages.value = emptyList()
            currentRoomId = ""
        }
    }

    fun playAgain() {
        viewModelScope.launch {
            val s = _uiState.value as? OnlineUiState.InGame ?: return@launch
            repo.resetRoom(currentRoomId)
        }
    }
}