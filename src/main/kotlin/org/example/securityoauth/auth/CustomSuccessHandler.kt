package org.example.securityoauth.auth

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.securityoauth.dto.CustomOAuth2User
import org.example.securityoauth.jwt.JWTutil
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomSuccessHandler(private val jwtUtil: JWTutil) : SimpleUrlAuthenticationSuccessHandler() {

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

        val token = jwtUtil.createJwt(username, role, 60 * 60 * 60L) // **

        response?.addCookie(createCookie("Authorization", token))
        response?.sendRedirect("http://localhost:3000/") // **
    }

    private fun createCookie(key: String, value: String): Cookie {
        val cookie = Cookie(key, value)
        cookie.maxAge = 60 * 60 * 60
        // cookie.isSecure = true
        cookie.path = "/"
        cookie.isHttpOnly = true
        return cookie
    }
}