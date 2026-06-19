package com.example.roadevadergame

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.*
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import com.google.android.gms.location.LocationServices
import android.location.Location
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val ROWS = 7
    private val COLS = 5
    private lateinit var obstacleMatrix: Array<Array<ImageView>>
    private lateinit var playerCarsArray: Array<ImageView>

    private lateinit var isCoinMatrix: Array<Array<Boolean>>

    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView

    private lateinit var odometerText: TextView
    private var distance = 0
    private var coinsCollected = 0

    private var lives = 3
    private var carLane = 2
    private var gameOver = false
    private val handler = Handler(Looper.getMainLooper())

    private var controlMode = "buttons"

    private var speedDelay: Long = 500

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastSensorUpdateTime: Long = 0

    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient

    private val gameLoop = object : Runnable {
        override fun run() {
            if (!gameOver) {
                moveObstacles()
                checkCollision()

                distance += 10
                updateOdometerText()

                handler.postDelayed(this, speedDelay)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
        }
        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)

        if (androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        controlMode = intent.getStringExtra("CONTROL_MODE") ?: "buttons"

        speedDelay = intent.getLongExtra("SPEED_DELAY", 500)

        playerCarsArray = arrayOf(
            findViewById(R.id.car_lane_0),
            findViewById(R.id.car_lane_1),
            findViewById(R.id.playerCar),
            findViewById(R.id.car_lane_3),
            findViewById(R.id.car_lane_4)
        )

        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)
        odometerText = findViewById(R.id.odometerText)

        val leftBtn = findViewById<Button>(R.id.leftBtn)
        val rightBtn = findViewById<Button>(R.id.rightBtn)

        if (controlMode == "sensors") {
            leftBtn.visibility = View.GONE
            rightBtn.visibility = View.GONE
        } else {
            leftBtn.visibility = View.VISIBLE
            rightBtn.visibility = View.VISIBLE
        }

        obstacleMatrix = Array(ROWS) { row ->
            Array(COLS) { col -> findViewById(resources.getIdentifier("obs_${row}_${col}", "id", packageName)) }
        }
        isCoinMatrix = Array(ROWS) { Array(COLS) { false } }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        leftBtn.setOnClickListener {
            if (controlMode == "buttons" && carLane > 0) { carLane--; updateCarPosition() }
        }
        rightBtn.setOnClickListener {
            if (controlMode == "buttons" && carLane < COLS - 1) { carLane++; updateCarPosition() }
        }
        findViewById<Button>(R.id.restartBtn).setOnClickListener { restartGame() }

        updateCarPosition()
        updateOdometerText()
        handler.post(gameLoop)
    }

    override fun onResume() {
        super.onResume()
        if (controlMode == "sensors") {
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (gameOver || event == null || controlMode != "sensors") return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val xValue = event.values[0]
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastSensorUpdateTime > 300) {

                if (xValue > 4.0) {
                    if (carLane > 0) {
                        carLane--
                        updateCarPosition()
                        lastSensorUpdateTime = currentTime
                    }
                } else if (xValue < -4.0) {
                    if (carLane < COLS - 1) {
                        carLane++
                        updateCarPosition()
                        lastSensorUpdateTime = currentTime
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateOdometerText() {
        odometerText.text = "Distance: $distance m | Coins: $coinsCollected"
    }

    private fun updateHearts() {
        heart1.visibility = if (lives >= 1) View.VISIBLE else View.INVISIBLE
        heart2.visibility = if (lives >= 2) View.VISIBLE else View.INVISIBLE
        heart3.visibility = if (lives >= 3) View.VISIBLE else View.INVISIBLE
    }

    private fun updateCarPosition() {
        for (i in playerCarsArray.indices) {
            playerCarsArray[i].visibility = if (carLane == i) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun moveObstacles() {
        for (i in ROWS - 1 downTo 1) {
            for (j in 0 until COLS) {
                obstacleMatrix[i][j].visibility = obstacleMatrix[i - 1][j].visibility
                isCoinMatrix[i][j] = isCoinMatrix[i - 1][j]

                if (isCoinMatrix[i][j]) {
                    obstacleMatrix[i][j].setImageResource(R.drawable.coin)
                } else {
                    obstacleMatrix[i][j].setImageResource(R.drawable.mihshol)
                }
            }
        }

        for (j in 0 until COLS) {
            obstacleMatrix[0][j].visibility = View.INVISIBLE
            isCoinMatrix[0][j] = false
        }

        var exists = false
        for (i in 0 until ROWS) {
            for (j in 0 until COLS) {
                if (obstacleMatrix[i][j].visibility == View.VISIBLE) exists = true
            }
        }

        if (!exists) {
            val randomCol = Random.nextInt(COLS)
            val isCoin = Random.nextInt(100) < 30

            isCoinMatrix[0][randomCol] = isCoin
            if (isCoin) {
                obstacleMatrix[0][randomCol].setImageResource(R.drawable.coin)
            } else {
                obstacleMatrix[0][randomCol].setImageResource(R.drawable.mihshol)
            }
            obstacleMatrix[0][randomCol].visibility = View.VISIBLE
        }
    }

    private fun checkCollision() {
        if (obstacleMatrix[ROWS - 1][carLane].visibility == View.VISIBLE) {

            if (isCoinMatrix[ROWS - 1][carLane]) {
                coinsCollected++
                updateOdometerText()
                Toast.makeText(this, "COIN COLLECTED!", Toast.LENGTH_SHORT).show()
            } else {
                vibrate()
                playCrashSound()
                Toast.makeText(this, "U GOT HIT", Toast.LENGTH_SHORT).show()
                lives--
                updateHearts()

                if (lives <= 0) {
                    gameOver = true
                    saveScoreWithLocation()
                    findViewById<Button>(R.id.restartBtn).visibility = View.VISIBLE
                }
            }

            obstacleMatrix[ROWS - 1][carLane].visibility = View.INVISIBLE
            isCoinMatrix[ROWS - 1][carLane] = false
        }
    }
    private fun saveScoreWithLocation() {

        val randomLat = 31.95 + (0..50).random() / 1000.0
        val randomLng = 34.75 + (0..50).random() / 1000.0

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    // real data
                    DataManager(this).addScore(distance, task.result.latitude, task.result.longitude)
                } else {
                    // random data
                    DataManager(this).addScore(distance, randomLat, randomLng)
                }
            }
        } else {
            // else (settings problem)
            DataManager(this).addScore(distance, randomLat, randomLng)
        }
    }

    private fun playCrashSound() {
        try {
            val mediaPlayer = MediaPlayer.create(this, R.raw.crash_sound)
            mediaPlayer.setOnCompletionListener { mp ->
                mp.release()
            }
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(300)
        }
    }

    private fun restartGame() {
        lives = 3
        carLane = 2
        distance = 0
        coinsCollected = 0

        updateHearts()
        updateCarPosition()
        updateOdometerText()
        gameOver = false
        findViewById<Button>(R.id.restartBtn).visibility = View.GONE

        for (i in 0 until ROWS) {
            for (j in 0 until COLS) {
                obstacleMatrix[i][j].visibility = View.INVISIBLE
                isCoinMatrix[i][j] = false
            }
        }
        handler.postDelayed(gameLoop, speedDelay)
    }
}