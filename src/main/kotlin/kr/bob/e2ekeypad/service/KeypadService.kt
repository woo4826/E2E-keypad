package kr.bob.e2ekeypad.service

import kr.bob.e2ekeypad.datas.Key
import kr.bob.e2ekeypad.datas.KeyPad
import kr.bob.e2ekeypad.datas.KeypadResponse
import kr.bob.e2ekeypad.repository.KeypadRepository
import kr.bob.e2ekeypad.utils.KeypadUtils
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.security.Key as JceKey
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.*
import javax.imageio.ImageIO

@Service
class KeypadService(private val keypadRepository: KeypadRepository) {

    private val secretKey: JceKey = SecretKeySpec("secretKey12345".toByteArray(), "HmacSHA256")

    fun requestKeypad(): Map<String, Any> {
        val keypadMap = KeypadUtils.shuffleKeypad()
        val keypadId = UUID.randomUUID().toString()
        val validUntil = (System.currentTimeMillis() + 60000).toString() // 유효기간 1분
        val sessionKey = generateSessionKey(keypadId)
        val sessionKeyHash = generateHash(sessionKey)
        val keyboardTimestamp = System.currentTimeMillis()

        val keyList = keypadMap.map { (key, value) ->
            generateHash(key)  // Here, generating hash instead of storing keys directly
        }

//        val image = generateBase64Image("dummy")  // Generating base64 image for response, modify as needed.

        val keyPad = KeyPad(
            id = keypadId,
            keypadId = keypadId,
            hash = sessionKeyHash,
            keyList = keypadMap.map { (key, value) ->
                Key(
                    keyHash = key,
                    base64Img = "",  // Not including base64 images in the keys array response.
                    keyType = value
                )
            }
        )
        keypadRepository.save(keypadId, keyPad)

        return mapOf(
            "keys" to keyList,
//            "image" to image,
            "sessionKey" to sessionKey,
            "sessionKeyHash" to sessionKeyHash,
            "keyboardTimestamp" to keyboardTimestamp,
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

    private fun generateBase64Image(key: String): String {
        return try {
            val resourcePath = if (key == " " || key == "  ") "/static/images/NULL.png" else "/static/images/_$key.png"
            val image = ImageIO.read(ClassPathResource(resourcePath).inputStream)
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(image, "png", outputStream)
            val imageBytes = outputStream.toByteArray()
            Base64.getEncoder().encodeToString(imageBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error loading image"
        }
    }
}