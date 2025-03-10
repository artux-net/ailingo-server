package net.artux.ailingo.server.service.impl

import net.artux.ailingo.server.dto.CreateTopicDTO
import net.artux.ailingo.server.dto.TopicResponseDTO
import net.artux.ailingo.server.dto.UpdateTopicDTO
import net.artux.ailingo.server.entity.TopicEntity
import net.artux.ailingo.server.repository.TopicRepository
import net.artux.ailingo.server.service.TopicService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class TopicServiceImpl(
    val topicRepository: TopicRepository
) : TopicService {
    override fun getTopics(): List<TopicResponseDTO> {
        return topicRepository.findAll().map {
            TopicResponseDTO(
                id = it.id,
                name = it.name,
                imageUrl = it.image,
                price = it.price,
                welcomePrompt = it.welcomePrompt,
                systemPrompt = it.systemPrompt,
                messageLimit = it.messageLimit
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
}
