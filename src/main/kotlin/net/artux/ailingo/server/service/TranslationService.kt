package net.artux.ailingo.server.service

interface TranslationService {
    fun translate(text: String, langpair: String): String?
}