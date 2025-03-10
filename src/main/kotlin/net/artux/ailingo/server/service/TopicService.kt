package net.artux.ailingo.server.service;

import net.artux.ailingo.server.dto.CreateTopicDTO;
import net.artux.ailingo.server.dto.TopicResponseDTO;
import net.artux.ailingo.server.dto.UpdateTopicDTO;
import net.artux.ailingo.server.entity.TopicEntity;

import java.util.List;

public interface TopicService {
    List<TopicResponseDTO> getTopics(String locale);

    TopicEntity addTopic(CreateTopicDTO createTopicDto);

    List<TopicEntity> addTopics(List<CreateTopicDTO> createTopicDTO);

    TopicEntity updateTopic(Long id, UpdateTopicDTO updateTopicDto);

    void deleteTopicByName(String name);

    void deleteTopicById(Long id);
}