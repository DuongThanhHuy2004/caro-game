package com.example.caro.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

@Composable
fun OnlineScreen(
    vm: OnlineGameViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val messages by vm.messages.collectAsStateWithLifecycle()
    val black = Color(0xFF0D0D0D)
    val bgColor = Color(0xFFF5F5F3)
    val mutedColor = Color(0xFF999999)

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { vm.leaveRoom(); onBack() }, modifier = Modifier.size(40.dp)) {
                    Text("←", fontSize = 22.sp, color = black)
                }
                Spacer(Modifier.weight(1f))
                Text("ONLINE", fontSize = 11.sp, letterSpacing = 2.sp, color = mutedColor)
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(40.dp))
            }

            when (val state = uiState) {
                is OnlineUiState.Idle -> IdleScreen(
                    onCreateRoom = { vm.createRoom() },
                    onJoinRoom = { vm.joinRoom(it) }
                )
                is OnlineUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = black)
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(state.message, color = Color(0xFF999999), fontSize = 13.sp, letterSpacing = 1.sp)
                        Button(
                            onClick = { vm.leaveRoom() },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = black)
                        ) {
                            Text("THỬ LẠI", fontSize = 11.sp, letterSpacing = 2.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IdleScreen(onCreateRoom: () -> Unit, onJoinRoom: (String) -> Unit) {
    var roomCode by remember { mutableStateOf("") }
    val black = Color(0xFF0D0D0D)
    val mutedColor = Color(0xFF999999)
    val gray = Color(0xFFE8E8E6)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "ONLINE",
            fontSize = 40.sp,
            fontWeight = FontWeight.Thin,
            letterSpacing = 6.sp,
            color = black
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "PLAY WITH FRIENDS",
            fontSize = 10.sp,
            letterSpacing = 4.sp,
            color = mutedColor
        )
        Spacer(Modifier.height(48.dp))

        Button(
            onClick = onCreateRoom,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = black,
                contentColor = Color.White
            )
        ) {
            Text("CREATE ROOM", fontSize = 12.sp, letterSpacing = 3.sp)
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD))
            Text("OR", fontSize = 10.sp, letterSpacing = 2.sp, color = mutedColor)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD))
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = roomCode,
            onValueChange = { if (it.length <= 6) roomCode = it.filter { c -> c.isDigit() } },
            placeholder = { Text("ROOM CODE", fontSize = 12.sp, letterSpacing = 2.sp, color = mutedColor) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFDDDDDD),
                focusedBorderColor = black
            )
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { if (roomCode.length == 6) onJoinRoom(roomCode) },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(50),
            enabled = roomCode.length == 6,
            colors = ButtonDefaults.buttonColors(
                containerColor = gray,
                contentColor = black,
                disabledContainerColor = Color(0xFFF0F0EE),
                disabledContentColor = mutedColor
            )
        ) {
            Text("JOIN ROOM", fontSize = 12.sp, letterSpacing = 3.sp)
        }
    }
}

@Composable
fun LobbyScreen(roomId: String, isHost: Boolean) {
    val black = Color(0xFF0D0D0D)
    val mutedColor = Color(0xFF999999)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isHost) {
            Text(
                "ROOM CODE",
                fontSize = 10.sp,
                letterSpacing = 4.sp,
                color = mutedColor
            )
            Spacer(Modifier.height(16.dp))
            Text(
                roomId,
                fontSize = 52.sp,
                fontWeight = FontWeight.Thin,
                letterSpacing = 12.sp,
                color = black
            )
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(color = black, strokeWidth = 1.5.dp)
            Spacer(Modifier.height(16.dp))
            Text(
                "WAITING FOR OPPONENT",
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                color = mutedColor
            )
        } else {
            CircularProgressIndicator(color = black, strokeWidth = 1.5.dp)
            Spacer(Modifier.height(20.dp))
            Text(
                "JOINING $roomId",
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                color = mutedColor
            )
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
    val black = Color(0xFF0D0D0D)
    val mutedColor = Color(0xFF999999)
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

    val gameState = GameState(
        board = boardArray,
        winner = when (room.winner) { 1 -> Player.X; 2 -> Player.O; else -> null },
        lastMove = if (room.lastMoveRow >= 0) Pair(room.lastMoveRow, room.lastMoveCol) else null
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .imePadding()
    ) {
        // Status
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("SESSION STATUS", fontSize = 10.sp, letterSpacing = 3.sp, color = mutedColor)
            Spacer(Modifier.height(3.dp))
            Text(
                when {
                    room.winner == 3 -> "DRAW"
                    room.winner != 0 ->
                        if ((isHost && room.winner == 1) || (!isHost && room.winner == 2)) "YOU WIN" else "YOU LOSE"
                    myTurn -> "YOUR TURN"
                    else -> "OPPONENT TURN"
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = black
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
            Text(
                "CHAT (${messages.size})",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = mutedColor,
                fontWeight = FontWeight.Medium
            )
            TextButton(onClick = { showChat = !showChat }) {
                Text(
                    if (showChat) "HIDE" else "SHOW",
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = black
                )
            }
        }

        AnimatedVisibility(showChat) {
            Column {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
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
                                    .background(if (isMe) black else Color(0xFFE8E8E6))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Column {
                                    if (!isMe) Text(
                                        msg.senderName,
                                        fontSize = 9.sp,
                                        letterSpacing = 1.sp,
                                        color = mutedColor
                                    )
                                    Text(
                                        msg.text,
                                        color = if (isMe) Color.White else black,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

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
                        placeholder = { Text("MESSAGE", fontSize = 10.sp, letterSpacing = 2.sp, color = mutedColor) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFDDDDDD),
                            focusedBorderColor = black
                        )
                    )
                    IconButton(
                        onClick = {
                            if (chatText.isNotBlank()) {
                                onSendMessage(chatText)
                                chatText = ""
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = black)
                    }
                }
            }
        }
    }

    // Win dialog
    if (room.winner != 0) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = Color(0xFFF5F5F3),
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    when {
                        room.winner == 3 -> "DRAW"
                        (isHost && room.winner == 1) || (!isHost && room.winner == 2) -> "YOU WIN"
                        else -> "YOU LOSE"
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onLeaveRoom,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8E8E6),
                            contentColor = black
                        )
                    ) {
                        Text("LEAVE", fontSize = 11.sp, letterSpacing = 2.sp)
                    }
                    Button(
                        onClick = onPlayAgain,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("REMATCH", fontSize = 11.sp, letterSpacing = 2.sp)
                    }
                }
            }
        )
    }
}