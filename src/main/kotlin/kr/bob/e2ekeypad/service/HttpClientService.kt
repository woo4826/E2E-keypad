package kr.bob.e2ekeypad.service

import kr.bob.e2ekeypad.external.client.SampleWebClient
import org.springframework.stereotype.Service

@Service
class SampleHttpClientService(
//    private val sampleHttpExchange: SampleHttpExchange,
//    private val sampleOpenFeign: SampleOpenFeign,
//    private val submitCall : SampleRestClient,
//    private val sampleRestClient: SampleRestClient,
    private val sampleWebClient: SampleWebClient,
) {
//    fun useHttpExchange(): String = sampleHttpExchange.sample()
//
////    fun useOpenFeign(): String = sampleOpenFeign.sample()
//
//    fun useRestClient(): String = sampleRestClient.sample()
//
//    fun submitCall(body:Map<String,Any> ): String = sampleRestClient.submit(body = body)
    fun submitCall(body:Map<String,Any> ): String = sampleWebClient.submit(body=body).block()!!
    fun useWebClientBlock(): String = sampleWebClient.sample().block()!!
}
