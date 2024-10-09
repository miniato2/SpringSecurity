package org.example.securityoauth.dto


interface OAuth2Response {
    fun getProvider(): String
    fun getProviderId(): String
    fun getEmail(): String
    fun getName(): String
}