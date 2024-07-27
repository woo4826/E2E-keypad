package kr.bob.e2ekeypad.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/get-keypad")
    fun testA():String{
        return "Hello Spring";
    }
}