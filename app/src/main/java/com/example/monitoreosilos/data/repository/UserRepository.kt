package com.example.monitoreosilos.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

data class AppUser(val name: String, val email: String)

object UserRepository {

    private lateinit var prefs: SharedPreferences

    private val _currentUser = MutableStateFlow<AppUser?>(null)
    val currentUser: StateFlow<AppUser?> = _currentUser.asStateFlow()

    /** Se llama una sola vez desde MainActivity. Recupera la sesión si había una. */
    fun init(context: Context) {
        if (::prefs.isInitialized) return
        prefs = context.applicationContext.getSharedPreferences("usuarios", Context.MODE_PRIVATE)
        val sessionEmail = prefs.getString("session_email", null)
        if (sessionEmail != null) {
            _currentUser.value = findUser(sessionEmail)?.let { AppUser(it.getString("name"), it.getString("email")) }
        }
    }

    fun emailExists(email: String): Boolean = findUser(email) != null

    fun register(name: String, email: String, password: String) {
        val users = loadUsers()
        users.put(JSONObject().apply {
            put("name", name.trim())
            put("email", email.trim().lowercase())
            put("password", hash(password))
        })
        prefs.edit().putString("users", users.toString()).apply()
    }

    /** Devuelve true si el correo y la contraseña coinciden con una cuenta registrada. */
    fun login(email: String, password: String): Boolean {
        val user = findUser(email) ?: return false
        if (user.getString("password") != hash(password)) return false
        _currentUser.value = AppUser(user.getString("name"), user.getString("email"))
        prefs.edit().putString("session_email", user.getString("email")).apply()
        return true
    }

    fun logout() {
        _currentUser.value = null
        prefs.edit().remove("session_email").apply()
    }

    private fun loadUsers(): JSONArray =
        try { JSONArray(prefs.getString("users", "[]")) } catch (e: Exception) { JSONArray() }

    private fun findUser(email: String): JSONObject? {
        val users = loadUsers()
        val wanted = email.trim().lowercase()
        for (i in 0 until users.length()) {
            val u = users.getJSONObject(i)
            if (u.getString("email") == wanted) return u
        }
        return null
    }

    private fun hash(text: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }
}