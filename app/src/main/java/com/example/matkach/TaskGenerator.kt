package com.example.matkach

import android.util.Log
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.random.Random

data class GeneratedTask(
    val text: String,
    val answer: Double
)

class TaskGenerator(private val tasks: List<Task>) {

    companion object {
        private const val TAG = "TaskGenerator"
    }

    fun generate(level: DifficultyLevel, types: List<String>): GeneratedTask {
        val filtered = tasks.filter {
            it.level.equals(level.value, ignoreCase = true) && it.type in types
        }

        if (filtered.isEmpty()) {
            Log.e(TAG, "No tasks found for level=${level.value}, types=$types")
            return GeneratedTask(text = "Нет задач для выбранных параметров", answer = 0.0)
        }

        val task = filtered.random()
        val values = generateVariables(task)

        return buildTask(task, values)
    }

    /**
     * Генерирует значения переменных с учётом флага needsWholeDivision.
     * Деление больше не требует отдельного if в generate() — логика инкапсулирована здесь.
     */
    private fun generateVariables(task: Task): Map<String, Double> {
        val values = mutableMapOf<String, Double>()

        if (task.needsWholeDivision) {
            // Гарантируем целочисленное деление: a = result * b
            val result = Random.nextInt(2, 20)
            val b = Random.nextInt(2, 20)
            values["a"] = (result * b).toDouble()
            values["b"] = b.toDouble()
        } else {
            for ((key, range) in task.variables) {
                values[key] = Random.nextInt(range.min.toInt(), range.max.toInt() + 1).toDouble()
            }
        }

        return values
    }

    private fun buildTask(task: Task, values: Map<String, Double>): GeneratedTask {
        var text = task.template
        for ((key, value) in values) {
            text = text.replace("{$key}", formatValue(value))
        }

        return try {
            val expression = ExpressionBuilder(task.formula)
                .variables(values.keys)
                .build()
                .apply { values.forEach { (k, v) -> setVariable(k, v) } }

            val rawResult = expression.evaluate()
            // Используем поле round из JSON вместо захардкоженного "%.2f"
            val scale = Math.pow(10.0, task.round.toDouble())
            val result = Math.round(rawResult * scale).toDouble() / scale

            GeneratedTask(text = text, answer = result)
        } catch (e: Exception) {
            Log.e(TAG, "Expression error for task '${task.id}': $text", e)
            GeneratedTask(text = "Ошибка задачи", answer = 0.0)
        }
    }

    private fun formatValue(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString()
        else String.format("%.2f", value)
}
