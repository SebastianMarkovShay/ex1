package com.example.roadevadergame
import com.example.roadevadergame.MapFragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ScoresActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)
    }

    fun updateMapFromList(lat: Double, lng: Double, score: Int) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.container_map) as? MapFragment
        mapFragment?.updateMapMarker(lat, lng, score)
    }
}