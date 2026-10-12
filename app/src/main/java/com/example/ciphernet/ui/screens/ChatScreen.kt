package com.example.ciphernet.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ciphernet.data.CipherNetRepository
import com.example.ciphernet.model.Conversation
import com.example.ciphernet.model.DestructDuration
import com.example.ciphernet.model.DirectMessage
import com.example.ciphernet.ui.components.CipherInspectorDialog
import com.example.ciphernet.ui.components.ScheduleMessageDialog
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.DestructRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    repository: CipherNetRepository,
    modifier: Modifier = Modifier
) {
    val conversations by repository.conversations.collectAsState()
    val allMessages by repository.messages.collectAsState()
    var activeConversationId by remember { mutableStateOf<String?>(null) }

    var inspectMessage by remember { mutableStateOf<DirectMessage?>(null) }
    var showScheduleDialog by remember { mutableStateOf(false) }

    if (activeConversationId != null) {
        val activeConv = conversations.find { it.id == activeConversationId }
        val msgList = allMessages[activeConversationId] ?: emptyList()

        BackHandler {
            activeConversationId = null
        }

        ActiveChatThreadView(
            conversation = activeConv ?: conversations.first(),
            messages = msgList,
            onBack = { activeConversationId = null },
            onSendMessage = { text, duration ->
                activeConversationId?.let { convId ->
                    repository.sendMessage(convId, text, duration)
                }
            },
            onChangeDestructTimer = { duration ->
                activeConversationId?.let { convId ->
                    repository.setConversationDestructTimer(convId, duration)
                }
            },
            onInspectCipher = { inspectMessage = it },
            onOpenSchedule = { showScheduleDialog = true },
            modifier = modifier
        )
    } else {
        ConversationListView(
            conversations = conversations,
            onSelectConversation = { activeConversationId = it.id },
            onOpenSchedule = { showScheduleDialog = true },
            modifier = modifier
        )
    }

    inspectMessage?.let { msg ->
        CipherInspectorDialog(
            message = msg,
            onDismiss = { inspectMessage = null }
        )
    }

    if (showScheduleDialog) {
        ScheduleMessageDialog(
            conversations = conversations,
            initialConversationId = activeConversationId,
            onDismiss = { showScheduleDialog = false },
            onSchedule = { recipientId, recipientName, text, triggerTime, recurrence ->
                repository.scheduleMessage(recipientId, recipientName, text, triggerTime, recurrence)
                showScheduleDialog = false
            }
        )
    }
}

@Composable
fun ConversationListView(
    conversations: List<Conversation>,
    onSelectConversation: (Conversation) -> Unit,
    onOpenSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Encrypted Rooms",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "End-to-End Encrypted • Double Ratchet Protocol",
                        fontSize = 11.sp,
                        color = CyberEmerald
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberEmerald.copy(alpha = 0.15f))
                        .border(1.dp, CyberEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { onOpenSchedule() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("open_schedule_dialog_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Schedule",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberEmerald
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(conversations, key = { it.id }) { conv ->
            ConversationRowCard(
                conversation = conv,
                onClick = { onSelectConversation(conv) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ConversationRowCard(conversation: Conversation, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("conversation_item_${conversation.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(conversation.peerAvatarColor))
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.peerName.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.peerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (conversation.activeDestructDuration != DestructDuration.OFF) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = DestructRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = conversation.activeDestructDuration.label,
                                fontSize = 10.sp,
                                color = DestructRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = conversation.lastMessage?.plainText ?: "Encrypted handshake established",
                    maxLines = 1,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Key: ${conversation.keyFingerprint}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = ElectricCyan
                )
            }
        }
    }
}

@Composable
fun ActiveChatThreadView(
    conversation: Conversation,
    messages: List<DirectMessage>,
    onBack: () -> Unit,
    onSendMessage: (String, DestructDuration) -> Unit,
    onChangeDestructTimer: (DestructDuration) -> Unit,
    onInspectCipher: (DirectMessage) -> Unit,
    onOpenSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var showTimerDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat Header
        ChatHeaderBar(
            conversation = conversation,
            onBack = onBack,
            onTimerClick = { showTimerDropdown = true },
            onScheduleClick = onOpenSchedule
        )

        // Ephemeral Timer dropdown
        DropdownMenu(
            expanded = showTimerDropdown,
            onDismissRequest = { showTimerDropdown = false }
        ) {
            DestructDuration.values().forEach { duration ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (duration != DestructDuration.OFF) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = DestructRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text("Disappearing: ${duration.label}")
                        }
                    },
                    onClick = {
                        onChangeDestructTimer(duration)
                        showTimerDropdown = false
                    }
                )
            }
        }

        // Encryption active strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberEmerald.copy(alpha = 0.08f))
                .border(0.5.dp, CyberEmerald.copy(alpha = 0.2f), RoundedCornerShape(0.dp))
                .padding(vertical = 4.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CyberEmerald,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "E2EE AES-256-GCM • Fingerprint: ${conversation.keyFingerprint}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = CyberEmerald
                    )
                }
                if (conversation.activeDestructDuration != DestructDuration.OFF) {
                    Text(
                        text = "🔥 Auto-Destruct: ${conversation.activeDestructDuration.label}",
                        fontSize = 10.sp,
                        color = DestructRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Message list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(messages, key = { it.id }) { msg ->
                MessageBubble(
                    message = msg,
                    onInspectCipher = { onInspectCipher(msg) }
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        // Input Bar
        ChatInputBar(
            inputText = inputText,
            onInputChange = { inputText = it },
            activeDestructDuration = conversation.activeDestructDuration,
            onSend = {
                if (inputText.isNotBlank()) {
                    onSendMessage(inputText, conversation.activeDestructDuration)
                    inputText = ""
                }
            }
        )
    }
}

@Composable
fun ChatHeaderBar(
    conversation: Conversation,
    onBack: () -> Unit,
    onTimerClick: () -> Unit,
    onScheduleClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(0.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(conversation.peerAvatarColor)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = conversation.peerName.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.peerName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Key Verified • No Cloud Storage",
                fontSize = 10.sp,
                color = CyberEmerald
            )
        }

        IconButton(onClick = onScheduleClick, modifier = Modifier.testTag("chat_schedule_button")) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Schedule Recurring Message",
                tint = ElectricCyan
            )
        }

        IconButton(onClick = onTimerClick, modifier = Modifier.testTag("chat_timer_button")) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Destruct Timer",
                tint = if (conversation.activeDestructDuration != DestructDuration.OFF) DestructRed else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: DirectMessage,
    onInspectCipher: () -> Unit
) {
    val isMe = message.isMe
    val alignment = if (isMe) Alignment.End else Alignment.Start
    val bgColor = if (isMe) CyberEmerald.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
    val borderColor = if (isMe) CyberEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isMe) 14.dp else 2.dp,
                bottomEnd = if (isMe) 2.dp else 14.dp
            ),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .testTag("message_bubble_${message.id}")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Sender & destruct countdown header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isMe) "You (Signed)" else message.senderName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isMe) CyberEmerald else ElectricCyan
                    )

                    if (message.remainingSeconds != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = DestructRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Burns in ${message.remainingSeconds}s",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DestructRed
                            )
                        }
                    }
                }

                if (message.remainingSeconds != null && message.destructDuration.seconds > 0) {
                    val progress = (message.remainingSeconds.toFloat() / message.destructDuration.seconds.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .padding(vertical = 4.dp),
                        color = DestructRed,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Plaintext message
                Text(
                    text = message.plainText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Cryptographic verification & Cipher inspection button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AES-256-GCM Verified",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onInspectCipher() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Inspect Cipher",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    activeDestructDuration: DestructDuration,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(0.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            placeholder = {
                Text(
                    text = if (activeDestructDuration != DestructDuration.OFF)
                        "Encrypted message (Burns in ${activeDestructDuration.label})..."
                    else "Type end-to-end encrypted message...",
                    fontSize = 12.sp
                )
            },
            modifier = Modifier
                .weight(1f)
                .testTag("chat_message_input"),
            shape = RoundedCornerShape(20.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (inputText.isNotBlank()) CyberEmerald else MaterialTheme.colorScheme.surfaceVariant)
                .clickable(enabled = inputText.isNotBlank()) { onSend() }
                .testTag("chat_send_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (inputText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
