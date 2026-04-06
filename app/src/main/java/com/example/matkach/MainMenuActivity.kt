package com.example.matkach

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnEasy = findViewById<Button>(R.id.btnEasy)
        val btnHard = findViewById<Button>(R.id.btnHard)
        val btnCombi = findViewById<Button>(R.id.btnCombi)
        val btnAbout = findViewById<Button>(R.id.btnAbout)

        btnEasy.setOnClickListener {
            openGame("easy")
        }

        btnHard.setOnClickListener {
            openGame("hard")
        }

        btnCombi.setOnClickListener {
            openGame("combi")
        }

        btnAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    private fun openGame(difficulty: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("difficulty", difficulty)
        startActivity(intent)
    }
}