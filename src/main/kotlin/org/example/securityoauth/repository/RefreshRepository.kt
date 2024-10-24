package org.example.securityoauth.repository

import org.example.securityoauth.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface RefreshRepository : JpaRepository<RefreshToken, Long> {

    fun existsByRefresh(refresh: String) : Boolean

    @Transactional
    fun deleteByRefresh(refresh: String)

}