package com.example.ciphernet.model

enum class DestructDuration(val seconds: Long, val label: String) {
    OFF(0, "Off"),
    TEN_SECONDS(10, "10 seconds"),
    THIRTY_SECONDS(30, "30 seconds"),
    ONE_MINUTE(60, "1 minute"),
    FIVE_MINUTES(300, "5 minutes"),
    ONE_HOUR(3600, "1 hour"),
    TWENTY_FOUR_HOURS(86400, "24 hours"),
    BURN_ON_READ(-1, "Burn on Read")
}

enum class RecurrenceType(val label: String) {
    NONE("One-Time"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

data class DirectMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val isMe: Boolean,
    val plainText: String,
    val cipherText: String, // Real AES-256-GCM cipher string
    val iv: String, // 12-byte IV Base64
    val authTag: String, // Authentication Tag Base64
    val timestamp: Long,
    val destructDuration: DestructDuration = DestructDuration.OFF,
    val remainingSeconds: Long? = null, // Countdown for auto-destruct
    val isBurned: Boolean = false,
    val ipfsAttachmentCid: String? = null
)

data class Conversation(
    val id: String,
    val peerName: String,
    val peerAddress: String,
    val peerAvatarColor: Long,
    val isOnline: Boolean = true,
    val activeDestructDuration: DestructDuration = DestructDuration.OFF,
    val lastMessage: DirectMessage? = null,
    val unreadCount: Int = 0,
    val keyFingerprint: String
)

data class ScheduledMessage(
    val id: String,
    val recipientId: String,
    val recipientName: String,
    val messageText: String,
    val scheduledTimeEpochMs: Long,
    val recurrence: RecurrenceType = RecurrenceType.NONE,
    val isExecuted: Boolean = false,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
