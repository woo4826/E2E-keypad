package kr.bob.e2ekeypad.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
class FormController {
    @RequestMapping("/form")
    fun inputForm(): String {
        return "index"
    }

}