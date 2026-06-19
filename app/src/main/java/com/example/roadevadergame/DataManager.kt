package com.example.roadevadergame

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


data class ScoreEntry(
    val score: Int,
    val date: String,
    val latitude: Double,
    val longitude: Double
)


class DataManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("GAME_PREFS", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getHighScores(): MutableList<ScoreEntry> {
        val json = sharedPreferences.getString("HIGH_SCORES", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<ScoreEntry>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addScore(score: Int, lat: Double, lng: Double) {
        val scores = getHighScores()

        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        val currentDate = sdf.format(java.util.Date())

        scores.add(ScoreEntry(score, currentDate, lat, lng))

        scores.sortByDescending { it.score }
        val topTen = if (scores.size > 10) scores.subList(0, 10) else scores

        val json = gson.toJson(topTen)
        sharedPreferences.edit().putString("HIGH_SCORES", json).apply()
    }
}