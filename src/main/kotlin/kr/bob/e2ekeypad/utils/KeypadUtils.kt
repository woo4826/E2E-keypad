package kr.bob.e2ekeypad.utils

import java.security.*
import java.util.*
import javax.crypto.Cipher
import kotlin.collections.HashMap

object KeypadUtils {

    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2048)
        return keyGen.generateKeyPair()
    }


    fun shuffleKeypad(): Map<String, String> {
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", " ", "  ")
        Collections.shuffle(keys) // Shuffle keys to randomize order

        val keypadMap = HashMap<String, String>()
        keys.forEach { key ->
            keypadMap[key] = UUID.randomUUID().toString() // Assign each key a unique UUID
        }

        // Shuffle the entries
        val shuffledEntries = keypadMap.entries.shuffled() // Shuffle the entries

        // Create a new LinkedHashMap to maintain the shuffled order
        val shuffledMap = LinkedHashMap<String, String>()
        shuffledEntries.forEach { entry ->
            shuffledMap[entry.key] = entry.value
        }

        return shuffledMap
    }
    fun encrypt(data: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.toByteArray()))
    }
}