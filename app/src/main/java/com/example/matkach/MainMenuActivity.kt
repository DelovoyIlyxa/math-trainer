package com.example.matkach

import android.content.Intent
import android.os.Bundle
import android.widget.*
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

        val btnBegin = findViewById<Button>(R.id.btnBegin)
        val btnAbout = findViewById<Button>(R.id.btnAbout)

        val levelGroup = findViewById<RadioGroup>(R.id.levelGroup)

        val cbAddition = findViewById<CheckBox>(R.id.cbAddition)
        val cbSubtraction = findViewById<CheckBox>(R.id.cbSubtraction)
        val cbMultiplication = findViewById<CheckBox>(R.id.cbMultiplication)
        val cbDivision = findViewById<CheckBox>(R.id.cbDivision)
        val cbWord = findViewById<CheckBox>(R.id.cbWord)

        btnBegin.setOnClickListener {

            // 📌 УРОВЕНЬ
            val difficulty = when (levelGroup.checkedRadioButtonId) {
                R.id.easyLevel -> "easy"
                R.id.hardLevel -> "hard"
                else -> "easy"
            }

            // 📌 ТИПЫ ЗАДАЧ
            val selectedTypes = mutableListOf<String>()

            if (cbAddition.isChecked) selectedTypes.add("addition")
            if (cbSubtraction.isChecked) selectedTypes.add("subtraction")
            if (cbMultiplication.isChecked) selectedTypes.add("multiplication")
            if (cbDivision.isChecked) selectedTypes.add("division")
            if (cbWord.isChecked) selectedTypes.add("word_problem")

            // ❗ защита от пустого выбора
            if (selectedTypes.isEmpty()) {
                Toast.makeText(this, "Выберите хотя бы один тип задач", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            print(selectedTypes)
            openGame(difficulty, selectedTypes)
        }

        btnAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    private fun openGame(difficulty: String, types: List<String>) {
        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra("difficulty", difficulty)
        intent.putStringArrayListExtra("types", ArrayList(types))

        startActivity(intent)
    }
}