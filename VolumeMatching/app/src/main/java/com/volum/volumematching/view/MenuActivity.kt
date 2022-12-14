package com.volum.volumematching.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.volum.volumematching.databinding.ActivityMenuBinding
import com.volum.volumematching.LevelSelectionActivity
import kotlin.random.Random

class MenuActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val btnAuthors by lazy { binding.btnAuthors }
    private val btnArcade by lazy { binding.btnArcade }
    private val btnMania by lazy { binding.btnMania }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            this.supportActionBar?.hide();
        }

        btnAuthors.setOnClickListener {
            val intent = Intent(this, AuthorsActivity::class.java)
            startActivity(intent)
        }

        btnArcade.setOnClickListener {
            val intent = Intent(this, LevelSelectionActivity::class.java)
            startActivity(intent)
        }

        btnMania.setOnClickListener {
            val level = Random.nextInt(1, 4)
            val levelClass = when(level) {
                1 -> Level1Activity::class.java
                2 -> Level2Activity::class.java
                3 -> Level3Activity::class.java
                else -> null
            }

            val intent = Intent(this, levelClass)
            intent.putExtra("GVM-mode", 2)
            intent.putExtra("GVM-size", level)
            intent.putExtra("GVM-User-Time", 31000)
            intent.putExtra("GVM-User-Score", 0)
            startActivityForResult(intent, 1)
        }
    }
}