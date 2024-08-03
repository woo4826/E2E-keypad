package kr.bob.e2ekeypad.repository

import kr.bob.e2ekeypad.datas.KeypadInfo
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class KeypadRepository(private val redisTemplate: RedisTemplate<String, KeypadInfo>) {

    fun save(keypadId: String, keypadInfo: KeypadInfo) {
        redisTemplate.opsForValue().set(keypadId, keypadInfo, 1000, TimeUnit.MINUTES)
    }

    fun findById(keypadId: String): KeypadInfo? {
        return redisTemplate.opsForValue().get(keypadId)
    }

    fun delete(keypadId: String) {
        redisTemplate.delete(keypadId)
    }
}
