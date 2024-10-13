package org.example.securityoauth.dto

data class UserDTO(
    val role: String,
    val name: String,
    val username: String
){
    constructor(role: String, username: String) : this(role, username, "Empty")
}