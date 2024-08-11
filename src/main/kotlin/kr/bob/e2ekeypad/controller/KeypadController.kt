package kr.bob.e2ekeypad.controller

import kr.bob.e2ekeypad.datas.InputRequest
import kr.bob.e2ekeypad.datas.InputResponse
import kr.bob.e2ekeypad.datas.KeypadResponse
import kr.bob.e2ekeypad.service.KeypadService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/keypad")
class KeypadController(private val keypadService: KeypadService) {

    @GetMapping()
    fun requestKeypad(): Map<String,Any> {
        return keypadService.requestKeypad()
    }

    @GetMapping("/{keypadId}")
    fun requestKeypad(@PathVariable keypadId: String): KeypadResponse {
        return keypadService.restoreKeypad(keypadId)
    }
//    @PostMapping("/input")
//    fun inputKey(@RequestBody inputRequest: InputRequest): InputResponse {
//        return keypadService.inputKey(inputRequest)
//    }
}
