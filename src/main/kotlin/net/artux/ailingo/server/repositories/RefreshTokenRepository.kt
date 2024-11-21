package net.artux.ailingo.server.repositories

import jakarta.transaction.Transactional
import net.artux.ailingo.server.entity.RefreshTokenEntity
import net.artux.ailingo.server.entity.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, UUID> {

    fun findByUser(user: UserEntity): Optional<RefreshTokenEntity>

    @Modifying
    @Transactional
    @Query("delete from RefreshTokenEntity rt where rt.user = :user")
    fun deleteByUser(user: UserEntity)

}