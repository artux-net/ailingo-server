package net.artux.ailingo.server.repository

import net.artux.ailingo.server.entity.SavedTopicsEntity
import net.artux.ailingo.server.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SavedTopicsRepository : JpaRepository<SavedTopicsEntity, Long> {
    fun findByUser(user: UserEntity): SavedTopicsEntity?
}