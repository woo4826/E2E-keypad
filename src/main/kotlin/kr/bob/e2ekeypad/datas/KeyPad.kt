package kr.bob.e2ekeypad.datas

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "keypad", timeToLive = 3600)
class KeyPad(
    @Id val id: String? = null,
    val keypadId: String? = null,
    val hash: String? = null,
    val validUntil: String? = null,
    val keyList: List<Key>? = null
)

class Key(
    val keyHash: String? = null,
    var base64Img: String? = null,
    val keyType: String? = null
)