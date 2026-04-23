package com.example.matkach

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private fun updateLives(livesText: TextView, lives: Int) {
        livesText.text = "Ошибок: ${3 - lives} из 3"
    }

    private fun updateProgress(progressText: TextView, solved: Int, max: Int) {
        progressText.text = "Решено: $solved из $max"
    }

    private fun format(value: Double): String {
        return if (value % 1.0 == 0.0) value.toInt().toString()
        else String.format("%.2f", value)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        // UI
        val button = findViewById<Button>(R.id.checkButton)
        val input = findViewById<EditText>(R.id.answerInput)
        val backButton = findViewById<Button>(R.id.backButton)
        val taskText = findViewById<TextView>(R.id.taskText)
        val livesText = findViewById<TextView>(R.id.livesText)
        val coinsText = findViewById<TextView>(R.id.coinsText)

        // Game settings
        val maxTasks = 20

        val difficulty = intent.getStringExtra("difficulty") ?: "easy"
        val types = intent.getStringArrayListExtra("types") ?: arrayListOf()

        // защита от пустых типов
        if (types.isEmpty()) {
            taskText.text = "Ошибка: не выбраны типы задач"
            button.isEnabled = false
            return
        }

        val repository = TaskRepository(this)
        val tasks = repository.loadTasks()
        val generator = TaskGenerator(tasks)

        val level = DifficultyLevel.fromString(difficulty)

        // Game state
        var currentTask = generator.generate(level, types)

        var lives = 3
        var solved = 0
        var correct = 0

        var isAnswered = false

        // init UI
        taskText.text = currentTask.text
        updateLives(livesText, lives)
        updateProgress(coinsText, solved, maxTasks)

        backButton.setOnClickListener {
            finish()
        }

        button.setOnClickListener {

            if (!isAnswered) {

                val userAnswer = input.text.toString()
                    .replace(",", ".")
                    .toDoubleOrNull()

                if (userAnswer == null) {
                    Toast.makeText(this, "Введите число", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                solved++

                val isCorrect = kotlin.math.abs(userAnswer - currentTask.answer) < 0.01

                if (isCorrect) {
                    correct++
                    taskText.text = "✅ Верно!"
                } else {
                    lives--
                    updateLives(livesText, lives)
                    taskText.text = "❌ Неверно. Ответ: ${format(currentTask.answer)}"
                }

                updateProgress(coinsText, solved, maxTasks)

                if (lives <= 0) {
                    taskText.text = taskText.text.toString() + "\nИгра окончена.\nМного ошибок!"
                    button.isEnabled = false
                    return@setOnClickListener
                }

                if (solved >= maxTasks) {
                    taskText.text = taskText.text.toString() + "\n🎉 Уровень пройден!\n$correct / $solved"
                    button.isEnabled = false
                    return@setOnClickListener
                }

                input.isEnabled = false
                button.text = "Дальше"
                isAnswered = true

            } else {

                currentTask = generator.generate(level, types)

                taskText.text = currentTask.text
                input.text.clear()
                input.isEnabled = true

                button.text = "Проверить"
                isAnswered = false
            }
        }
    }
}