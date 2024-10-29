package org.example.securityoauth.jwt

import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.securityoauth.repository.RefreshRepository
import org.springframework.web.filter.GenericFilterBean

class CustomLogOutFilter(
    private val jwtUtil: JWTUtil,
    private val refreshRepository: RefreshRepository
) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        doFilter(request as HttpServletRequest, response as HttpServletResponse, chain)
    }

    private fun doFilter(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?){

        // 경로, 메소드 검증
        val requestUri = request!!.requestURI
        if (!requestUri.matches(Regex("^/logout$"))) {
            chain?.doFilter(request, response)
            return
        }

        val method = request.method
        if(!method.equals("POST")){
            chain!!.doFilter(request, response)
            return
        }

        //refresh null check
        val refresh = request.cookies?.firstOrNull { it.name == "refresh" }?.value
        if (refresh == null) {
            response!!.status = HttpServletResponse.SC_BAD_REQUEST
            return
        }

        //만료 check
        try{
            jwtUtil.isExpired(refresh)
        }catch (e: ExpiredJwtException){
            response!!.status = HttpServletResponse.SC_BAD_REQUEST
            return
        }

        val category = jwtUtil.getCategory(refresh)
        if(!category.equals("refresh")){
            response!!.status = HttpServletResponse.SC_BAD_REQUEST
            return
        }

        //db 저장확인
        val isExist: Boolean = refreshRepository.existsByRefresh(refresh)
        if(!isExist){
            response!!.status = HttpServletResponse.SC_BAD_REQUEST
            return
        }

        //로그아웃 진행
        //refresh db 제거
        refreshRepository.deleteByRefresh(refresh)

        //refresh cookie 값 0
        val cookie: Cookie = Cookie("refresh", null)
        cookie.maxAge = 0
        cookie.path = "/"

        response?.addCookie(cookie)
        response?.status = HttpServletResponse.SC_OK
    }
}