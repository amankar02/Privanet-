package com.example.ciphernet.data

import com.example.ciphernet.crypto.CryptoEngine
import com.example.ciphernet.model.AuthorIdentity
import com.example.ciphernet.model.Conversation
import com.example.ciphernet.model.DestructDuration
import com.example.ciphernet.model.DevApiKey
import com.example.ciphernet.model.DirectMessage
import com.example.ciphernet.model.IpfsBlock
import com.example.ciphernet.model.PeerNode
import com.example.ciphernet.model.PluginDefinition
import com.example.ciphernet.model.Post
import com.example.ciphernet.model.RecurrenceType
import com.example.ciphernet.model.ScheduledMessage
import com.example.ciphernet.model.SocialChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CipherNetRepository(private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {

    // Current user's self-sovereign identity
    val myIdentity = AuthorIdentity(
        username = "@cipher_48f9",
        address = "0x8F92E67B10B94A02",
        avatarColorHex = 0xFF10B981,
        isVerifiedSigner = true
    )

    val myKeyFingerprint = "4F8A-E921-10B7-39C2"
    val mySeedPhrase = CryptoEngine.generateSeedPhrase()

    // App Security state
    private val _isBiometricLockEnabled = MutableStateFlow(false)
    val isBiometricLockEnabled: StateFlow<Boolean> = _isBiometricLockEnabled.asStateFlow()

    private val _isVaultLocked = MutableStateFlow(false)
    val isVaultLocked: StateFlow<Boolean> = _isVaultLocked.asStateFlow()

    private val _masterPin = MutableStateFlow("1337")
    val masterPin: StateFlow<String> = _masterPin.asStateFlow()

    // Theme: "DARK", "LIGHT", "SYSTEM"
    private val _themeMode = MutableStateFlow("DARK")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // Social Feed
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _selectedChannel = MutableStateFlow(SocialChannel.GENERAL)
    val selectedChannel: StateFlow<SocialChannel> = _selectedChannel.asStateFlow()

    // Encrypted Chat Conversations & Messages
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<DirectMessage>>>(emptyMap())
    val messages: StateFlow<Map<String, List<DirectMessage>>> = _messages.asStateFlow()

    // Scheduled Recurring Messages
    private val _scheduledMessages = MutableStateFlow<List<ScheduledMessage>>(emptyList())
    val scheduledMessages: StateFlow<List<ScheduledMessage>> = _scheduledMessages.asStateFlow()

    // Decentralized IPFS Storage
    private val _ipfsBlocks = MutableStateFlow<List<IpfsBlock>>(emptyList())
    val ipfsBlocks: StateFlow<List<IpfsBlock>> = _ipfsBlocks.asStateFlow()

    private val _peerNodes = MutableStateFlow<List<PeerNode>>(emptyList())
    val peerNodes: StateFlow<List<PeerNode>> = _peerNodes.asStateFlow()

    // Multi-Device Sync
    private val _syncedDevices = MutableStateFlow<List<com.example.ciphernet.model.DeviceSync>>(emptyList())
    val syncedDevices: StateFlow<List<com.example.ciphernet.model.DeviceSync>> = _syncedDevices.asStateFlow()

    private val _syncStatus = MutableStateFlow("All devices synchronized • P2P Mesh online")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    // Developer API Keys & Plugins
    private val _apiKeys = MutableStateFlow<List<DevApiKey>>(emptyList())
    val apiKeys: StateFlow<List<DevApiKey>> = _apiKeys.asStateFlow()

    private val _plugins = MutableStateFlow<List<PluginDefinition>>(emptyList())
    val plugins: StateFlow<List<PluginDefinition>> = _plugins.asStateFlow()

    init {
        initSampleData()
        startDestructMessageTicker()
    }

    private fun initSampleData() {
        // Sample Posts
        val samplePosts = listOf(
            Post(
                id = "p-1",
                author = AuthorIdentity("@satoshi_mesh", "0x0A91D3C29910", 0xFF06B6D4),
                channel = SocialChannel.PRIVACY_TECH.displayName,
                content = "Zero-Knowledge Rollup update: Client-side ZK-SNARK proofs now verify locally in under 42ms. No server telemetry or metadata traces are logged.",
                timestamp = System.currentTimeMillis() - 15 * 60 * 1000L,
                signature = CryptoEngine.generateSignature("zk-snark", "0x0A91D3C29910"),
                ipfsCid = CryptoEngine.generateIpfsCid("zk-snark-spec"),
                likesCount = 38,
                tags = listOf("zkSNARKs", "Privacy", "Decentralized")
            ),
            Post(
                id = "p-2",
                author = AuthorIdentity("@cypherpunk_alice", "0x7F20B912AA4C", 0xFF10B981),
                channel = SocialChannel.GENERAL.displayName,
                content = "Reminder: Your keys, your data. Centralized cloud platforms sell your social graphs. CipherNet pins everything across peer storage with zero backdoors.",
                timestamp = System.currentTimeMillis() - 48 * 60 * 1000L,
                signature = CryptoEngine.generateSignature("keys-privacy", "0x7F20B912AA4C"),
                ipfsCid = CryptoEngine.generateIpfsCid("keys-manifesto"),
                likesCount = 92,
                tags = listOf("Sovereignty", "E2EE", "IPFS")
            ),
            Post(
                id = "p-3",
                author = AuthorIdentity("@ghost_relay", "0xEE44A1098801", 0xFFA855F7),
                channel = SocialChannel.DECENTRALIZED.displayName,
                content = "Decentralized DHT routing table reached 14,200 peer relays today across 68 countries. Onion transport hops are active with Yamux multiplexing.",
                timestamp = System.currentTimeMillis() - 2 * 3600 * 1000L,
                signature = CryptoEngine.generateSignature("dht-stats", "0xEE44A1098801"),
                ipfsCid = CryptoEngine.generateIpfsCid("dht-routing-v2"),
                likesCount = 57,
                tags = listOf("DHT", "P2P", "Tor")
            ),
            Post(
                id = "p-4",
                author = AuthorIdentity("@anon_e61b", "0x33B10029EE99", 0xFFF59E0B),
                channel = SocialChannel.ANON_LOUNGE.displayName,
                content = "Encrypted whisper: Rotating ratchet session generated. Auto-destructing messages ensure forward secrecy against quantum ledger scraping.",
                timestamp = System.currentTimeMillis() - 5 * 3600 * 1000L,
                signature = CryptoEngine.generateSignature("whisper-ratchet", "0x33B10029EE99"),
                ipfsCid = CryptoEngine.generateIpfsCid("whisper-block"),
                likesCount = 24,
                tags = listOf("Whisper", "Ephemeral")
            )
        )
        _posts.value = samplePosts

        // Conversations
        val conv1 = Conversation(
            id = "c-1",
            peerName = "Elena Rostova",
            peerAddress = "0x4A1098B22019",
            peerAvatarColor = 0xFF10B981,
            isOnline = true,
            activeDestructDuration = DestructDuration.THIRTY_SECONDS,
            keyFingerprint = "7B19-E02A-8812-4099"
        )
        val conv2 = Conversation(
            id = "c-2",
            peerName = "Kaelen Voss",
            peerAddress = "0x8891002BA441",
            peerAvatarColor = 0xFF06B6D4,
            isOnline = true,
            activeDestructDuration = DestructDuration.OFF,
            keyFingerprint = "F901-38AC-0144-88A2"
        )
        val conv3 = Conversation(
            id = "c-3",
            peerName = "Ghost Node #7",
            peerAddress = "0x992BCA118742",
            peerAvatarColor = 0xFFA855F7,
            isOnline = false,
            activeDestructDuration = DestructDuration.ONE_MINUTE,
            keyFingerprint = "C228-90BE-119A-5431"
        )
        val conv4 = Conversation(
            id = "c-4",
            peerName = "Satoshi's Circle (Secret Mesh)",
            peerAddress = "0x000188AB9472",
            peerAvatarColor = 0xFFF59E0B,
            isOnline = true,
            activeDestructDuration = DestructDuration.FIVE_MINUTES,
            keyFingerprint = "A042-881E-47BC-0012"
        )
        _conversations.value = listOf(conv1, conv2, conv3, conv4)

        // Seed messages with real AES-256-GCM ciphertexts
        val c1Msgs = mutableListOf<DirectMessage>()
        val p1 = CryptoEngine.encryptAesGcm("Hey! Have you reviewed the decentralized storage protocol?")
        c1Msgs.add(
            DirectMessage(
                id = "m-1",
                senderId = "elena",
                senderName = "Elena Rostova",
                isMe = false,
                plainText = p1.plainText,
                cipherText = p1.cipherTextBase64,
                iv = p1.ivBase64,
                authTag = p1.authTagBase64,
                timestamp = System.currentTimeMillis() - 10 * 60 * 1000L
            )
        )
        val p2 = CryptoEngine.encryptAesGcm("Yes, the IPFS CID verification is completely seamless. Zero-knowledge proof verified.")
        c1Msgs.add(
            DirectMessage(
                id = "m-2",
                senderId = "me",
                senderName = "@cipher_48f9",
                isMe = true,
                plainText = p2.plainText,
                cipherText = p2.cipherTextBase64,
                iv = p2.ivBase64,
                authTag = p2.authTagBase64,
                timestamp = System.currentTimeMillis() - 8 * 60 * 1000L
            )
        )
        val p3 = CryptoEngine.encryptAesGcm("This message will auto-destruct in 30 seconds after you view it.")
        c1Msgs.add(
            DirectMessage(
                id = "m-3",
                senderId = "elena",
                senderName = "Elena Rostova",
                isMe = false,
                plainText = p3.plainText,
                cipherText = p3.cipherTextBase64,
                iv = p3.ivBase64,
                authTag = p3.authTagBase64,
                timestamp = System.currentTimeMillis() - 20 * 1000L,
                destructDuration = DestructDuration.THIRTY_SECONDS,
                remainingSeconds = 30L
            )
        )

        val c2Msgs = mutableListOf<DirectMessage>()
        val p4 = CryptoEngine.encryptAesGcm("Scheduled recurring report is active. Broadcasting to encrypted mesh every Monday.")
        c2Msgs.add(
            DirectMessage(
                id = "m-4",
                senderId = "kaelen",
                senderName = "Kaelen Voss",
                isMe = false,
                plainText = p4.plainText,
                cipherText = p4.cipherTextBase64,
                iv = p4.ivBase64,
                authTag = p4.authTagBase64,
                timestamp = System.currentTimeMillis() - 40 * 60 * 1000L
            )
        )

        _messages.value = mapOf("c-1" to c1Msgs, "c-2" to c2Msgs)

        // Scheduled Recurring Messages
        _scheduledMessages.value = listOf(
            ScheduledMessage(
                id = "s-1",
                recipientId = "c-1",
                recipientName = "Elena Rostova",
                messageText = "Daily Sovereign Status Check: Node operational. All channels encrypted.",
                scheduledTimeEpochMs = System.currentTimeMillis() + 3600 * 1000L * 4,
                recurrence = RecurrenceType.DAILY
            ),
            ScheduledMessage(
                id = "s-2",
                recipientId = "c-4",
                recipientName = "Satoshi's Circle (Secret Mesh)",
                messageText = "Weekly Key Fingerprint Audit: Rotated ratchet keys published.",
                scheduledTimeEpochMs = System.currentTimeMillis() + 3600 * 1000L * 24 * 3,
                recurrence = RecurrenceType.WEEKLY
            )
        )

        // IPFS Blocks
        _ipfsBlocks.value = listOf(
            IpfsBlock(
                cid = "bafybeihdwdcefgh4dqj67zpkmx7a329kl",
                label = "Encrypted_Identity_Profile_0x8F92.json",
                sizeBytes = 24 * 1024L,
                mimeType = "application/json",
                isPinned = true,
                replicationFactor = 12,
                addedTimestamp = System.currentTimeMillis() - 24 * 3600 * 1000L
            ),
            IpfsBlock(
                cid = "bafybeicgqomd5pkaqwer79812401lzkmn",
                label = "Mesh_Decentralized_Feed_Snapshot_v1.tar",
                sizeBytes = 1420 * 1024L,
                mimeType = "application/x-tar",
                isPinned = true,
                replicationFactor = 18,
                addedTimestamp = System.currentTimeMillis() - 12 * 3600 * 1000L
            ),
            IpfsBlock(
                cid = "bafybeif9a02kmd9982410bcda9871zzpq",
                label = "ZeroKnowledge_Circuit_Artifact.zkey",
                sizeBytes = 3840 * 1024L,
                mimeType = "application/octet-stream",
                isPinned = false,
                replicationFactor = 6,
                addedTimestamp = System.currentTimeMillis() - 2 * 3600 * 1000L
            )
        )

        // Peer Nodes
        _peerNodes.value = listOf(
            PeerNode("12D3KooWR7...", "Zurich, Switzerland", 28, true, "libp2p/noise", 1420),
            PeerNode("12D3KooWB9...", "Reykjavik, Iceland", 45, true, "libp2p/noise", 2180),
            PeerNode("12D3KooWA4...", "Singapore", 88, true, "libp2p/noise", 980),
            PeerNode("12D3KooWE1...", "Tor Onion v3 (Hidden)", 195, true, "onion/yamux", 4520)
        )

        // Synced Devices
        _syncedDevices.value = listOf(
            com.example.ciphernet.model.DeviceSync("d-1", "Android Node (This Device)", "Android", System.currentTimeMillis(), true, "hash_0x8891A"),
            com.example.ciphernet.model.DeviceSync("d-2", "CipherNet Desktop v2.4", "Linux x86_64", System.currentTimeMillis() - 5 * 60 * 1000L, false, "hash_0x3429E"),
            com.example.ciphernet.model.DeviceSync("d-3", "Encrypted Web Vault (Firefox)", "Web Browser", System.currentTimeMillis() - 40 * 60 * 1000L, false, "hash_0x90F1C")
        )

        // Developer API Keys
        _apiKeys.value = listOf(
            DevApiKey(
                id = "k-1",
                keyName = "Local Desktop Daemon",
                apiKeyMasked = "apk_live_948f••••••••e921",
                fullApiKey = "apk_live_948f21908bcdae1104e921",
                permissions = listOf("read:feed", "write:messages", "crypto:sign"),
                createdAtEpochMs = System.currentTimeMillis() - 14 * 24 * 3600 * 1000L,
                callCount = 1420
            ),
            DevApiKey(
                id = "k-2",
                keyName = "IPFS Pinning Microservice",
                apiKeyMasked = "apk_live_3301••••••••88ac",
                fullApiKey = "apk_live_3301ffaa9871020088ac",
                permissions = listOf("ipfs:pin", "storage:read"),
                createdAtEpochMs = System.currentTimeMillis() - 3 * 24 * 3600 * 1000L,
                callCount = 89
            )
        )

        // Plugins
        _plugins.value = listOf(
            PluginDefinition(
                id = "plug-1",
                name = "PGP Keyring Bridge",
                version = "1.4.2",
                author = "CipherNet Core Labs",
                description = "Imports existing OpenPGP / GPG armor keys and signs outgoing mesh posts with dual-signature headers.",
                isEnabled = true,
                category = "Cryptography",
                permissionsRequired = listOf("crypto:sign", "keys:read")
            ),
            PluginDefinition(
                id = "plug-2",
                name = "Steganography Image Shield",
                version = "2.0.1",
                author = "StealthSec Group",
                description = "Embeds encrypted payloads inside JPEG/PNG least-significant bits for anti-surveillance transmission.",
                isEnabled = false,
                category = "Privacy",
                permissionsRequired = listOf("storage:write", "media:encode")
            ),
            PluginDefinition(
                id = "plug-3",
                name = "Matrix Protocol Relay",
                version = "0.9.8",
                author = "Federation Guild",
                description = "Bridges CipherNet E2EE rooms with decentralized Matrix federation servers via Megolm encryption.",
                isEnabled = true,
                category = "Networking",
                permissionsRequired = listOf("network:bridge", "messages:write")
            ),
            PluginDefinition(
                id = "plug-4",
                name = "Auto-Burn Memory Auditor",
                version = "1.1.0",
                author = "ZeroTrace Systems",
                description = "Forces RAM memory overwrite with random bytes before disposing expired auto-destructing messages.",
                isEnabled = true,
                category = "Security",
                permissionsRequired = listOf("memory:scrub")
            )
        )
    }

    // Auto-destruct ticker runs every second
    private fun startDestructMessageTicker() {
        scope.launch {
            while (true) {
                delay(1000L)
                val currentMap = _messages.value
                var changed = false
                val newMap = currentMap.mapValues { (_, msgList) ->
                    msgList.mapNotNull { msg ->
                        if (msg.destructDuration != DestructDuration.OFF && msg.destructDuration != DestructDuration.BURN_ON_READ) {
                            val remaining = msg.remainingSeconds ?: msg.destructDuration.seconds
                            if (remaining <= 1L) {
                                changed = true
                                null // Destructed / Burned from memory!
                            } else {
                                if (remaining != msg.remainingSeconds) changed = true
                                msg.copy(remainingSeconds = remaining - 1L)
                            }
                        } else {
                            msg
                        }
                    }
                }
                if (changed) {
                    _messages.value = newMap
                }
            }
        }
    }

    // Social Feed Actions
    fun setSelectedChannel(channel: SocialChannel) {
        _selectedChannel.value = channel
    }

    fun addPost(channelName: String, content: String, tags: List<String>) {
        val signature = CryptoEngine.generateSignature(content, myIdentity.address)
        val cid = CryptoEngine.generateIpfsCid(content)
        val newPost = Post(
            id = "p-" + UUID.randomUUID().toString().take(8),
            author = myIdentity,
            channel = channelName,
            content = content,
            timestamp = System.currentTimeMillis(),
            signature = signature,
            ipfsCid = cid,
            likesCount = 0,
            tags = tags
        )
        _posts.value = listOf(newPost) + _posts.value

        // Also add to IPFS pinned blocks
        val newBlock = IpfsBlock(
            cid = cid,
            label = "Social_Post_${newPost.id}.json",
            sizeBytes = content.toByteArray().size.toLong() + 256L,
            mimeType = "application/json",
            isPinned = true,
            replicationFactor = 1,
            addedTimestamp = System.currentTimeMillis()
        )
        _ipfsBlocks.value = listOf(newBlock) + _ipfsBlocks.value
    }

    fun toggleLike(postId: String) {
        _posts.value = _posts.value.map {
            if (it.id == postId) {
                val newLiked = !it.isLiked
                val newCount = if (newLiked) it.likesCount + 1 else (it.likesCount - 1).coerceAtLeast(0)
                it.copy(isLiked = newLiked, likesCount = newCount)
            } else it
        }
    }

    // Encrypted Chat Actions
    fun sendMessage(conversationId: String, text: String, destructDuration: DestructDuration) {
        val payload = CryptoEngine.encryptAesGcm(text)
        val remaining = if (destructDuration != DestructDuration.OFF && destructDuration != DestructDuration.BURN_ON_READ) {
            destructDuration.seconds
        } else null

        val newMsg = DirectMessage(
            id = "m-" + UUID.randomUUID().toString().take(8),
            senderId = myIdentity.address,
            senderName = myIdentity.username,
            isMe = true,
            plainText = text,
            cipherText = payload.cipherTextBase64,
            iv = payload.ivBase64,
            authTag = payload.authTagBase64,
            timestamp = System.currentTimeMillis(),
            destructDuration = destructDuration,
            remainingSeconds = remaining
        )

        val currentList = _messages.value[conversationId] ?: emptyList()
        val updatedMap = _messages.value.toMutableMap()
        updatedMap[conversationId] = currentList + newMsg
        _messages.value = updatedMap

        // Update conversation lastMessage
        _conversations.value = _conversations.value.map {
            if (it.id == conversationId) it.copy(lastMessage = newMsg) else it
        }
    }

    fun setConversationDestructTimer(conversationId: String, duration: DestructDuration) {
        _conversations.value = _conversations.value.map {
            if (it.id == conversationId) it.copy(activeDestructDuration = duration) else it
        }
    }

    fun burnMessageNow(conversationId: String, messageId: String) {
        val currentList = _messages.value[conversationId] ?: return
        val updatedMap = _messages.value.toMutableMap()
        updatedMap[conversationId] = currentList.filter { it.id != messageId }
        _messages.value = updatedMap
    }

    // Scheduled Recurring Messages
    fun scheduleMessage(
        recipientId: String,
        recipientName: String,
        text: String,
        scheduledTimeEpochMs: Long,
        recurrence: RecurrenceType
    ) {
        val item = ScheduledMessage(
            id = "s-" + UUID.randomUUID().toString().take(8),
            recipientId = recipientId,
            recipientName = recipientName,
            messageText = text,
            scheduledTimeEpochMs = scheduledTimeEpochMs,
            recurrence = recurrence
        )
        _scheduledMessages.value = listOf(item) + _scheduledMessages.value
    }

    fun cancelScheduledMessage(id: String) {
        _scheduledMessages.value = _scheduledMessages.value.filter { it.id != id }
    }

    fun dispatchScheduledMessageNow(id: String) {
        val found = _scheduledMessages.value.find { it.id == id } ?: return
        sendMessage(found.recipientId, found.messageText, DestructDuration.OFF)
        _scheduledMessages.value = _scheduledMessages.value.filter { it.id != id }
    }

    // IPFS Actions
    fun togglePin(cid: String) {
        _ipfsBlocks.value = _ipfsBlocks.value.map {
            if (it.cid == cid) it.copy(isPinned = !it.isPinned) else it
        }
    }

    fun addCustomIpfsBlock(label: String, content: String) {
        val cid = CryptoEngine.generateIpfsCid(content)
        val block = IpfsBlock(
            cid = cid,
            label = label,
            sizeBytes = content.toByteArray().size.toLong(),
            mimeType = "text/plain",
            isPinned = true,
            replicationFactor = 4,
            addedTimestamp = System.currentTimeMillis()
        )
        _ipfsBlocks.value = listOf(block) + _ipfsBlocks.value
    }

    // Multi-Device Sync
    fun triggerSyncNow() {
        scope.launch {
            _syncStatus.value = "Establishing noise handshake with paired nodes..."
            delay(800L)
            _syncStatus.value = "Syncing ratchet states & encrypted blocks..."
            delay(1000L)
            _syncedDevices.value = _syncedDevices.value.map {
                it.copy(lastSyncEpochMs = System.currentTimeMillis())
            }
            _syncStatus.value = "All 3 devices synchronized • P2P Mesh verified"
        }
    }

    // Developer API Keys & Plugins
    fun generateApiKey(name: String, permissions: List<String>): DevApiKey {
        val rawKey = "apk_live_" + UUID.randomUUID().toString().replace("-", "").take(20)
        val masked = "apk_live_" + rawKey.take(4) + "••••••••" + rawKey.takeLast(4)
        val newKey = DevApiKey(
            id = "k-" + UUID.randomUUID().toString().take(8),
            keyName = name,
            apiKeyMasked = masked,
            fullApiKey = rawKey,
            permissions = permissions,
            createdAtEpochMs = System.currentTimeMillis(),
            callCount = 0
        )
        _apiKeys.value = listOf(newKey) + _apiKeys.value
        return newKey
    }

    fun revokeApiKey(id: String) {
        _apiKeys.value = _apiKeys.value.filter { it.id != id }
    }

    fun togglePlugin(pluginId: String) {
        _plugins.value = _plugins.value.map {
            if (it.id == pluginId) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun executeDevApi(endpoint: String, method: String, payloadJson: String): String {
        return when (endpoint) {
            "/v1/vault/feed" -> {
                val count = _posts.value.size
                """{"status":"200 OK","node":"12D3KooWR7","posts_count":$count,"latest_cid":"${_posts.value.firstOrNull()?.ipfsCid ?: "none"}"}"""
            }
            "/v1/messages/e2ee_send" -> {
                """{"status":"201 CREATED","cipher":"AES-256-GCM","auth_tag":"0x8F92E...","ratchet_step":42}"""
            }
            "/v1/storage/ipfs/pin" -> {
                val mockCid = CryptoEngine.generateIpfsCid(payloadJson)
                """{"status":"200 OK","cid":"$mockCid","pinned_nodes":12,"storage_bytes":2400}"""
            }
            "/v1/crypto/public_key" -> {
                """{"status":"200 OK","owner":"${myIdentity.address}","fingerprint":"$myKeyFingerprint","curve":"Ed25519"}"""
            }
            else -> """{"status":"404 NOT FOUND","error":"Unknown API endpoint: $endpoint"}"""
        }
    }

    // Security & Biometric Lock
    fun setBiometricLockEnabled(enabled: Boolean) {
        _isBiometricLockEnabled.value = enabled
        if (!enabled) {
            _isVaultLocked.value = false
        }
    }

    fun lockVault() {
        _isVaultLocked.value = true
    }

    fun unlockVault(pin: String): Boolean {
        if (pin == _masterPin.value) {
            _isVaultLocked.value = false
            return true
        }
        return false
    }

    fun unlockVaultBiometric() {
        _isVaultLocked.value = false
    }

    fun updateMasterPin(newPin: String) {
        if (newPin.length == 4) {
            _masterPin.value = newPin
        }
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }
}
