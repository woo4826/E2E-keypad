package kr.bob.e2ekeypad.utils

import java.security.*
import java.util.*
import java.util.Collections.shuffle
import javax.crypto.Cipher
import kotlin.collections.HashMap

object KeypadUtils {

    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2048)
        return keyGen.generateKeyPair()
    }

    fun shuffleKeypad(): Map<String, String> {
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", " ", " ")

        // Shuffle keys to randomize order
        val shuffledKeys = keys.shuffled()

        // Assign each key a UUID or keep it as is for spaces
        val keypadMap = shuffledKeys.associateWith { key ->
            if(key == " ") ""
            else UUID.randomUUID().toString()
        }

        return keypadMap
    }
    fun encrypt(data: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.toByteArray()))
    }
}