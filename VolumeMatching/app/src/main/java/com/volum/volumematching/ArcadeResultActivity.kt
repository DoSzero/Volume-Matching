package com.volum.volumematching

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.volum.volumematching.databinding.ActivityArcadeResultBinding
import com.volum.volumematching.view.Level1Activity
import com.volum.volumematching.view.Level2Activity
import com.volum.volumematching.view.Level3Activity
import com.volum.volumematching.view.MenuActivity
import kotlin.random.Random

class ArcadeResultActivity : AppCompatActivity() {

    private lateinit var binding:ActivityArcadeResultBinding
    private val phrases = listOf("Ты все сделал отлично!", "Отличная работа!", "Отличная работа!")

    private val btnPlayAgain by lazy {
        binding.btnPlayAgain
    }

    private val btnMenu by lazy {
        binding.btnMenu
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArcadeResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phrase = phrases[Random.nextInt(0, 2)]
        binding.txtMessage.text = phrase

        val gvmMode = intent.getIntExtra("GVM-mode", 0)
        val gvmSize = intent.getIntExtra("GVM-size", 0)
        val gvmLevel = when(gvmSize) {
            1 -> Level1Activity::class.java
            2 -> Level2Activity::class.java
            3 -> Level3Activity::class.java
            else -> null
        }

        btnPlayAgain.setOnClickListener {
            val intent = Intent(this, gvmLevel)
            intent.putExtra("GVM-mode", gvmMode)
            intent.putExtra("GVM-size", gvmSize)
            startActivityForResult(intent, 1)
            finish()
        }

        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}