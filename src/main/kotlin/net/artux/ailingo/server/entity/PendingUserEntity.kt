package net.artux.ailingo.server.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "pending_users")
class PendingUserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val login: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val verificationCode: String,

    @Column(nullable = false)
    val registrationTime: LocalDateTime = LocalDateTime.now()
)