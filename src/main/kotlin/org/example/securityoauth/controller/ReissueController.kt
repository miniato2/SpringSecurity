package org.example.securityoauth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.securityoauth.jwt.JWTUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody


@Controller
@ResponseBody
class ReissueController (
    private val jwtUtil: JWTUtil,
){

    @PostMapping
    fun reissue(request: HttpServletRequest, response: HttpServletResponse) : ResponseEntity<Any> {
        var refresh : String? = null
        val cookies = request.cookies

        cookies?.let {
            for(cookie in it){
                if(cookie.name == "refresh"){
                    refresh = cookie.value
                }
            }
        }


        return ResponseEntity(HttpStatus.OK)
    }
}