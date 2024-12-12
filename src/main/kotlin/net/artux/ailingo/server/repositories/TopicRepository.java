package net.artux.ailingo.server.repositories;

import net.artux.ailingo.server.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM topic t WHERE t.name = :name", nativeQuery = true)
    void deleteTopicByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM topic t WHERE t.id = :id", nativeQuery = true)
    void deleteTopicById(@Param("id") Long id);
}