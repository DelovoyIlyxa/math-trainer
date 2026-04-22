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

    fun generate(level: DifficultyLevel, types: List<String>): GeneratedTask {

        val filtered = tasks.filter {
            it.level.equals(level.value, ignoreCase = true) &&
                    it.type in types
        }

        if (filtered.isEmpty()) {
            Log.e("TASK_GENERATOR", "EMPTY FILTER")
            Log.e("TASK_GENERATOR", "level=${level.value}")
            Log.e("TASK_GENERATOR", "types=$types")

            return GeneratedTask(
                text = "Нет задач для выбранных параметров",
                answer = 0.0
            )
        }

        val task = filtered.random()

        val values = mutableMapOf<String, Double>()

        // =========================
        // 1. ГЕНЕРАЦИЯ ПЕРЕМЕННЫХ
        // =========================
        for ((key, range) in task.variables) {

            val value = Random.nextInt(
                range.min.toInt(),
                range.max.toInt() + 1
            ).toDouble()

            values[key] = value
        }

        // =========================
        // 2. СПЕЦ-ЛОГИКА ДЛЯ ДЕЛЕНИЯ
        // =========================
        if (task.type == "division") {

            val result = Random.nextInt(2, 20)
            val b = Random.nextInt(2, 20)
            val a = result * b

            values["a"] = a.toDouble()
            values["b"] = b.toDouble()

            val text = task.template
                .replace("{a}", a.toString())
                .replace("{b}", b.toString())

            return GeneratedTask(
                text = text,
                answer = result.toDouble()
            )
        }

        // =========================
        // 3. ОБЫЧНЫЕ ЗАДАЧИ
        // =========================
        var text = task.template

        for ((key, value) in values) {

            val formatted = if (value % 1.0 == 0.0) {
                value.toInt().toString()
            } else {
                String.format("%.2f", value)
            }

            text = text.replace("{$key}", formatted)
        }

        return try {

            val expression = ExpressionBuilder(task.formula)
                .variables(values.keys)
                .build()

            for ((key, value) in values) {
                expression.setVariable(key, value)
            }

            var result = expression.evaluate()
            result = String.format("%.2f", result).toDouble()

            GeneratedTask(
                text = text,
                answer = result
            )

        } catch (e: Exception) {

            Log.e("TASK_GENERATOR", "Expression error: $text", e)

            GeneratedTask(
                text = "Ошибка задачи",
                answer = 0.0
            )
        }
    }
}
