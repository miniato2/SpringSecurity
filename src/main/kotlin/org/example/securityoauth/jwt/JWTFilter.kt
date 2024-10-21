package org.example.securityoauth.jwt

import io.jsonwebtoken.ExpiredJwtException
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
//        var authorization: String? = null
//        val cookies = request.cookies
//
//        cookies?.let {
//            for (cookie in it) {
//                println(cookie.name)
//                if (cookie.name == "access") {
//                    authorization = cookie.value
//                }
//            }
//        }

        val accessToken = request.getHeader("access")

        //인증이 필요하지 않은 요청에 대해서는 다음필터로 넘겨줌
        if(accessToken == null){
            filterChain.doFilter(request, response)
            return
        }


        //토큰 만료시간 검증
//        if(jwtUtil.isExpired(accessToken)){
//            filterChain.doFilter(request, response)
//            return
//        }

        //토큰 만료시
        try{
            jwtUtil.isExpired(accessToken)
        }catch (e : ExpiredJwtException){
            val writer = response.writer
            writer.print("invalid access token");

            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }

        //토큰 access 인지 확인(발급시 페이로드에 명시)
        val category: String = jwtUtil.getCategory(accessToken)

        if(!category.equals("access")) {
            //http 응답 body에 메세지 작성하기 위함
            val writer = response.writer
            writer.print("invalid access token")

            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }

        val username = jwtUtil.getUsername(accessToken)
        val role = jwtUtil.getRole(accessToken)

        val userDTO = UserDTO(username = username, role = role)
        val customOAuth2User = CustomOAuth2User(userDTO)

        //스프링 시큐리티 인증 토큰 생성
        val authtoken = UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.authorities)

        //세션에 사용자 등록
        SecurityContextHolder.getContext().authentication = authtoken

        filterChain.doFilter(request, response)
    }
}