package org.example.securityoauth.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(private val userDTO: UserDTO) : OAuth2User{

    fun getUsername(): String {

        return userDTO.username
    }

    override fun getName(): String {

        return userDTO.name
    }

    override fun getAttributes(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()

        authorities.add(GrantedAuthority { userDTO.role })

        return authorities
    }
}