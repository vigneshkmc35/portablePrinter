package com.pos.portablebilling.util

import android.content.Context
import org.json.JSONObject

class LanguageManager(private val context: Context) {
    private var currentLanguageJson = JSONObject()

    fun loadLanguage(langCode: String) {
        try {
            val inputStream = context.assets.open("languages/$langCode.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            currentLanguageJson = JSONObject(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getString(key: String): String {
        return try {
            currentLanguageJson.getString(key)
        } catch (e: Exception) {
            key // Return key as fallback
        }
    }
}

data class AppLanguage(
    val name: String,
    val code: String
)

val availableLanguages = listOf(
    AppLanguage("English", "en"),
    AppLanguage("தமிழ் (Tamil)", "ta"),
    AppLanguage("മലയാളം (Malayalam)", "ml"),
    AppLanguage("ಕನ್ನಡ (Kannada)", "kn"),
    AppLanguage("తెలుగు (Telugu)", "te"),
    AppLanguage("हिन्दी (Hindi)", "hi")
)
