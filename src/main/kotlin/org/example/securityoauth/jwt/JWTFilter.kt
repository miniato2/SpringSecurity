package org.example.securityoauth.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.securityoauth.dto.CustomOAuth2User
import org.example.securityoauth.dto.UserDTO
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
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

        //왜 굳이 다시 token이름으로 넣는지?
        val token = authorization

        //토큰 만료시간 검증
        if(jwtUtil.isExpired(token)){
            filterChain.doFilter(request, response)
            return
        }

        val username = jwtUtil.getUsername(token)
        val role = jwtUtil.getRole(token)

        val userDTO = UserDTO(username = username, role = role)
        val customOAuth2User = CustomOAuth2User(userDTO)

        //스프링 시큐리티 인증 토큰 생성
        val authtoken = UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.authorities)

        //세션에 사용자 등록
        SecurityContextHolder.getContext().authentication = authtoken

        filterChain.doFilter(request, response)
    }
}