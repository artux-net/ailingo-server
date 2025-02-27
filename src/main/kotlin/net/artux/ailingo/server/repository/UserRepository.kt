package net.artux.ailingo.server.repository

import net.artux.ailingo.server.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun getByLogin(login: String): Optional<UserEntity>
    fun findByLogin(login: String): Optional<UserEntity>
    fun findMemberByEmail(email: String): Optional<UserEntity>
    fun findByEmail(email: String): Optional<UserEntity>
}