package com.example.ciphernet.model

data class AuthorIdentity(
    val username: String,
    val address: String, // e.g. 0x8F92...E67B
    val avatarColorHex: Long = 0xFF10B981,
    val isVerifiedSigner: Boolean = true
)

data class Post(
    val id: String,
    val author: AuthorIdentity,
    val channel: String,
    val content: String,
    val timestamp: Long,
    val signature: String, // Ed25519 cryptographic signature hash
    val ipfsCid: String, // Decentralized storage CID
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val tags: List<String> = emptyList(),
    val isEncryptedChannel: Boolean = false
)

enum class SocialChannel(val displayName: String, val topic: String, val iconName: String) {
    GENERAL("#general", "Public mesh announcements & decentralized discussions", "tag"),
    PRIVACY_TECH("#privacy-tech", "Zero-knowledge proofs, encryption protocols, and mixnets", "shield"),
    DECENTRALIZED("#decentralized", "IPFS, libp2p, peer-to-peer storage, and nodes", "hub"),
    ANON_LOUNGE("#anon-lounge", "Ephemeral pseudonymous posts with auto-rotating identities", "visibility_off")
}
