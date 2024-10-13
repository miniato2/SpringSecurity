package org.example.securityoauth.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class JWTFilter(
    private val jwtUtil: JWTUtil
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        //쿠키에서 JWT 추출
        var authorization: String? = null
        val cookies = request.cookies

        cookies?.let {
            for (cookie in it) {
                println(cookie.name)
                if (cookie.name == "Authorization") {
                    authorization = cookie.value
                }
            }
        }

        //인증이 필요하지 않은 요청에 대해서는 다음필터로 넘겨줌
        if(authorization == null){
            filterChain.doFilter(request, response)
            return
        }


    }
}