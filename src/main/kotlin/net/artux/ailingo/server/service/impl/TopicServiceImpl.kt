package net.artux.ailingo.server.service.impl

import net.artux.ailingo.server.entity.TopicEntity
import net.artux.ailingo.server.model.TopicResponseDTO
import net.artux.ailingo.server.model.CreateTopicDTO
import net.artux.ailingo.server.model.UpdateTopicDTO
import net.artux.ailingo.server.repositories.TopicRepository
import net.artux.ailingo.server.service.TopicService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class TopicServiceImpl(
    private val topicRepository: TopicRepository
) : TopicService {

    override fun getTopics(locale: String): List<TopicResponseDTO> {
        return topicRepository.findAll().map { topic ->
            TopicResponseDTO(
                name = topic.name ?: "Unnamed Topic",
                imageUrl = topic.image
            )
        }
    }

    override fun addTopic(createTopicDto: CreateTopicDTO): TopicEntity {
        val topic = TopicEntity().apply {
            name = createTopicDto.name
            image = createTopicDto.imageUrl
        }
        return topicRepository.save(topic)
    }

    override fun updateTopic(id: Long, updateTopicDto: UpdateTopicDTO): TopicEntity {
        val topic = topicRepository.findById(id).orElseThrow {
            IllegalArgumentException("Topic with id $id not found")
        }

        updateTopicDto.name?.let { topic.name = it }
        updateTopicDto.imageUrl?.let { topic.image = it }

        return topicRepository.save(topic)
    }

    override fun deleteTopicByName(name: String) {
        topicRepository.deleteTopicByName(name)
    }

    @PreAuthorize("hasAuthority('SCOPE_delete_topic')")
    override fun deleteTopicById(id: Long) {
        topicRepository.deleteTopicById(id)
    }
}
