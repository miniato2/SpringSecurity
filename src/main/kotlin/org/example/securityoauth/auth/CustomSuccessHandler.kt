package org.example.securityoauth.auth

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.securityoauth.dto.CustomOAuth2User
import org.example.securityoauth.entity.RefreshToken
import org.example.securityoauth.jwt.JWTUtil
import org.example.securityoauth.repository.RefreshRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.util.*

@Component
class CustomSuccessHandler(
    private val jwtUtil: JWTUtil,
    @Value("\${spring.jwt.accessTokenExpirationTime}")
    private val accessTokenExpirationTime : Long,
    @Value("\${spring.jwt.refreshTokenExpirationTime}")
    private val refreshTokenExpirationTime : Long,
    private val refreshTokenRepository: RefreshRepository
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {

        val customUserDetail = authentication?.principal as CustomOAuth2User
        val username = customUserDetail.getUsername()

        val authorities = authentication.authorities
        val iterator = authorities.iterator()
        val auth = iterator.next()
        val role = auth.authority

        //토큰 생성
        val access = jwtUtil.createJwt("access", username, role, accessTokenExpirationTime)
        val refresh = jwtUtil.createJwt("refresh", username, role, refreshTokenExpirationTime)

        //refresh 토큰 저장
        addRefreshToken(username, refresh, refreshTokenExpirationTime)

        response?.addCookie(createCookie(key = "access", value = access))
        response?.addCookie(createCookie(key = "refresh", value = refresh))
        response?.sendRedirect("http://localhost:3000/")
    }

    private fun createCookie(key: String, value: String): Cookie {
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
        refreshTokenRepository.save(refreshToken)
    }
}