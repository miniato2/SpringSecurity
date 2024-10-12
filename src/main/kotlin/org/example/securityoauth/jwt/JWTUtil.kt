package org.example.securityoauth.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JWTUtil (
    @Value("\${spring.jwt.secret}")
    private val secret: String
){
    private val secretKey: SecretKey

    init {
        secretKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), Keys.hmacShaKeyFor(secret.toByteArray()).algorithm)
    }

    fun getUsername(token: String?): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJwt(token)
            .body
            .get("username", String::class.java)
    }

    fun getRole(token: String?): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJwt(token)
            .body
            .get("role", String::class.java)
    }

    fun isExpired(token: String?): Boolean {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJwt(token)
            .body
            .expiration.before(Date())
    }

    fun createJwt(username: String, role: String, expiredMs: Long): String{
        return Jwts.builder()
            .claim("username", username)
            .claim("role", role)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiredMs))
            .signWith(secretKey)
            .compact()
    }
}