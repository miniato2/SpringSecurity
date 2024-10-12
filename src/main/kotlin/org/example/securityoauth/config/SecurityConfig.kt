package org.example.securityoauth.config

import org.example.securityoauth.auth.CustomSuccessHandler
import org.example.securityoauth.jwt.JWTUtil
import org.example.securityoauth.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

// kotlin dsl을 사용하기 위해서 명시적인 import가 필요하다.
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
class SecurityConfig (
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customSuccessHandler: CustomSuccessHandler,
    private val jwtUtil: JWTUtil
){

    @Bean
    fun filterChain(http: HttpSecurity) : SecurityFilterChain {

        http{

            httpBasic { disable() }
            cors { disable() }
            csrf { disable() } //REST api 에서는 stateless 하기 때문에 CSRF보호가 필요없음

            authorizeHttpRequests {
                authorize("/", permitAll)
                authorize("/login", permitAll)
                authorize("/signup", permitAll)
                authorize(anyRequest, authenticated)
            }

            oauth2Login {
                userInfoEndpoint {
                    customOAuth2UserService
                }
                authenticationSuccessHandler = customSuccessHandler
            }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS // 세션을 사용하지 않음 (Stateless)
            }
        }

        return http.build()
    }
}