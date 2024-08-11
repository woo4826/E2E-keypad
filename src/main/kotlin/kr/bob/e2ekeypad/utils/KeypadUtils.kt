package kr.bob.e2ekeypad.utils

import org.springframework.core.io.ClassPathResource
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.security.*
import java.util.*
import java.util.logging.Logger
import javax.crypto.Cipher
import javax.imageio.ImageIO

object KeypadUtils {
//
//    fun generateKeyPair(): KeyPair {
//        val keyGen = KeyPairGenerator.getInstance("RSA")
//        keyGen.initialize(2048)
//        return keyGen.generateKeyPair()
//    }

    fun generateKeyPad  (): Map<String, String> {
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", " ", "  ").shuffled()
        val keypadMap = keys.associateWith {
            //logger

            if( it == " " || it == "  "){
                ""
            }else{
                UUID.randomUUID().toString()
            }
        }

        return keypadMap
    }

    private fun genKeyHash(key: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(key.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }
    fun encrypt(data: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.toByteArray()))
    }

     fun generateBase64Image(keys: List<String>): String {
        return try {
            // 각 키에 대응하는 이미지를 불러옵니다.
            val images = keys.map { key ->
                val resourcePath = if (key == " " || key == "  ") "/static/images/NULL.png" else "/static/images/_$key.png"
                ImageIO.read(ClassPathResource(resourcePath).inputStream)
            }

            // 첫 번째 이미지를 기준으로 가로와 세로 크기를 설정합니다.
            val imageWidth = images[0].width
            val imageHeight = images[0].height

            // 합쳐질 최종 이미지의 크기를 계산합니다. (4x3 배열로 결합)
            val combinedImageWidth = imageWidth * 4
            val combinedImageHeight = imageHeight * 3

            // 최종 이미지를 생성합니다.
            val combinedImage = BufferedImage(combinedImageWidth, combinedImageHeight, BufferedImage.TYPE_INT_ARGB)
            val g: Graphics2D = combinedImage.createGraphics()

            // 이미지를 4x3 배열로 합칩니다.
            for (i in images.indices) {
                val x = (i % 4) * imageWidth
                val y = (i / 4) * imageHeight
                g.drawImage(images[i], x, y, null)
            }
            g.dispose()

            // 이미지를 Base64 문자열로 변환합니다.
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(combinedImage, "png", outputStream)
            val imageBytes = outputStream.toByteArray()
            Base64.getEncoder().encodeToString(imageBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error loading or combining images"
        }
    }
    fun decrypt(data: String, privateKey:  java.security.Key): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return String(cipher.doFinal(Base64.getDecoder().decode(data)))
    }
}