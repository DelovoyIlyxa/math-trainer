package com.example.matkach

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    fun updateLivesView(livesText: TextView, lives: Int) {
        val hearts = "❤️".repeat(lives)
        livesText.text = hearts
    }

    fun loadJSON(context: Context): String {
        val inputStream = context.assets.open("tasks.json")
        return inputStream.bufferedReader().use { it.readText() }
    }

    @SuppressLint("DefaultLocale")
    fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            String.format("%.2f", value)
        }
    }

    fun getRandomDifficulty(): String {
        var num = Random.nextInt(2)
        if (num == 0)
            return "easy"
        else
            return "hard"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = findViewById<Button>(R.id.checkButton)
        val input = findViewById<EditText>(R.id.answerInput)
        val backButton = findViewById<Button>(R.id.backButton)
        val livesText = findViewById<TextView>(R.id.livesText)

        val difficulty = intent.getStringExtra("difficulty") ?: "easy"

        lateinit var generator: TaskGenerator
        lateinit var currentTask: GeneratedTask
        var isAnswered = false
        var lives = 3

        // Score points
        var correct = 0
        var missing = 0

        // Tasks generator
        val repository = TaskRepository(this)
        val tasks = repository.loadTasks()
        var level = difficulty

        if (difficulty == "combi") {
            level = getRandomDifficulty()
        }

        generator = TaskGenerator(tasks)
        currentTask = generator.generate(level)

        // Lives
        updateLivesView(livesText, lives)

        val taskText = findViewById<TextView>(R.id.taskText)
        taskText.text = currentTask.text

        backButton.setOnClickListener {
            finish()
        }

        button.setOnClickListener {

            if (!isAnswered) {
                // проверка
                val userAnswer = input.text.toString().toDoubleOrNull()

                if (userAnswer != null) {
                    val isCorrect = kotlin.math.abs(userAnswer - currentTask.answer) < 0.01

                    if (isCorrect) {
                        correct++
                        taskText.text = "✅ Верно!"
                    } else {
                        missing++
                        lives--
                        updateLivesView(livesText, lives)
                        taskText.text =
                            "❌ Неверно. Ответ: ${formatNumber(currentTask.answer)}"
                    }

                    if (lives == 0) {
                        taskText.text =
                            "Игра окончена. Правильных ответов: ${correct} из ${correct + missing}"
                        button.isEnabled = false
                        input.isEnabled = false
                    }

                    button.text = "Дальше"
                    isAnswered = true
                }

            } else {
                // следующая задача
                if (difficulty == "combi") {
                    level = getRandomDifficulty()
                }
                currentTask = generator.generate(level)

                taskText.text = currentTask.text
                input.text.clear()

                button.text = "Проверить"
                isAnswered = false
            }
        }
    }
}