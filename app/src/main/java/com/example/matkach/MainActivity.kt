package com.example.matkach

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels

class MainActivity : BaseActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applySystemBarPadding(findViewById(R.id.main))

        val contentRoot  = findViewById<android.view.View>(R.id.contentRoot)
        //val btnFontSize  = findViewById<Button>(R.id.btnFontSize)
        val taskText     = findViewById<TextView>(R.id.taskText)
        val livesText    = findViewById<TextView>(R.id.livesText)
        val progressText = findViewById<TextView>(R.id.coinsText)
        val input        = findViewById<EditText>(R.id.answerInput)
        val checkButton  = findViewById<Button>(R.id.checkButton)
        val backButton   = findViewById<Button>(R.id.backButton)

        //setupFontButton(btnFontSize, contentRoot)

        val difficulty = intent.getStringExtra("difficulty") ?: "easy"
        val types = intent.getStringArrayListExtra("types") ?: arrayListOf()

        if (types.isEmpty()) {
            taskText.text = getString(R.string.error_no_task_types)
            checkButton.isEnabled = false
            return
        }

        viewModel.init(difficulty, types)

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is GameUiState.Question -> {
                    taskText.text = state.taskText
                    livesText.text = getString(R.string.status_mistakes, state.mistakesCount, GameConfig.MAX_LIVES)
                    progressText.text = getString(R.string.status_progress, state.solvedCount, GameConfig.MAX_TASKS)
                    input.isEnabled = true
                    input.text.clear()
                    checkButton.isEnabled = true
                    checkButton.text = getString(R.string.btn_check)
                }
                is GameUiState.AnswerResult -> {
                    taskText.text = if (state.isCorrect)
                        getString(R.string.result_correct)
                    else
                        getString(R.string.result_wrong, state.correctAnswer)
                    livesText.text = getString(R.string.status_mistakes, state.mistakesCount, GameConfig.MAX_LIVES)
                    progressText.text = getString(R.string.status_progress, state.solvedCount, GameConfig.MAX_TASKS)
                    input.isEnabled = false
                    checkButton.text = getString(R.string.btn_next)
                }
                is GameUiState.GameOver -> {
                    taskText.text = getString(R.string.game_over)
                    progressText.text = getString(R.string.status_progress, state.solvedCount, GameConfig.MAX_TASKS)
                    input.isEnabled = false
                    checkButton.isEnabled = false
                }
                is GameUiState.LevelComplete -> {
                    taskText.text = getString(R.string.level_complete, state.correctCount, state.solvedCount)
                    progressText.text = getString(R.string.status_progress, state.solvedCount, GameConfig.MAX_TASKS)
                    input.isEnabled = false
                    checkButton.isEnabled = false
                }
            }
        }

        checkButton.setOnClickListener {
            when (viewModel.uiState.value) {
                is GameUiState.Question -> {
                    val userInput = input.text.toString()
                    if (viewModel.isInvalidInput(userInput)) {
                        Toast.makeText(this, getString(R.string.error_enter_number), Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    viewModel.checkAnswer(userInput)
                }
                is GameUiState.AnswerResult -> viewModel.nextQuestion()
                else -> {}
            }
        }

        backButton.setOnClickListener { finish() }
    }
}
