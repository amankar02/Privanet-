package com.example.ciphernet.model

data class IpfsBlock(
    val cid: String, // Content Identifier (CIDv1)
    val label: String,
    val sizeBytes: Long,
    val mimeType: String,
    val isPinned: Boolean,
    val replicationFactor: Int, // Number of peer nodes pinning this block
    val addedTimestamp: Long
)

data class PeerNode(
    val peerId: String,
    val location: String,
    val latencyMs: Int,
    val isConnected: Boolean,
    val protocol: String = "libp2p/noise/yamux",
    val blocksHosted: Int
)

data class DeviceSync(
    val id: String,
    val deviceName: String,
    val platform: String, // Android, Linux Desktop, Web Vault, macOS
    val lastSyncEpochMs: Long,
    val isCurrentDevice: Boolean = false,
    val syncKeyHash: String
)

data class DevApiKey(
    val id: String,
    val keyName: String,
    val apiKeyMasked: String,
    val fullApiKey: String,
    val permissions: List<String>, // "read:feed", "write:messages", "ipfs:pin", "crypto:sign"
    val createdAtEpochMs: Long,
    val callCount: Int = 0
)

data class PluginDefinition(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val isEnabled: Boolean,
    val category: String,
    val permissionsRequired: List<String>
)
