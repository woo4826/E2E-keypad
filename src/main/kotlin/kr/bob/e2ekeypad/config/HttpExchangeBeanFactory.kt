package kr.bob.e2ekeypad.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
@Configuration
class AppConfig {

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder().build()
    }
}