package com.example.matkach

import android.annotation.SuppressLint
import android.util.Log
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.random.Random

data class GeneratedTask(
    val text: String,
    val answer: Double
)

class TaskGenerator(private val tasks: List<Task>) {

    @SuppressLint("DefaultLocale")
    fun generate(level: DifficultyLevel): GeneratedTask {
        val filtered = tasks.filter { it.level == level.value }
        val task = filtered.random()

        val values = mutableMapOf<String, Double>()

        // генерируем переменные
        for ((key, range) in task.variables) {
//            // OLD difficulty
//            val value = if (task.level == DifficultyLevel.EASY.value) {
//                Random.nextInt(range.min.toInt(), range.max.toInt() + 1).toDouble()
//            } else {
//                Random.nextDouble(range.min, range.max)
//            }
            // NEW difficulty
            val value = Random.nextInt(
                range.min.toInt(),
                range.max.toInt() + 1
            ).toDouble()
            values[key] = value
        }

        // подставляем в текст
        var text = task.template
        for ((key, value) in values) {
            val formatted = if (value % 1.0 == 0.0) {
                value.toInt().toString()
            } else {
                String.format("%.2f", value)
            }

            text = text.replace("{$key}", formatted)
        }

        // считаем формулу
        val expression = ExpressionBuilder(task.formula)
            .variables(values.keys)
            .build()

        for ((key, value) in values) {
            expression.setVariable(key, value)
        }

        try {
            var result = expression.evaluate()
            // округление
            result = String.format("%.2f", result).toDouble()

            return GeneratedTask(text, result)
        } catch (e: Exception) {
            Log.e("TASK", "Ошибка в выражении: $text")
            return GeneratedTask("Введите 0.", 0.0)
        }

    }
}
