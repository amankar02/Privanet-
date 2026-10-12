package com.example.ciphernet.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.ciphernet.crypto.CryptoEngine
import com.example.ciphernet.model.Conversation
import com.example.ciphernet.model.DirectMessage
import com.example.ciphernet.model.IpfsBlock
import com.example.ciphernet.model.Post
import com.example.ciphernet.model.ScheduledMessage
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ExportResult(
    val fileName: String,
    val totalItems: Int,
    val sizeKb: Double,
    val checksumSha256: String,
    val isEncrypted: Boolean,
    val jsonContentPreview: String
)

object ExportHelper {

    fun generateVaultExport(
        posts: List<Post>,
        conversations: List<Conversation>,
        messages: Map<String, List<DirectMessage>>,
        scheduledMessages: List<ScheduledMessage>,
        ipfsBlocks: List<IpfsBlock>,
        userAddress: String,
        encryptVault: Boolean
    ): Pair<String, ExportResult> {
        val root = JSONObject()
        root.put("app", "CipherNet")
        root.put("schemaVersion", "1.0.0-sovereign")
        root.put("exportedAt", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date()))
        root.put("ownerAddress", userAddress)
        root.put("isEncryptedVault", encryptVault)

        // Social Posts
        val postsArray = JSONArray()
        posts.forEach { post ->
            val pObj = JSONObject().apply {
                put("id", post.id)
                put("channel", post.channel)
                put("author", post.author.username)
                put("authorAddress", post.author.address)
                put("content", post.content)
                put("signature", post.signature)
                put("ipfsCid", post.ipfsCid)
                put("timestamp", post.timestamp)
                put("likesCount", post.likesCount)
            }
            postsArray.put(pObj)
        }
        root.put("posts", postsArray)

        // Conversations & E2EE Messages
        val convosArray = JSONArray()
        conversations.forEach { conv ->
            val cObj = JSONObject().apply {
                put("conversationId", conv.id)
                put("peerName", conv.peerName)
                put("peerAddress", conv.peerAddress)
                put("keyFingerprint", conv.keyFingerprint)
                
                val msgList = messages[conv.id] ?: emptyList()
                val mArray = JSONArray()
                msgList.forEach { m ->
                    val mObj = JSONObject().apply {
                        put("id", m.id)
                        put("isMe", m.isMe)
                        put("plainText", m.plainText)
                        put("cipherText", m.cipherText)
                        put("iv", m.iv)
                        put("authTag", m.authTag)
                        put("timestamp", m.timestamp)
                        put("destructDurationSeconds", m.destructDuration.seconds)
                    }
                    mArray.put(mObj)
                }
                put("messages", mArray)
            }
            convosArray.put(cObj)
        }
        root.put("conversations", convosArray)

        // Scheduled Transmissions
        val schedArray = JSONArray()
        scheduledMessages.forEach { s ->
            val sObj = JSONObject().apply {
                put("id", s.id)
                put("recipientName", s.recipientName)
                put("messageText", s.messageText)
                put("scheduledTimeEpochMs", s.scheduledTimeEpochMs)
                put("recurrence", s.recurrence.name)
            }
            schedArray.put(sObj)
        }
        root.put("scheduledMessages", schedArray)

        // IPFS Blocks
        val ipfsArray = JSONArray()
        ipfsBlocks.forEach { b ->
            val bObj = JSONObject().apply {
                put("cid", b.cid)
                put("label", b.label)
                put("sizeBytes", b.sizeBytes)
                put("isPinned", b.isPinned)
            }
            ipfsArray.put(bObj)
        }
        root.put("ipfsPinnedBlocks", ipfsArray)

        val rawJsonString = root.toString(2)
        val finalPayload: String
        val checksum: String

        if (encryptVault) {
            val encrypted = CryptoEngine.encryptAesGcm(rawJsonString)
            val encObj = JSONObject().apply {
                put("ciphernet_encrypted_vault", true)
                put("cipherText", encrypted.cipherTextBase64)
                put("iv", encrypted.ivBase64)
                put("authTag", encrypted.authTagBase64)
                put("algorithm", "AES-256-GCM")
            }
            finalPayload = encObj.toString(2)
            checksum = CryptoEngine.sha256Fingerprint(finalPayload)
        } else {
            finalPayload = rawJsonString
            checksum = CryptoEngine.sha256Fingerprint(finalPayload)
        }

        val totalItems = posts.size + messages.values.sumOf { it.size } + scheduledMessages.size + ipfsBlocks.size
        val sizeKb = finalPayload.toByteArray(Charsets.UTF_8).size / 1024.0
        val fileName = if (encryptVault) "ciphernet_vault_encrypted.json" else "ciphernet_vault_export.json"

        val preview = if (encryptVault) {
            finalPayload.lines().take(15).joinToString("\n") + "\n... [Encrypted with AES-256-GCM]"
        } else {
            rawJsonString.lines().take(20).joinToString("\n") + "\n... [truncated]"
        }

        val result = ExportResult(
            fileName = fileName,
            totalItems = totalItems,
            sizeKb = String.format(Locale.US, "%.2f", sizeKb).toDoubleOrNull() ?: 1.0,
            checksumSha256 = checksum,
            isEncrypted = encryptVault,
            jsonContentPreview = preview
        )

        return Pair(finalPayload, result)
    }

    fun shareExportFile(context: Context, fileName: String, payload: String) {
        try {
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use {
                it.write(payload.toByteArray(Charsets.UTF_8))
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_SUBJECT, "CipherNet Sovereign Vault Backup")
                putExtra(Intent.EXTRA_TEXT, payload)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(shareIntent, "Save or Export CipherNet Vault").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
