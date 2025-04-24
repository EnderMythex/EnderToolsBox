package com.example.endertoolsbox.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.endertoolsbox.models.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Properties
import java.util.concurrent.TimeUnit

class GeminiChatViewModel(application: Application) : AndroidViewModel(application) {
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Charger la clé API depuis le fichier de propriétés
    private val apiKey: String = loadApiKey(application.applicationContext)
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Fonction pour charger la clé API depuis le fichier de propriétés
    private fun loadApiKey(context: Context): String {
        try {
            val properties = Properties()
            context.assets.open("apikeys.properties").use {
                properties.load(it)
            }
            return properties.getProperty("GEMINI_API_KEY", "")
        } catch (e: Exception) {
            Log.e("GeminiChatViewModel", "Erreur lors du chargement de la clé API: ${e.message}")
            return ""
        }
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        val userMessage = ChatMessage(content = message, isUser = true)
        _chatMessages.value = _chatMessages.value + userMessage

        _isLoading.value = true

        // Lancer la requête API
        viewModelScope.launch {
            try {
                val response = sendMessageToGemini(message)
                val aiMessage = ChatMessage(content = response, isUser = false)
                _chatMessages.value = _chatMessages.value + aiMessage
            } catch (e: Exception) {
                Log.e("GeminiChat", "Error sending message to Gemini: ${e.message}")
                val errorMessage = ChatMessage(
                    content = "Désolé, je n'ai pas pu obtenir de réponse de Gemini. Erreur: ${e.message}",
                    isUser = false
                )
                _chatMessages.value = _chatMessages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun sendMessageToGemini(message: String): String = withContext(Dispatchers.IO) {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", message)
                        })
                    })
                })
            })
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(endpoint)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("API request failed with code: ${response.code}. Body: ${response.body?.string()}")
            }

            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            Log.d("GeminiChat", "Response: $responseBody")
            val jsonResponse = JSONObject(responseBody)

            try {
                val candidates = jsonResponse.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).getString("text")
                    }
                }
                throw Exception("Could not parse response from Gemini")
            } catch (e: Exception) {
                Log.e("GeminiChat", "Error parsing response: ${e.message}")
                throw Exception("Could not parse response from Gemini: ${e.message}")
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }
}