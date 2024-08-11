package kr.bob.e2ekeypad.datas

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "keypad", timeToLive = 3600)
data class KeyPad @JsonCreator constructor(
    @Id @JsonProperty("id") val id: String? = null,
    @JsonProperty("keypadId") val keypadId: String? = null,
    @JsonProperty("validUntil") val validUntil: String? = null,
    @JsonProperty("publicKey") val publicKey: String? = null,
    @JsonProperty("privateKey") val privateKey: String? = null,
    @JsonProperty("hashedKeyList") val hashedKeyList: List<String>? = null,
    @JsonProperty("sessionKeyHash") val sessionKeyHash: String? = null
)

data class Key @JsonCreator constructor(
    @JsonProperty("keyHash") val keyHash: String? = null,
    @JsonProperty("base64Img") var base64Img: String? = null,
    @JsonProperty("keyType") val keyType: String? = null
)