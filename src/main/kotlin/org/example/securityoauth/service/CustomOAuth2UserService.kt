package org.example.securityoauth.service

import org.example.securityoauth.dto.CustomOAuth2User
import org.example.securityoauth.dto.NaverResponse
import org.example.securityoauth.dto.UserDTO
import org.example.securityoauth.entity.User
import org.example.securityoauth.repository.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {

        val oAuth2User: OAuth2User = super.loadUser(userRequest)
        println("oAuth2User" + oAuth2User)
//        println("Access Token: ${userRequest?.accessToken?.tokenValue}")
        //access token도 확인할수 있다

        val registrationId : String? = userRequest?.clientRegistration?.registrationId

        println(oAuth2User.attributes["response"])
        val oAuth2Response = when (registrationId) {
            "naver" -> NaverResponse(oAuth2User.attributes)
            else -> throw OAuth2AuthenticationException("Unsupported provider: $registrationId")
        }
        println("oAuth2Response" + oAuth2Response)

        val username = "${oAuth2Response.getProvider()} ${oAuth2Response.getProviderId()}"

        val existsUser: User? = userRepository.findByUsername(username)

        if(existsUser == null){
            val user = User(username = username, name = oAuth2Response.getName(), email = oAuth2Response.getEmail(), role = "ROLE_USER")
            userRepository.save(user)

            val userDTO  = UserDTO("ROLE_USER", oAuth2Response.getName(), username)

            return CustomOAuth2User(userDTO)
        }else{

            existsUser.email = oAuth2Response.getEmail()
            existsUser.name = oAuth2Response.getName()

            println(existsUser)

            userRepository.save(existsUser)

            val userDTO  = UserDTO(role = existsUser.role, name = existsUser.name, username = existsUser.username)

            return CustomOAuth2User(userDTO)

        }
    }
}