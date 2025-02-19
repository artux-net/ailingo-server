package net.artux.ailingo.server.service.impl

import net.artux.ailingo.server.entity.SavedTopicsEntity
import net.artux.ailingo.server.entity.TopicEntity
import net.artux.ailingo.server.repository.SavedTopicsRepository
import net.artux.ailingo.server.repository.UserRepository
import net.artux.ailingo.server.service.SavedTopicService
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class SavedTopicServiceImpl(
    private val userRepository: UserRepository,
    private val savedTopicsRepository: SavedTopicsRepository
) : SavedTopicService {

    private fun getCurrentUser() = userRepository.findByLogin(SecurityContextHolder.getContext().authentication.name)
        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден") }

    override fun getUserSavedTopics(): List<TopicEntity> {
        val currentUser = getCurrentUser()
        val savedTopicsEntity = savedTopicsRepository.findByUser(currentUser) ?: return emptyList()
        return savedTopicsEntity.savedTopics
    }

    override fun saveUserTopics(topics: List<TopicEntity>) {
        val currentUser = getCurrentUser()
        var savedTopicsEntity = savedTopicsRepository.findByUser(currentUser)

        if (savedTopicsEntity == null) {
            savedTopicsEntity = SavedTopicsEntity()
            savedTopicsEntity.user = currentUser
        }
        savedTopicsEntity.savedTopics.addAll(topics)
        savedTopicsRepository.save(savedTopicsEntity)
    }

    override fun removeUserTopic(topic: TopicEntity) {
        val currentUser = getCurrentUser()
        val savedTopicsEntity = savedTopicsRepository.findByUser(currentUser) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Сохраненные топики пользователя не найдены")

        savedTopicsEntity.savedTopics.removeIf { it.id == topic.id }
        savedTopicsRepository.save(savedTopicsEntity)
    }
}
