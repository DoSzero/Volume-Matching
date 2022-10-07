package com.volum.volumematching.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.volum.volumematching.databinding.ActivityAuthorsBinding

class AuthorsActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAuthorsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            this.supportActionBar?.hide();
        }
    }
}