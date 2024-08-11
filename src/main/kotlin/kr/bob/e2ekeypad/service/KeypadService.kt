package kr.bob.e2ekeypad.service

import kr.bob.e2ekeypad.datas.*
import kr.bob.e2ekeypad.repository.KeypadRepository
import kr.bob.e2ekeypad.utils.KeypadUtils
import org.springframework.stereotype.Service
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.security.Key as JceKey
import javax.crypto.Mac
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Service
class KeypadService(private val keypadRepository: KeypadRepository) {

    fun requestKeypad(): Map<String, Any> {
        val keypadMap = KeypadUtils.generateKeyPad()
        val keypadId = UUID.randomUUID().toString()
        val validUntil = (System.currentTimeMillis() + 1000 * 60 * 60).toString() // 1 hour
//        val  sessionKey = generateSessionKey(keypadId)

        val generatePubPriKeyPair = generatePubPriKeyPair()
        val pubKey = generatePubPriKeyPair.first
        val priKey = generatePubPriKeyPair.second

        val keyPad = KeyPad(
            id = keypadId,
            keypadId = keypadId,
            validUntil = validUntil,
            sessionKeyHash = generateHash(keypadId, validUntil, convertPublicKeyToBase64String(pubKey)),
            publicKey = convertPublicKeyToBase64String(pubKey),
            privateKey = convertPublicKeyToBase64String(priKey),
            hashedKeyList = keypadMap.map { (key, value) ->
                value
            }
        )
        keypadRepository.save(keypadId, keyPad)

        return mapOf(
            "keys" to  keyPad.hashedKeyList!!,
            "publicKey" to keyPad.publicKey!!,
            "keypadId" to keypadId,
            "backgroundImg" to KeypadUtils.generateBase64Image(keypadMap.keys.toList())
        )
    }

    fun convertPublicKeyToBase64String(publicKey: java.security.Key): String {
        return Base64.getEncoder().encodeToString(publicKey.encoded)
    }

    fun convertBase64StringToPublicKey(base64PublicKey: String): java.security.Key {
        val keyBytes = Base64.getDecoder().decode(base64PublicKey)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }
    private fun generatePubPriKeyPair (): Pair<JceKey, JceKey> {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.genKeyPair()
        return Pair(keyPair.public, keyPair.private)
    }

    fun submit (inputRequest: InputRequest): InputResponse {
        val iKeypadId = inputRequest.keypadId
        val keyPad: KeyPad = keypadRepository.findById(iKeypadId) ?: return InputResponse(false, "Invalid keypadId")
        val validUntil = keyPad.validUntil
        val publicKeyBase64 = inputRequest.publicKey

        val iEncryptedInput = inputRequest.encryptedInput

        if (validUntil== null ){
            return InputResponse(false, "cannot get data from redis storage")
        }
        if (validUntil.toLong() < System.currentTimeMillis()) {
            return InputResponse(false, "Expired keypadId")
        }
        if (keyPad.sessionKeyHash != generateHash(iKeypadId, validUntil, publicKeyBase64)) {
            return InputResponse(false, "Invalid properties")
        }

        //decrypt input
        val privateKeyBase64 :String= keyPad.privateKey ?: return InputResponse(false, "cannot get private key")
        val privateKey:  java.security.Key = convertBase64StringToPublicKey(privateKeyBase64) as Key

        val decryptedInput = KeypadUtils.decrypt(iEncryptedInput, privateKey)

        println("decryptedInput: $decryptedInput")


        return InputResponse(true, "Success")
    }


    //fun restoreKeypad(keypadId: String): KeypadResponse {
    //    val keyPad: KeyPad = keypadRepository.findById(keypadId) ?: return KeypadResponse(null)
    //
    //    return KeypadResponse(keyPad)
    //}
    private fun generateSessionKey(keypadId: String): String {
        return keypadId + System.currentTimeMillis()
    }

    private fun generateHash(keypadId : String, timeStamp: String, pubKeyBase64: String): String {
        val secretKey = SecretKeySpec("secret".toByteArray(), "HmacSHA256")

        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKey)
        val data = (keypadId + timeStamp + pubKeyBase64).toByteArray()
        val hash = mac.doFinal(data)
        return Base64.getEncoder().encodeToString(hash)
    }

}