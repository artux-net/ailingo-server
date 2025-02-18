package net.artux.ailingo.server.service

interface WordsService {
    fun addWordToFavorites(word: String)
    fun removeWordFromFavorites(word: String)
    fun getFavoriteWords(): List<String>
}