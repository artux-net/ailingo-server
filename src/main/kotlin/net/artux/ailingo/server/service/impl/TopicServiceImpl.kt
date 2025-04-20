package net.artux.ailingo.server.service.impl

import net.artux.ailingo.server.dto.CreateTopicDTO
import net.artux.ailingo.server.dto.TopicResponseDTO
import net.artux.ailingo.server.dto.UpdateTopicDTO
import net.artux.ailingo.server.entity.TopicEntity
import net.artux.ailingo.server.model.MessageType
import net.artux.ailingo.server.repository.MessageHistoryRepository
import net.artux.ailingo.server.repository.TopicRepository
import net.artux.ailingo.server.service.TopicService
import net.artux.ailingo.server.service.UserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class TopicServiceImpl(
    val topicRepository: TopicRepository,
    val userService: UserService,
    val historyRepository: MessageHistoryRepository
) : TopicService {
    override fun getTopics(): List<TopicResponseDTO> {
        val currentUser = userService.getCurrentUser()

        return topicRepository.findAll().map { topicEntity ->
            val isCompleted = historyRepository.existsByTopicAndOwnerAndType(topicEntity, currentUser, MessageType.FINAL)

            TopicResponseDTO(
                id = topicEntity.id,
                name = topicEntity.name,
                imageUrl = topicEntity.image,
                price = topicEntity.price,
                welcomePrompt = topicEntity.welcomePrompt,
                systemPrompt = topicEntity.systemPrompt,
                messageLimit = topicEntity.messageLimit,
                isCompleted = isCompleted,
                topicXp = topicEntity.topicXp
            )
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun addTopic(createTopicDto: CreateTopicDTO): TopicEntity {
        val topic = TopicEntity().apply {
            name = createTopicDto.name
            image = createTopicDto.image
            price = createTopicDto.price
            level = createTopicDto.level
            welcomePrompt = createTopicDto.welcomePrompt
            systemPrompt = createTopicDto.systemPrompt
            messageLimit = createTopicDto.messageLimit
            topicXp = createTopicDto.topicXp // Устанавливаем topicXp
        }
        return topicRepository.save(topic)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun addTopics(createTopicDTOs: List<CreateTopicDTO>): List<TopicEntity> {
        val topics = createTopicDTOs.map { dto ->
            TopicEntity().apply {
                name = dto.name
                image = dto.image
                price = dto.price
                level = dto.level
                welcomePrompt = dto.welcomePrompt
                systemPrompt = dto.systemPrompt
                messageLimit = dto.messageLimit
                topicXp = dto.topicXp // Устанавливаем topicXp
            }
        }
        return topicRepository.saveAll(topics)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun updateTopic(id: Long, updateTopicDto: UpdateTopicDTO): TopicEntity {
        val topic = topicRepository.findByIdOrNull(id) ?: throw IllegalArgumentException("Топик с id $id не найден")

        updateTopicDto.name?.let { topic.name = it }
        updateTopicDto.image?.let { topic.image = it }
        updateTopicDto.price?.let { topic.price = it }
        updateTopicDto.welcomePrompt?.let { topic.welcomePrompt = it }
        updateTopicDto.systemPrompt?.let { topic.systemPrompt = it }
        updateTopicDto.messageLimit?.let { topic.messageLimit = it }
        updateTopicDto.topicXp?.let { topic.topicXp = it } // Обновляем topicXp

        return topicRepository.save(topic)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun deleteTopicByName(name: String) {
        topicRepository.deleteTopicByName(name)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun deleteTopicById(id: Long) {
        if (!topicRepository.existsById(id)) {
            throw IllegalArgumentException("Топик с id $id не найден")
        }
        topicRepository.deleteById(id)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    override fun deleteAllTopic() {
        topicRepository.deleteAll()
    }
}