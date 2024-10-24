package org.example.securityoauth.controller

import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.securityoauth.jwt.JWTUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody


@Controller
@ResponseBody
class ReissueController (
    private val jwtUtil: JWTUtil,
    @Value("\${spring.jwt.accessTokenExpirationTime}")
    private val accessTokenExpirationTime : Long
){

    @PostMapping("/reissue")
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

        if(refresh == null){
            return ResponseEntity("refresh token null", HttpStatus.BAD_REQUEST)
        }

        try {
            jwtUtil.isExpired(refresh)
        }catch (e : ExpiredJwtException){
            return ResponseEntity("refresh token expired", HttpStatus.BAD_REQUEST)
        }

        val category = jwtUtil.getCategory(refresh)

        if(!category.equals("refresh")){
            return ResponseEntity("invalid refresh token", HttpStatus.BAD_REQUEST)
        }

        val username = jwtUtil.getUsername(refresh)
        val role = jwtUtil.getRole(refresh)

        val newAccess = jwtUtil.createJwt("access", username, role, accessTokenExpirationTime)

        response.setHeader("access", newAccess)

        return ResponseEntity(HttpStatus.OK)
    }
}