package net.artux.ailingo.server.service

import net.artux.ailingo.server.entity.TopicEntity

interface SavedTopicService {
    fun getUserSavedTopics(): List<TopicEntity>
    fun saveUserTopics(topics: List<TopicEntity>)
    fun removeUserTopic(topic: TopicEntity)
}
