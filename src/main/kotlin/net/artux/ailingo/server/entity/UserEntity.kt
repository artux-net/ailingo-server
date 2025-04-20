package net.artux.ailingo.server.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import net.artux.ailingo.server.model.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant
import java.util.UUID

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "app_user")
class UserEntity : UserDetails {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    @Column(nullable = false)
    var name: String

    @Column(unique = true)
    var login: String

    @Column(unique = true)
    var email: String

    var userPassword: String

    var avatar: String? = null

    var xp: Int = 0
    var streak: Int = 0
    var lastStreakAt: Instant? = null
    var coins: Int = 0

    var registration: Instant? = null

    var lastLoginAt: Instant? = null

    @Enumerated(EnumType.STRING)
    var role: Role

    @Column
    var verificationCode: String? = null

    var isEmailVerified: Boolean = false

    private var accountNonExpired: Boolean = true
    private var accountNonLocked: Boolean = true
    private var credentialsNonExpired: Boolean = true
    private var enabled: Boolean = true

    constructor(id: UUID?, login: String, email: String, password: String, name: String, avatar: String?) {
        this.id = id
        this.login = login
        this.email = email
        this.userPassword = password
        this.name = name
        this.avatar = avatar
        this.xp = 0
        this.streak = 0
        this.lastStreakAt = null
        this.coins = 500
        this.role = Role.USER
        this.lastLoginAt = Instant.now()
        this.accountNonExpired = true
        this.accountNonLocked = true
        this.credentialsNonExpired = true
        this.enabled = true
    }

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var savedTopics: MutableSet<SavedTopicsEntity> = HashSet()

    @ElementCollection
    @Column
    var favoriteWords: MutableList<String> = ArrayList()

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String {
        return userPassword
    }

    fun changeCoins(amount: Int) {
        this.coins += amount
    }

    override fun getUsername(): String {
        return login
    }

    override fun isAccountNonExpired(): Boolean {
        return accountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return accountNonLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return credentialsNonExpired
    }

    override fun isEnabled(): Boolean {
        return enabled
    }
}