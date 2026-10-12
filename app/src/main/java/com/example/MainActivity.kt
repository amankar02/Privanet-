package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ciphernet.data.CipherNetRepository
import com.example.ciphernet.ui.components.LockScreenOverlay
import com.example.ciphernet.ui.screens.ChatScreen
import com.example.ciphernet.ui.screens.DevSdkScreen
import com.example.ciphernet.ui.screens.FeedScreen
import com.example.ciphernet.ui.screens.SettingsScreen
import com.example.ciphernet.ui.screens.StorageScreen
import com.example.ciphernet.ui.screens.SyncScreen
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme

enum class NavTab(val label: String, val icon: ImageVector) {
    FEED("Feed", Icons.Default.Hub),
    MESSAGES("Chat", Icons.Default.Lock),
    STORAGE("IPFS", Icons.Default.CloudQueue),
    SYNC("Sync", Icons.Default.Devices),
    DEV_SDK("API", Icons.Default.Code),
    SETTINGS("Vault", Icons.Default.Security)
}

class MainActivity : ComponentActivity() {
    private val repository by lazy { CipherNetRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by repository.themeMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val useDark = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> systemDark
            }

            MyApplicationTheme(darkTheme = useDark) {
                CipherNetMainApp(repository = repository)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (repository.isBiometricLockEnabled.value) {
            repository.lockVault()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CipherNetMainApp(repository: CipherNetRepository) {
    var currentTab by remember { mutableStateOf(NavTab.FEED) }
    val isVaultLocked by repository.isVaultLocked.collectAsState()
    val isBiometricEnabled by repository.isBiometricLockEnabled.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(CyberEmerald)
                                    .border(1.dp, ElectricCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "CipherNet",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CyberEmerald.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "E2EE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = CyberEmerald
                                        )
                                    }
                                }
                                Text(
                                    text = repository.myIdentity.username,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = ElectricCyan
                                )
                            }
                        }
                    },
                    actions = {
                        if (isBiometricEnabled) {
                            IconButton(
                                onClick = { repository.lockVault() },
                                modifier = Modifier.testTag("appbar_quick_lock_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock Vault",
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    NavTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = CyberEmerald,
                                indicatorColor = CyberEmerald,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (currentTab) {
                    NavTab.FEED -> FeedScreen(repository = repository)
                    NavTab.MESSAGES -> ChatScreen(repository = repository)
                    NavTab.STORAGE -> StorageScreen(repository = repository)
                    NavTab.SYNC -> SyncScreen(repository = repository)
                    NavTab.DEV_SDK -> DevSdkScreen(repository = repository)
                    NavTab.SETTINGS -> SettingsScreen(repository = repository)
                }
            }
        }

        // Biometric / PIN Lock Overlay
        LockScreenOverlay(
            isLocked = isVaultLocked,
            onUnlockWithPin = { pin ->
                repository.unlockVault(pin)
            },
            onUnlockBiometric = {
                repository.unlockVaultBiometric()
            }
        )
    }
}
