package net.artux.ailingo.server.service.impl

import net.artux.ailingo.server.entity.TopicEntity
import net.artux.ailingo.server.dto.CreateTopicDTO
import net.artux.ailingo.server.dto.TopicResponseDTO
import net.artux.ailingo.server.dto.UpdateTopicDTO
import net.artux.ailingo.server.repository.TopicRepository
import net.artux.ailingo.server.service.TopicService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException

@Service
@Transactional
class TopicServiceImpl(
    private val topicRepository: TopicRepository
) : TopicService {

    override fun getTopics(locale: String): List<TopicResponseDTO> {
        return topicRepository.findAll().map { topic ->
            TopicResponseDTO(
                id = topic.id,
                name = topic.name,
                imageUrl = topic.image,
                price = topic.price,
            )
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun addTopic(createTopicDto: CreateTopicDTO): TopicEntity {
        val topic = TopicEntity().apply {
            name = createTopicDto.name
            image = createTopicDto.imageUrl
            price = createTopicDto.price
        }
        return topicRepository.save(topic)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun addTopics(createTopicDTOs: List<CreateTopicDTO>): List<TopicEntity> {
        val topics = createTopicDTOs.map { dto ->
            TopicEntity().apply {
                name = dto.name
                image = dto.imageUrl
                price = dto.price
            }
        }
        return topicRepository.saveAll(topics)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateTopic(id: Long, updateTopicDto: UpdateTopicDTO): TopicEntity {
        val topic = topicRepository.findByIdOrNull(id) ?: throw IllegalArgumentException("Топик с id $id не найден")

        updateTopicDto.name?.let { topic.name = it }
        updateTopicDto.imageUrl?.let { topic.image = it }
        updateTopicDto.price?.let { topic.price = it }

        return topicRepository.save(topic)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun deleteTopicByName(name: String) {
        val topicToDelete = topicRepository.findByName(name) ?: throw IllegalArgumentException("Топик с именем $name не найден")
        topicRepository.delete(topicToDelete)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun deleteTopicById(id: Long) {
        if (!topicRepository.existsById(id)) {
            throw IllegalArgumentException("Топик с id $id не найден")
        }
        topicRepository.deleteById(id)
    }
}