package com.example.caro.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caro.model.ChatMessage
import com.example.caro.model.GameState
import com.example.caro.model.OnlineRoom
import com.example.caro.model.Player
import com.example.caro.viewmodel.OnlineGameViewModel
import com.example.caro.viewmodel.OnlineUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineScreen(
    vm: OnlineGameViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val messages by vm.messages.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chơi Online", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { vm.leaveRoom(); onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is OnlineUiState.Idle -> IdleScreen(
                    onCreateRoom = { vm.createRoom() },
                    onJoinRoom = { vm.joinRoom(it) }
                )
                is OnlineUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
                is OnlineUiState.InLobby -> LobbyScreen(
                    roomId = state.roomId,
                    isHost = state.isHost
                )
                is OnlineUiState.InGame -> OnlineGameContent(
                    room = state.room,
                    isHost = state.isHost,
                    playerId = vm.playerId,
                    messages = messages,
                    onMove = { r, c -> vm.makeMove(r, c) },
                    onSendMessage = { vm.sendMessage(it) },
                    onLeaveRoom = { vm.leaveRoom(); onBack() },
                    onPlayAgain = { vm.playAgain() }
                )
                is OnlineUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌ ${state.message}", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { vm.leaveRoom() }) { Text("Thử lại") }
                    }
                }
            }
        }
    }
}

@Composable
fun IdleScreen(onCreateRoom: () -> Unit, onJoinRoom: (String) -> Unit) {
    var roomCode by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🌐", fontSize = 64.sp)
        Spacer(Modifier.height(8.dp))
        Text("Chơi Online", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = onCreateRoom,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Tạo phòng mới", fontSize = 16.sp) }
        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = roomCode,
            onValueChange = { if (it.length <= 6) roomCode = it.filter { c -> c.isDigit() } },
            label = { Text("Nhập mã phòng") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { if (roomCode.length == 6) onJoinRoom(roomCode) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = roomCode.length == 6
        ) { Text("Vào phòng", fontSize = 16.sp) }
    }
}

@Composable
fun LobbyScreen(roomId: String, isHost: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isHost) {
            Text("Phòng đã tạo!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Text("Gửi mã này cho bạn bè:", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)) {
                Text(
                    roomId,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 40.dp, vertical = 20.dp),
                    letterSpacing = 8.sp
                )
            }
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Đang chờ đối thủ...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("Đang vào phòng $roomId...")
        }
    }
}

@Composable
fun OnlineGameContent(
    room: OnlineRoom,
    isHost: Boolean,
    playerId: String,
    messages: List<ChatMessage>,
    onMove: (Int, Int) -> Unit,
    onSendMessage: (String) -> Unit,
    onLeaveRoom: () -> Unit,
    onPlayAgain: () -> Unit
) {
    val myTurn = (isHost && room.currentPlayer == 1) || (!isHost && room.currentPlayer == 2)
    var chatText by remember { mutableStateOf("") }
    var showChat by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    val boardArray = Array(15) { r ->
        Array<Player?>(15) { c ->
            when (room.board[r][c]) {
                1 -> Player.X
                2 -> Player.O
                else -> null
            }
        }
    }

    val winnerPlayer = when (room.winner) {
        1 -> Player.X
        2 -> Player.O
        else -> null
    }

    val gameState = GameState(
        board = boardArray,
        winner = winnerPlayer,
        lastMove = if (room.lastMoveRow >= 0) Pair(room.lastMoveRow, room.lastMoveCol) else null
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .imePadding()
    ) {
        // Status bar
        val statusText = when {
            room.winner == 3 -> "Hòa! 🤝"
            room.winner != 0 ->
                if ((isHost && room.winner == 1) || (!isHost && room.winner == 2))
                    "Bạn thắng! 🎉" else "Bạn thua! 😢"
            myTurn -> "Lượt của bạn (${if (isHost) "X" else "O"})"
            else -> "Đối thủ đang đánh..."
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                if (myTurn && room.winner == 0) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                statusText,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }

        // Board
        BoardView(
            state = gameState,
            boardSize = 15,
            onCellClick = { r, c -> if (room.winner == 0) onMove(r, c) },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.height(8.dp))

        // Chat toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💬 Chat (${messages.size})", fontWeight = FontWeight.Medium)
            TextButton(onClick = { showChat = !showChat }) {
                Text(if (showChat) "Ẩn" else "Hiện")
            }
        }

        AnimatedVisibility(showChat) {
            Column {
                // Messages list
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(messages) { msg ->
                        val isMe = msg.senderId == playerId
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isMe) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Column {
                                    if (!isMe) Text(
                                        msg.senderName,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        msg.text,
                                        color = if (isMe) Color.White
                                        else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Chat input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatText,
                        onValueChange = { chatText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Nhắn tin...") },
                        singleLine = true,
                        enabled = true
                    )
                    IconButton(
                        onClick = {
                            if (chatText.isNotBlank()) {
                                onSendMessage(chatText)
                                chatText = ""
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Send")
                    }
                }
            }
        }
    }

    // Win/Lose/Draw dialog
    if (room.winner != 0) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    when {
                        room.winner == 3 -> "Hòa! 🤝"
                        (isHost && room.winner == 1) || (!isHost && room.winner == 2) -> "Bạn thắng! 🎉"
                        else -> "Bạn thua! 😢"
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            confirmButton = {
                Button(onClick = onLeaveRoom) {
                    Text("Thoát phòng")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onPlayAgain) {
                    Text("Chơi lại")
                }
            }
        )
    }
}