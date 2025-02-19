package net.artux.ailingo.server.service.impl

import net.artux.ailingo.server.repository.UserRepository
import net.artux.ailingo.server.service.WordsService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class WordsServiceImpl(
    private val userRepository: UserRepository
) : WordsService {

    private fun getCurrentUser() = userRepository.findByLogin(SecurityContextHolder.getContext().authentication.name)
        .orElseThrow { IllegalStateException("Пользователь не найден") }

    override fun addWordToFavorites(word: String) {
        val currentUser = getCurrentUser()
        if (currentUser.favoriteWords.contains(word)) {
            throw IllegalArgumentException("Слово уже в избранном")
        }
        currentUser.favoriteWords.add(word)
        userRepository.save(currentUser)
    }

    override fun removeWordFromFavorites(word: String) {
        val currentUser = getCurrentUser()
        if (!currentUser.favoriteWords.contains(word)) {
            throw IllegalArgumentException("Слово не найдено в избранном")
        }
        currentUser.favoriteWords.remove(word)
        userRepository.save(currentUser)
    }

    override fun getFavoriteWords(): List<String> {
        return getCurrentUser().favoriteWords
    }
}
