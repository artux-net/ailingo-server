package net.artux.ailingo.server.service

import net.artux.ailingo.server.dto.CreateTopicDTO
import net.artux.ailingo.server.dto.TopicResponseDTO
import net.artux.ailingo.server.dto.UpdateTopicDTO
import net.artux.ailingo.server.entity.TopicEntity

interface TopicService {
    fun getTopics(): List<TopicResponseDTO>

    fun addTopic(createTopicDto: CreateTopicDTO): TopicEntity

    fun addTopics(createTopicDTOs: List<CreateTopicDTO>): List<TopicEntity>

    fun updateTopic(id: Long, updateTopicDto: UpdateTopicDTO): TopicEntity

    fun deleteTopicByName(name: String)

    fun deleteTopicById(id: Long)
}