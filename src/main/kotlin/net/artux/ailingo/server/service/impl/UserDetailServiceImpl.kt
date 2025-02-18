package net.artux.ailingo.server.service.impl

import lombok.RequiredArgsConstructor
import net.artux.ailingo.server.entity.UserEntity
import net.artux.ailingo.server.jwt.model.SecurityUser
import net.artux.ailingo.server.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
@RequiredArgsConstructor
class UserDetailServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        if (username.isBlank())
            throw UsernameNotFoundException("Access denied.")

        val userOptional: Optional<UserEntity> =
            if (username.contains("@")) {
                userRepository.getByEmail(username)
            } else
                userRepository.getByLogin(username)

        if (userOptional.isPresent) {
            val simpleUser = userOptional.get()
            if (simpleUser.lastLoginAt == null || simpleUser.lastLoginAt!!.plusSeconds(5 * 60).isBefore(Instant.now())
            ) {
                simpleUser.lastLoginAt = Instant.now()
                userRepository.save(simpleUser)
            }
            val userDetails = User.builder()
                .username(simpleUser.login)
                .password(simpleUser.password)
                .authorities(simpleUser.authorities)
                .build()
            return SecurityUser(simpleUser.id, userDetails)
        } else {
            throw UsernameNotFoundException("User not found")
        }
    }
}