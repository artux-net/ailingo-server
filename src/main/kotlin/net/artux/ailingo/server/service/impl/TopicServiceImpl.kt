package net.artux.ailingo.server.service.impl

import net.artux.ailingo.server.entity.TopicEntity
import net.artux.ailingo.server.model.TopicResponseDTO
import net.artux.ailingo.server.repositories.TopicRepository
import net.artux.ailingo.server.service.TopicService
import org.springframework.stereotype.Service

@Service
class TopicServiceImpl(
    private val topicRepository: TopicRepository
) : TopicService {

    override fun getTopics(locale: String): List<TopicResponseDTO> {
        return topicRepository.findAll().map { topic ->
            TopicResponseDTO(
                name = topic.name ?: topic.name ?: "Unnamed Topic",
                imageUrl = "${topic.image}${topic.image}"
            )
        }
    }

    override fun addTopic(topic: TopicEntity): TopicEntity {
        return topicRepository.save(topic)
    }

    override fun deleteTopicByName(name: String) {
        topicRepository.deleteTopicByName(name)
    }

    override fun deleteTopicById(id: Long) {
        topicRepository.deleteTopicById(id)
    }
}