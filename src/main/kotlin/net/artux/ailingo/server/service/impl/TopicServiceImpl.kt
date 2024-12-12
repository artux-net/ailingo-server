package net.artux.ailingo.server.service.impl

import net.artux.ailingo.server.entity.TopicEntity
import net.artux.ailingo.server.model.CreateTopicDTO
import net.artux.ailingo.server.model.TopicResponseDTO
import net.artux.ailingo.server.model.UpdateTopicDTO
import net.artux.ailingo.server.repositories.TopicRepository
import net.artux.ailingo.server.service.TopicService
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class TopicServiceImpl(
    private val topicRepository: TopicRepository
) : TopicService {

    val logger = LoggerFactory.getLogger(TopicServiceImpl::class.java)

    override fun getTopics(locale: String): List<TopicResponseDTO> {
        return topicRepository.findAll().map { topic ->
            TopicResponseDTO(
                id = topic.id,
                name = topic.name,
                imageUrl = topic.image,
                price = topic.price
            )
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun addTopic(createTopicDto: CreateTopicDTO): TopicEntity {
        try {
            val topic = TopicEntity().apply {
                name = createTopicDto.name
                image = createTopicDto.imageUrl
                price = createTopicDto.price
            }
            return topicRepository.save(topic)
        } catch (e: Exception) {
            logger.error("Error adding topic: ", e);
            throw e;
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun addTopics(createTopicDTOs: List<CreateTopicDTO>): List<TopicEntity> {
        try {
            val topics = createTopicDTOs.map { dto ->
                TopicEntity().apply {
                    name = dto.name
                    image = dto.imageUrl
                    price = dto.price
                }
            }
            return topicRepository.saveAll(topics)
        } catch (e: Exception) {
            logger.error("Error adding topics: ", e)
            throw e
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateTopic(id: Long, updateTopicDto: UpdateTopicDTO): TopicEntity {
        val topic = topicRepository.findById(id).orElseThrow {
            IllegalArgumentException("Topic with id $id not found")
        }

        updateTopicDto.name?.let { topic.name = it }
        updateTopicDto.imageUrl?.let { topic.image = it }
        updateTopicDto.price?.let { topic.price = it }

        return topicRepository.save(topic)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun deleteTopicByName(name: String) {
        topicRepository.deleteTopicByName(name)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun deleteTopicById(id: Long) {
        topicRepository.deleteTopicById(id)
    }
}