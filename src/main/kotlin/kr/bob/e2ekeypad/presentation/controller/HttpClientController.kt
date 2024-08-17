package kr.bob.e2ekeypad.presentation.controller

import kr.bob.e2ekeypad.service.SampleHttpClientService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api")
@RestController
class SampleHttpClientController(
    private val sampleHttpClientService: SampleHttpClientService,
) {
    @GetMapping("/sample")
    fun test(): String {
        return "sample controller response"
    }

//    @GetMapping("/sample/http-exchange")
//    fun useHttpExchange() = sampleHttpClientService.useHttpExchange()
//
////    @GetMapping("/sample/open-feign")
////    fun useOpenFeign() = sampleHttpClientService.useOpenFeign()
//
//    @GetMapping("/sample/rest-client")
//    fun useRestClient() = sampleHttpClientService.useRestClient()

    @PostMapping("/submit/2" )
    fun submit(@RequestBody body: Map<String, Any>) = sampleHttpClientService.submitCall(body)

    @GetMapping("/sample/web-client")
    fun useWebClient() = sampleHttpClientService.useWebClientBlock()
}
