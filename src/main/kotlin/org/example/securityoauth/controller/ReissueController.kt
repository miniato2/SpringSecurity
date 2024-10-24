package org.example.securityoauth.controller

import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.securityoauth.entity.RefreshToken
import org.example.securityoauth.jwt.JWTUtil
import org.example.securityoauth.repository.RefreshRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*


@Controller
@ResponseBody
class ReissueController (
    private val jwtUtil: JWTUtil,
    @Value("\${spring.jwt.accessTokenExpirationTime}")
    private val accessTokenExpirationTime : Long,
    @Value("\${spring.jwt.refreshTokenExpirationTime}")
    private val refreshTokenExpirationTime : Long,
    private val refreshRepository: RefreshRepository
){

    @PostMapping("/reissue")
    fun reissue(request: HttpServletRequest, response: HttpServletResponse) : ResponseEntity<Any> {

        val refresh = request.cookies?.firstOrNull { it.name == "refresh" }?.value
            ?: return ResponseEntity("refresh token null", HttpStatus.BAD_REQUEST)

        //만료확인
        try {
            jwtUtil.isExpired(refresh)
        } catch (e: ExpiredJwtException) {
            return ResponseEntity("refresh token expired", HttpStatus.BAD_REQUEST)
        }

        //refresh 확인
        if (jwtUtil.getCategory(refresh) != "refresh") {
            return ResponseEntity("invalid refresh token", HttpStatus.BAD_REQUEST)
        }

        //저장되어있는지 확인
        if (!refreshRepository.existsByRefresh(refresh)) {
            return ResponseEntity("invalid refresh token", HttpStatus.BAD_REQUEST)
        }

        val username = jwtUtil.getUsername(refresh)
        val role = jwtUtil.getRole(refresh)

        val newAccess = jwtUtil.createJwt("access", username, role, accessTokenExpirationTime)
        val newRefresh = jwtUtil.createJwt("refresh", username, role, refreshTokenExpirationTime)

        //기존 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh)
        addRefreshToken(username, newRefresh, refreshTokenExpirationTime)

        response.setHeader("access", newAccess)
        response.addCookie(createCookie("refresh", newRefresh))

        return ResponseEntity(HttpStatus.OK)
    }

    private fun createCookie(key : String, value: String) : Cookie{
        val cookie = Cookie(key, value)
        cookie.maxAge = 24 * 60 * 60
        // cookie.isSecure = true
        cookie.path = "/"
        cookie.isHttpOnly = true
        return cookie
    }

    private fun addRefreshToken(username: String, refresh: String, expiredMs: Long){
        val date = Date(System.currentTimeMillis() + expiredMs)

        val refreshToken = RefreshToken(username = username, refresh = refresh, expiration = date.toString())
        refreshRepository.save(refreshToken)
    }
}