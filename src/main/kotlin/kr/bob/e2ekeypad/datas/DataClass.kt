package kr.bob.e2ekeypad.datas

data class PublicKeyRequest(val publicKey: String,)

//data class KeypadResponse(val keypadId: String, val validUntil: String,val keypad: KeyPad?,)
data class KeypadResponse( val keypad: KeyPad?,)


data class InputRequest(val keypadId: String, val encryptedInput: String, val publicKey:String, val hmac : String)

data class InputResponse(val success: Boolean, val message: String,)

data class KeypadInfo(val keypadMap: Map<String, String>, val validUntil: String, val hash: String, val imageMap : Map<String, String>,)

data class SubmitRequest(val keypadId : String, val keyLength: Int, val data: String)