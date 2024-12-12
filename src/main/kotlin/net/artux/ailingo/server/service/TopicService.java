package net.artux.ailingo.server.service;

import net.artux.ailingo.server.entity.TopicEntity;
import net.artux.ailingo.server.model.CreateTopicDTO;
import net.artux.ailingo.server.model.TopicResponseDTO;
import net.artux.ailingo.server.model.UpdateTopicDTO;

import java.util.List;

public interface TopicService {

    List<TopicResponseDTO> getTopics(String locale);

    TopicEntity addTopic(CreateTopicDTO createTopicDto);

    List<TopicEntity> addTopics(List<CreateTopicDTO> createTopicDTO);

    TopicEntity updateTopic(Long id, UpdateTopicDTO updateTopicDto);

    void deleteTopicByName(String name);

    void deleteTopicById(Long id);
}