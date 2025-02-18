package net.artux.ailingo.server.repository

import net.artux.ailingo.server.entity.TopicEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicRepository : JpaRepository<TopicEntity, Long> {
    fun deleteTopicByName(name: String)
    fun deleteTopicById(id: Long)
    fun findByName(name: String): TopicEntity?
}