package  kr.bob.e2ekeypad.external.client


import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono

@Component
class SampleWebClient(
    webClientBuilder: WebClient.Builder,
) {
    private val webClient: WebClient = webClientBuilder
        .baseUrl("http://146.56.119.112:8081")
        .build()

    fun sample(): Mono<String> {
        return webClient.get().uri("/key/value/one/two")
            .retrieve()
            .bodyToMono(String::class.java)
    }

    suspend fun sampleCoroutine(): String {
        return webClient.get().uri("/key/value/one/two")
            .retrieve()
            .awaitBody<String>()
    }

    fun submit(body: Map<String, Any>): Mono<String> {
        return webClient.post().uri("/auth")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String::class.java)
    }
}
