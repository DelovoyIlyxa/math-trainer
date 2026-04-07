package com.example.matkach

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    fun updateLivesView(livesText: TextView, lives: Int) {
        livesText.text = "❤️".repeat(lives)
    }

    @SuppressLint("DefaultLocale")
    fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) value.toInt().toString() else String.format("%.2f", value)
    }

    fun getRandomDifficulty(): DifficultyLevel {
        val num = Random.nextInt(2)
        return if (num == 0) DifficultyLevel.EASY else DifficultyLevel.HARD
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
        var level = DifficultyLevel.fromString(difficulty)

        if (difficulty == DifficultyLevel.COMBI.value) {
            level = getRandomDifficulty()
        }

        generator = TaskGenerator(tasks)
        currentTask = generator.generate(level)

        // Lives
        updateLivesView(livesText, lives)

        val taskText = findViewById<TextView>(R.id.taskText)
        taskText.text = currentTask.text
        // TODO: Сообщение пользователю: если результат дробь - округлить до сотых

        backButton.setOnClickListener {
            finish()
        }

        button.setOnClickListener {

            if (!isAnswered) {
                // Check input
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
                        taskText.text = taskText.text.toString() +
                            "\n Игра окончена. Правильных ответов: ${correct} из ${correct + missing}"
                        button.isEnabled = false
                    }

                    input.isEnabled = false
                    button.text = "Дальше"
                    isAnswered = true
                }

            } else {
                // Next Task
                if (difficulty == DifficultyLevel.COMBI.value) {
                    level = getRandomDifficulty()
                }
                currentTask = generator.generate(level)

                taskText.text = currentTask.text
                input.text.clear()
                input.isEnabled = true

                button.text = "Проверить"
                isAnswered = false
            }
        }
    }
}