package com.example.ciphernet.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.example.ciphernet.data.CipherNetRepository
import com.example.ciphernet.model.DevApiKey
import com.example.ciphernet.model.PluginDefinition
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.DestructRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.QuantumViolet

@Composable
fun DevSdkScreen(
    repository: CipherNetRepository,
    modifier: Modifier = Modifier
) {
    val apiKeys by repository.apiKeys.collectAsState()
    val plugins by repository.plugins.collectAsState()

    var showCreateKeyDialog by remember { mutableStateOf(false) }

    // API Console State
    var selectedEndpoint by remember { mutableStateOf("/v1/vault/feed") }
    var apiResponseText by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Developer SDK Hero Header
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, QuantumViolet.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .testTag("dev_sdk_hero_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = QuantumViolet,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Developer SDK & Open API",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(QuantumViolet.copy(alpha = 0.2f))
                                .border(1.dp, QuantumViolet, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "REST & IPC v1.0",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuantumViolet
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Build custom privacy plugins, bots, and external mesh integrations using the CipherNet Open API specification. All communications are verified via client signatures.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Interactive API Tester Sandbox
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .testTag("api_sandbox_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interactive API Sandbox",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Button(
                            onClick = {
                                apiResponseText = repository.executeDevApi(selectedEndpoint, "GET", "{}")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("test_api_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Execute", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Endpoint options
                    val endpoints = listOf(
                        "/v1/vault/feed",
                        "/v1/messages/e2ee_send",
                        "/v1/storage/ipfs/pin",
                        "/v1/crypto/public_key"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        endpoints.take(2).forEach { ep ->
                            val isSel = ep == selectedEndpoint
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) ElectricCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (isSel) ElectricCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        selectedEndpoint = ep
                                        apiResponseText = null
                                    }
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ep.replace("/v1/", ""),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = if (isSel) ElectricCyan else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        endpoints.drop(2).forEach { ep ->
                            val isSel = ep == selectedEndpoint
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) ElectricCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (isSel) ElectricCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        selectedEndpoint = ep
                                        apiResponseText = null
                                    }
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ep.replace("/v1/", ""),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = if (isSel) ElectricCyan else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    apiResponseText?.let { resp ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(0.8.dp, CyberEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = resp,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = CyberEmerald
                            )
                        }
                    }
                }
            }
        }

        // API Keys Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Developer API Credentials (${apiKeys.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberEmerald.copy(alpha = 0.2f))
                        .clickable { showCreateKeyDialog = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("create_api_key_button")
                ) {
                    Text(
                        text = "+ Generate Key",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberEmerald
                    )
                }
            }
        }

        items(apiKeys, key = { it.id }) { key ->
            ApiKeyRowCard(
                key = key,
                onRevoke = { repository.revokeApiKey(key.id) }
            )
        }

        // Plugin Ecosystem Section
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Installed Custom Plugins (${plugins.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${plugins.count { it.isEnabled }} Enabled",
                    fontSize = 11.sp,
                    color = CyberEmerald
                )
            }
        }

        items(plugins, key = { it.id }) { plugin ->
            PluginRowCard(
                plugin = plugin,
                onToggle = { repository.togglePlugin(plugin.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showCreateKeyDialog) {
        CreateApiKeyDialog(
            onDismiss = { showCreateKeyDialog = false },
            onCreate = { name, perms ->
                repository.generateApiKey(name, perms)
                showCreateKeyDialog = false
            }
        )
    }
}

@Composable
fun ApiKeyRowCard(key: DevApiKey, onRevoke: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .testTag("api_key_${key.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ElectricCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = key.keyName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = key.apiKeyMasked,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = CyberEmerald
                )
                Text(
                    text = "Scopes: ${key.permissions.joinToString(", ")} • ${key.callCount} calls",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onRevoke, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Revoke Key",
                    tint = DestructRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun PluginRowCard(plugin: PluginDefinition, onToggle: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (plugin.isEnabled) CyberEmerald.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
            .testTag("plugin_item_${plugin.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Extension,
                        contentDescription = null,
                        tint = if (plugin.isEnabled) CyberEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = plugin.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "v${plugin.version}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = plugin.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyberEmerald),
                    modifier = Modifier.testTag("plugin_switch_${plugin.id}")
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = plugin.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Permissions: ${plugin.permissionsRequired.joinToString(", ")} • by ${plugin.author}",
                fontSize = 10.sp,
                color = ElectricCyan
            )
        }
    }
}

@Composable
fun CreateApiKeyDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, perms: List<String>) -> Unit
) {
    var keyName by remember { mutableStateOf("External Analytics Bot") }
    var readFeed by remember { mutableStateOf(true) }
    var writeMessages by remember { mutableStateOf(true) }
    var ipfsPin by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Generate Developer API Key",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = keyName,
                    onValueChange = { keyName = it },
                    label = { Text("App / Service Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Granted Scopes:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = readFeed, onCheckedChange = { readFeed = it })
                    Text("read:feed (Read signed posts)", fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = writeMessages, onCheckedChange = { writeMessages = it })
                    Text("write:messages (Inject E2EE messages)", fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = ipfsPin, onCheckedChange = { ipfsPin = it })
                    Text("ipfs:pin (Pin blocks to DHT)", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val perms = mutableListOf<String>()
                            if (readFeed) perms.add("read:feed")
                            if (writeMessages) perms.add("write:messages")
                            if (ipfsPin) perms.add("ipfs:pin")
                            onCreate(keyName, perms)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald)
                    ) {
                        Text("Create Key", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
