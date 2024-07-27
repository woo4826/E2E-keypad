package kr.bob.e2ekeypad.controller


import kr.bob.e2ekeypad.datas.*
import kr.bob.e2ekeypad.utils.KeypadUtils
import org.springframework.web.bind.annotation.*
import java.security.*
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import java.util.Base64
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

@RestController
@RequestMapping("/api/keypad")
@CrossOrigin(origins = ["*"])
class KeypadController {

    private val keypads = HashMap<String, KeypadInfo>()
    private val secretKey = SecretKeySpec("secretKey12345".toByteArray(), "HmacSHA256")

    @GetMapping("/request")
    fun requestKeypad(): KeypadResponse {
//        val publicKeyRequest = KeypadUtils.generateKeyPair()
//        val clientPublicKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyRequest.clientPublicKey)))

        val keypadMap = KeypadUtils.shuffleKeypad()
        val keypadImage = generateKeypadImage(keypadMap)
        val keypadId = UUID.randomUUID().toString()
        val validUntil = (System.currentTimeMillis() + 60000).toString() // 유효기간 1분
        val hash = generateHash(keypadId, validUntil)


        keypads[keypadId] = KeypadInfo(keypadMap, validUntil, hash, keypadImage)
        //return key 's key is keypadMap's value, value is keypadimage's value
        val returnKey :Map<String,String> = keypadMap.map { (key, value) -> value to keypadImage[key]!! }.toMap()

        return KeypadResponse(keypadId,
                validUntil,
                returnKey,)
    }

    @PostMapping("/input")
    fun inputKey(@RequestBody inputRequest: InputRequest): InputResponse {
        val keypadInfo = keypads[inputRequest.keypadId]

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


    fun generateKeypadImage(keypadMap: Map<String, String>): Map<String, String> {
        val keypadImage = mutableMapOf<String, String>()

        keypadMap.forEach { (key, _) ->
            val base64Image = try {
                // Load the image from the resources folder
                val resource: Resource = if(key == " " || key =="  ")   ClassPathResource("/static/images/NULL.png") else ClassPathResource("/static/images/$key.png")
                val image = ImageIO.read(resource.inputStream)

                // Convert the image to byte array
                val outputStream = ByteArrayOutputStream()
                ImageIO.write(image, "png", outputStream)  // You might need to adjust the format based on your images
                val imageBytes = outputStream.toByteArray()

                // Encode byte array to Base64
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