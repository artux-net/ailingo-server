package org.ailingo.server.service.dictionary;

import org.ailingo.server.entity.dictionary.Word;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WordRepository extends CrudRepository<Word, Long> {
    List<Word> findByUserId(UUID user_id);
}
