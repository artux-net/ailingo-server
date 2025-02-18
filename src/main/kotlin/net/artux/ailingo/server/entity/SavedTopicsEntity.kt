package net.artux.ailingo.server.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "saved_topics")
class SavedTopicsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserEntity? = null

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_saved_topics",
        joinColumns = [JoinColumn(name = "saved_topics_id")],
        inverseJoinColumns = [JoinColumn(name = "topic_id")]
    )
    var savedTopics: MutableList<TopicEntity> = mutableListOf()
}