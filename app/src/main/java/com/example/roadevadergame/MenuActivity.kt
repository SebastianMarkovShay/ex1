package com.example.roadevadergame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


        val btnButtonsSlow = findViewById<Button>(R.id.btnButtonsSlow)
        val btnButtonsFast = findViewById<Button>(R.id.btnButtonsFast)
        val btnSensorsMode = findViewById<Button>(R.id.btnSensorsMode)
        val btnViewScores = findViewById<Button>(R.id.btnViewScores)


        // slow
        btnButtonsSlow.setOnClickListener {
            startGame("buttons", 600)
        }

        // fast
        btnButtonsFast.setOnClickListener {
            startGame("buttons", 300)
        }

        //sensor
        btnSensorsMode.setOnClickListener {
            startGame("sensors", 500)
        }
        btnViewScores.setOnClickListener {
            val intent = Intent(this, ScoresActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startGame(mode: String, speedDelay: Long) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("CONTROL_MODE", mode)
        intent.putExtra("SPEED_DELAY", speedDelay)
        startActivity(intent)
    }
}