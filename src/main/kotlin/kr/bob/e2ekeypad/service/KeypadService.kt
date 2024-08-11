package kr.bob.e2ekeypad.service

import kr.bob.e2ekeypad.datas.Key
import kr.bob.e2ekeypad.datas.KeyPad
import kr.bob.e2ekeypad.datas.KeypadResponse
import kr.bob.e2ekeypad.repository.KeypadRepository
import kr.bob.e2ekeypad.utils.KeypadUtils
import org.springframework.stereotype.Service
import java.security.Key as JceKey
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.*

@Service
class KeypadService(private val keypadRepository: KeypadRepository) {

    private val secretKey: JceKey = SecretKeySpec("secretKey12345".toByteArray(), "HmacSHA256")

    fun requestKeypad(): Map<String, Any> {
        val keypadMap = KeypadUtils.generateKeyPad()
        val keypadId = UUID.randomUUID().toString()
        val validUntil = (System.currentTimeMillis() + 60000).toString() // 유효기간 1분
        val sessionKey = generateSessionKey(keypadId)
        val sessionKeyHash = generateHash(sessionKey)
        val keyboardTimestamp = System.currentTimeMillis()

//        val keyList = keypadMap.map { (key, value) ->
//            generateHash(key)  // Here, generating hash instead of storing keys directly
//        }

//        val image = generateBase64Image("dummy")  // Generating base64 image for response, modify as needed.
        val randomKeyList = keypadMap.values.toList()
        val keyPad = KeyPad(
            id = keypadId,
            keypadId = keypadId,
            hash = sessionKeyHash,

            keyList = keypadMap.map { (key, value) ->
                Key(
                    keyHash = key,
                    keyType = value
                )
            }
        )
        keypadRepository.save(keypadId, keyPad)

        return mapOf(
            "keys" to randomKeyList,
            "sessionKey" to sessionKey,
            "sessionKeyHash" to sessionKeyHash,
            "keyboardTimestamp" to keyboardTimestamp,
            "backgroundImg" to KeypadUtils.generateBase64Image(keypadMap.keys.toList())
        )
    }

    fun restoreKeypad(keypadId: String): KeypadResponse {
        val keyPad: KeyPad = keypadRepository.findById(keypadId) ?: return KeypadResponse(null)
        keyPad.keyList!!.forEach {
            it.base64Img = ""
        }
        return KeypadResponse(keyPad)
    }

    private fun generateSessionKey(keypadId: String): String {
        return "$keypadId.${UUID.randomUUID()}".toUpperCase()
    }

    private fun generateHash(data: String): String {
        val hmac = Mac.getInstance("HmacSHA256")
        hmac.init(secretKey)
        return hmac.doFinal(data.toByteArray()).joinToString("") { "%02x".format(it) }
    }

}