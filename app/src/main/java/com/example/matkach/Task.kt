package com.example.matkach

data class Task(
    val id: String,
    val level: String,
    val type: String,
    val template: String,
    val variables: Map<String, VariableRange>,
    val formula: String,
    val round: Int,
    /** Если true — генератор гарантирует целочисленное деление (a = result * b).
     *  Управляется через tasks.json, не через хардкод в генераторе. */
    val needsWholeDivision: Boolean = false
)

data class VariableRange(
    val min: Double,
    val max: Double
)

enum class DifficultyLevel(val value: String) {
    EASY("easy"),
    HARD("hard");

    companion object {
        fun fromString(value: String): DifficultyLevel =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown difficulty level: $value")
    }
}
