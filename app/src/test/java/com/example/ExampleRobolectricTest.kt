package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ciphernet.crypto.CryptoEngine
import com.example.ciphernet.data.CipherNetRepository
import com.example.ciphernet.model.DestructDuration
import com.example.ciphernet.model.RecurrenceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CipherNet", appName)
  }

  @Test
  fun `verify aes gcm encryption and decryption roundtrip`() {
    val testMessage = "Sovereign confidential payload #1337"
    val encrypted = CryptoEngine.encryptAesGcm(testMessage)
    
    assertNotNull(encrypted.cipherTextBase64)
    assertNotNull(encrypted.ivBase64)
    assertNotNull(encrypted.authTagBase64)
    
    val decrypted = CryptoEngine.decryptAesGcm(
        cipherTextBase64 = encrypted.cipherTextBase64,
        ivBase64 = encrypted.ivBase64,
        authTagBase64 = encrypted.authTagBase64
    )
    assertEquals(testMessage, decrypted)
  }

  @Test
  fun `verify repository sovereign identity and post publishing`() {
    val repo = CipherNetRepository()
    assertEquals("@cipher_48f9", repo.myIdentity.username)
    
    val initialCount = repo.posts.value.size
    repo.addPost("#privacy-tech", "Test automated mesh post", listOf("Privacy", "Test"))
    
    assertEquals(initialCount + 1, repo.posts.value.size)
    val latestPost = repo.posts.value.first()
    assertEquals("Test automated mesh post", latestPost.content)
    assertTrue(latestPost.ipfsCid.startsWith("bafybei"))
  }

  @Test
  fun `verify vault pin lock and biometric unlock`() {
    val repo = CipherNetRepository()
    repo.setBiometricLockEnabled(true)
    repo.lockVault()
    assertTrue(repo.isVaultLocked.value)

    val wrongPinResult = repo.unlockVault("9999")
    assertFalse(wrongPinResult)
    assertTrue(repo.isVaultLocked.value)

    val rightPinResult = repo.unlockVault("1337")
    assertTrue(rightPinResult)
    assertFalse(repo.isVaultLocked.value)
  }
}
