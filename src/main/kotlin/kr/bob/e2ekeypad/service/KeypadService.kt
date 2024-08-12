package kr.bob.e2ekeypad.service

import kr.bob.e2ekeypad.E2eKeypadApplication
import kr.bob.e2ekeypad.controller.KeypadController
import kr.bob.e2ekeypad.datas.*
import kr.bob.e2ekeypad.repository.KeypadRepository
import kr.bob.e2ekeypad.utils.KeypadUtils
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class KeypadService(
    private val keypadRepository: KeypadRepository,
) {

    // 서비스 인스턴스가 생성될 때 로컬에 키페어를 생성하여 저장
    private val publicKey: PublicKey = loadPublicKey()

    fun requestKeypad(): Map<String, Any> {
        val keypadMap = KeypadUtils.generateKeyPad()
        val keypadId = UUID.randomUUID().toString()
        val validUntil = (System.currentTimeMillis() + 1000 * 60 * 60).toString() // 1 hour


        val keyPad = KeyPad(
            id = keypadId,
            keypadId = keypadId,
            validUntil = validUntil,
            sessionKeyHash = generateHash(keypadId, validUntil, publicKey.toString()),
            hashedKeyList = keypadMap.map { (key, value) ->
                //encrypt using public key

               KeypadUtils.encrypt(value,publicKey)
            }
        )
        keypadRepository.save(keypadId, keyPad)

        return mapOf(
            "keys" to keyPad.hashedKeyList!!,
            "keypadId" to keypadId,
            "backgroundImg" to KeypadUtils.generateBase64Image(keypadMap.keys.toList())
        )
    }


    private fun loadPublicKey(): PublicKey {
        // 파일 경로: src/main/resources/public_key.pem
        val publicKeyFile = File("src/main/resources/keys/public_key.pem")

        val keyLines = BufferedReader(FileReader(publicKeyFile)).use { reader ->
            reader.lineSequence()
                .filter { line -> !line.startsWith("-----") }  // PEM의 헤더와 풋터는 제외
                .joinToString("") // 나머지 라인을 합쳐서 한 줄의 문자열로 변환
        }

        // Base64 디코딩
        val keyBytes = Base64.getDecoder().decode(keyLines)

        // X509EncodedKeySpec을 사용하여 공개 키를 생성
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePublic(keySpec)
    }
    fun submit(inputRequest: InputRequest): InputResponse {
        val iKeypadId = inputRequest.keypadId
        val keyPad: KeyPad = keypadRepository.findById(iKeypadId) ?: return InputResponse(false, "Invalid keypadId")
        val validUntil = keyPad.validUntil
        val iEncryptedInput = inputRequest.encryptedInput
        val iHmac :String = inputRequest.hmac

        if (validUntil == null) {
            return InputResponse(false, "Cannot get data from Redis storage")
        }
        if (validUntil.toLong() < System.currentTimeMillis()) {
            return InputResponse(false, "Expired keypadId")
        }
        if (iHmac != generateHash(iKeypadId, validUntil, publicKey.toString())) {
            return InputResponse(false, "Invalid properties")
        }

        // 로컬에 저장된 private key를 사용해 inputRequest의 encryptedKey를 복호화
//        val decryptedInput = KeypadUtils.decrypt(iEncryptedInput, keyPair.private)
//
//        println("decryptedInput: $decryptedInput")

        return InputResponse(true, "Success")
    }

    private fun generateHash(keypadId: String, timeStamp: String, pubKeyBase64: String): String {
        val secretKey = SecretKeySpec("secret".toByteArray(), "HmacSHA256")

        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKey)
        val data = (keypadId + timeStamp + pubKeyBase64).toByteArray()
        val hash = mac.doFinal(data)
        return Base64.getEncoder().encodeToString(hash)
    }
}