package com.example.mobilevizinhaa.ui.theme.sessionmanager

import com.example.mobilevizinhaa.ui.theme.data.ResidentResponse
import kotlin.jvm.java
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Salva o objeto ResidentResponse inteiro como String (JSON)
    fun saveUser(user: ResidentResponse) {
        val json = gson.toJson(user)
        prefs.edit().putString("user_data", json).apply()
    }

    // Recupera o objeto do disco
    fun getUser(): ResidentResponse? {
        val json = prefs.getString("user_data", null)
        return if (json != null) gson.fromJson(json, ResidentResponse::class.java) else null
    }

    fun clear() = prefs.edit().clear().apply()
}