package com.volum.volumematching.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.volum.volumematching.databinding.ActivityLevel2Binding
import com.volum.volumematching.ArcadeResultActivity
import com.volum.volumematching.ManiaResultActivity
import com.volum.volumematching.R
import com.volum.volumematching.viewModels.GameModes
import com.volum.volumematching.viewModels.GameSize
import com.volum.volumematching.viewModels.GameViewModel
import java.lang.Integer.parseInt
import kotlin.random.Random


class Level2Activity: AppCompatActivity() {

    private lateinit var binding: ActivityLevel2Binding
    private val imgButtons by lazy { listOf(
            binding.imgButton2A,
            binding.imgButton2B,
            binding.imgButton2C,
            binding.imgButton2D,
            binding.imgButton2E,
            binding.imgButton2F,
            binding.imgButton2G,
            binding.imgButton2H,
        )
    }

    private val txtCountdown by lazy { binding.txtCountDown }

    private lateinit var timeRemaining: String
    private lateinit var timer:CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityLevel2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val gameModel by viewModels<GameViewModel>()

        val gvmMode = intent.getIntExtra("GVM-mode", 0)
        val gvmModeUnit = when(gvmMode) {
            1 -> GameModes.MODE_ARCADE
            2 -> GameModes.MODE_MANIA
            else -> GameModes.MODE_NONE
        }

        val gvmSize = intent.getIntExtra("GVM-size", 0)
        val gvmSizeUnit = when(gvmSize) {
            1 -> GameSize.SIZE_1
            2 -> GameSize.SIZE_2
            3 -> GameSize.SIZE_3
            else -> GameSize.SIZE_0
        }

        val userScore = intent.getIntExtra("GVM-User-Score", 0)
        val userTimeLeft = intent.getIntExtra("GVM-User-Time", 31000)

        gameModel.setGameMode(gvmModeUnit)
        gameModel.setGameSize(gvmSizeUnit)
        gameModel.setUserData(userScore, userTimeLeft)

        timer = object: CountDownTimer(gameModel.timeLeft.value!!.toLong(),1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = millisUntilFinished / 1000
                timeRemaining = sec.toInt().toString()
                txtCountdown.text = timeRemaining
                intent.putExtra("GVM-User-Time", millisUntilFinished.toInt())
            }
            override fun onFinish() {
                maniaResults(gameModel)
            }
        }

        if(gvmModeUnit != GameModes.MODE_MANIA) txtCountdown.visibility = View.GONE
        if(gameModel.inGame.value == false) gameModel.startGame()
        if(gvmModeUnit == GameModes.MODE_MANIA && gameModel.inGame.value == true) timer.start()

        for(i in imgButtons.indices) { imgButtons[i].setOnClickListener { gameModel.checkOrSelect(i) } }

        gameModel.gameRoundImageStatus.observe(this) { refreshGrid() }
        gameModel.isDone.observe(this) {
            if(it == true) {
                if (gvmModeUnit == GameModes.MODE_ARCADE) {
                    val intent = Intent(this, ArcadeResultActivity::class.java)
                    intent.putExtra("GVM-mode", gvmMode)
                    intent.putExtra("GVM-size", gvmSize)
                    startActivityForResult(intent, 1)
                    finish()
                }

                if (gvmModeUnit == GameModes.MODE_MANIA) {
                    timer.cancel()

                    val level = Random.nextInt(1, 4)
                    val levelClass = when(level) {
                        1 -> Level1Activity::class.java
                        2 -> Level2Activity::class.java
                        3 -> Level3Activity::class.java
                        else -> null
                    }

                    val bonusTime = when(gameModel.gameSize.value) {
                        GameSize.SIZE_1 -> 5
                        GameSize.SIZE_2 -> 8
                        GameSize.SIZE_3 -> 12
                        else -> 0
                    }

                    val milliRemaining = (parseInt(timeRemaining) * 1000) + (bonusTime * 1000)

                    Toast.makeText(this, "+$bonusTime seconds", Toast.LENGTH_SHORT).show()
                    gameModel.setUserData(userScore + 1, milliRemaining)

                    val intent = Intent(this, levelClass)
                    intent.putExtra("GVM-mode", gvmMode)
                    intent.putExtra("GVM-size", level)
                    intent.putExtra("GVM-User-Score", gameModel.score.value)
                    intent.putExtra("GVM-User-Time", gameModel.timeLeft.value)
                    startActivityForResult(intent, 1)
                    finish()
                }
            }
        }
    }


    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    private fun refreshGrid() {
        val gameModel by viewModels<GameViewModel>()
        if (gameModel.gameRoundImages.value == null || gameModel.gameRoundImageStatus.value == null) return
        val gameRoundImages = gameModel.gameRoundImages.value!!
        val gameRoundImageStatus = gameModel.gameRoundImageStatus.value!!

        for ( i in imgButtons.indices) {
            if(gameRoundImageStatus[i]) imgButtons[i].setBackgroundResource(gameRoundImages[i])
            else imgButtons[i].setBackgroundResource(R.drawable.ic_music00)
        }

    }

    private fun maniaResults(gameModel: GameViewModel) {
        val intent = Intent(this, ManiaResultActivity::class.java)
        intent.putExtra("GVM-User-Score", gameModel.score.value)
        gameModel.stopGame()
        startActivityForResult(intent, 1)
        finish()
    }
}