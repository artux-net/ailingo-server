package net.artux.ailingo.server.repository

import net.artux.ailingo.server.entity.PendingUserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PendingUserRepository : JpaRepository<PendingUserEntity, Long> {
    fun findByVerificationCode(verificationCode: String): Optional<PendingUserEntity>
    fun findByEmail(email: String): Optional<PendingUserEntity>
}
