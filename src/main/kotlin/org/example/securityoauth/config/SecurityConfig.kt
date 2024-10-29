package org.example.securityoauth.config

import org.example.securityoauth.auth.CustomSuccessHandler
import org.example.securityoauth.jwt.CustomLogOutFilter
import org.example.securityoauth.jwt.JWTFilter
import org.example.securityoauth.jwt.JWTUtil
import org.example.securityoauth.repository.RefreshRepository
import org.example.securityoauth.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

// kotlin dsl을 사용하기 위해서 명시적인 import가 필요하다.
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig (
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customSuccessHandler: CustomSuccessHandler,
    private val jwtUtil: JWTUtil,
    private val refreshRepository: RefreshRepository
){

    @Bean
    fun filterChain(http: HttpSecurity) : SecurityFilterChain {

        http{

            httpBasic { disable() }

            csrf { disable() } //REST api 에서는 stateless 하기 때문에 CSRF보호가 필요없음

            //경로별 인가
            authorizeHttpRequests {
                authorize("/", permitAll)
                authorize("/login", permitAll)
                authorize("/signup", permitAll)
                authorize("/reissue", permitAll)
                authorize(anyRequest, authenticated)
            }

            //oauth
            oauth2Login {
                userInfoEndpoint {
                    customOAuth2UserService
                }
                authenticationSuccessHandler = customSuccessHandler
            }

            //필터 추가
            //JWTFilter는 모든 요청에 대해 UsernamePasswordAuthenticationFilter가 실행되기 전에 실행된다.
            addFilterBefore<UsernamePasswordAuthenticationFilter>(JWTFilter(jwtUtil))

            //LogOut
            addFilterBefore<LogoutFilter>(CustomLogOutFilter(jwtUtil, refreshRepository))

            cors {
                configurationSource = CorsConfigurationSource{
                    CorsConfiguration().apply {
                            allowedOrigins = listOf("http://localhost:3000") // 허용할 원본
                            allowedMethods = listOf("*") // 모든 HTTP 메서드 허용
                            allowCredentials = true // 자격 증명을 포함하는 요청 허용
                            allowedHeaders = listOf("*") // 모든 헤더 허용
                            maxAge = 3600L // 최대 캐시 시간 (초) = cors 요청의 유효기간

                            exposedHeaders = listOf("Set-Cookie", "Authorization") // 클라이언트가 접근할 수 있는 헤더
                    }
                }
            }

            //세션설정
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS // 세션을 사용하지 않음 (Stateless)
            }
        }

        return http.build()
    }
}