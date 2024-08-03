package kr.bob.e2ekeypad.service

import kr.bob.e2ekeypad.datas.KeypadInfo
import kr.bob.e2ekeypad.datas.InputRequest
import kr.bob.e2ekeypad.datas.InputResponse
import kr.bob.e2ekeypad.datas.KeypadResponse
import kr.bob.e2ekeypad.repository.KeypadRepository
import kr.bob.e2ekeypad.utils.KeypadUtils
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.security.Key
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import javax.imageio.ImageIO

@Service
class KeypadService(private val keypadRepository: KeypadRepository) {

    private val secretKey: Key = SecretKeySpec("secretKey12345".toByteArray(), "HmacSHA256")

    fun requestKeypad(): KeypadResponse {
        val keypadMap = KeypadUtils.shuffleKeypad()
        val keypadImage = generateKeypadImage(keypadMap)
        val keypadId = UUID.randomUUID().toString()
        val validUntil = (System.currentTimeMillis() + 60000).toString() // 유효기간 1분
        val hash = generateHash(keypadId, validUntil)

        val keypadInfo = KeypadInfo(keypadMap, validUntil, hash, keypadImage)
        keypadRepository.save(keypadId, keypadInfo)
        val returnKey: Map<String, String> = keypadMap.map { (key, value) -> value to keypadImage[key]!! }.toMap()
        return KeypadResponse(keypadId, validUntil, returnKey)
    }

    fun inputKey(inputRequest: InputRequest): InputResponse {
        val keypadInfo = keypadRepository.findById(inputRequest.keypadId)

        return if (keypadInfo == null || !isValid(keypadInfo, inputRequest)) {
            InputResponse(false, "Invalid or expired keypad.")
        } else {
            val decryptedInput = decryptInput(inputRequest.encryptedInput, keypadInfo)
            if (keypadInfo.keypadMap.containsValue(decryptedInput)) {
                InputResponse(true, "Input accepted.")
            } else {
                InputResponse(false, "Invalid input.")
            }
        }
    }

    private fun generateHash(keypadId: String, validUntil: String): String {
        val hmac = Mac.getInstance("HmacSHA256")
        hmac.init(secretKey)
        return Base64.getEncoder().encodeToString(hmac.doFinal("$keypadId$validUntil".toByteArray()))
    }

    private fun isValid(keypadInfo: KeypadInfo, inputRequest: InputRequest): Boolean {
        val currentTime = System.currentTimeMillis().toString()
        return keypadInfo.validUntil > currentTime && keypadInfo.hash == generateHash(inputRequest.keypadId, keypadInfo.validUntil)
    }

    private fun decryptInput(encryptedInput: String, keypadInfo: KeypadInfo): String {
        // 입력 값 복호화 로직
        return encryptedInput // 단순화된 반환
    }

    private fun generateKeypadImage(keypadMap: Map<String, String>): Map<String, String> {
        val keypadImage = mutableMapOf<String, String>()

        keypadMap.forEach { (key, _) ->
            val base64Image = try {
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

            keypadImage[key] = base64Image
        }

        return keypadImage
    }
}
