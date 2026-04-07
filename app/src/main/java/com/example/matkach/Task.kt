package com.example.matkach


data class Task(
    val id: String,
    val level: String,
    val type: String,
    val template: String,
    val variables: Map<String, VariableRange>,
    val formula: String,
    val round: Int
)

data class VariableRange(
    val min: Double,
    val max: Double
)

enum class DifficultyLevel(val value: String) {
    EASY("easy"),
    HARD("hard"),
    COMBI("combi");

    companion object {
        fun fromString(value: String): DifficultyLevel {
            return entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown level: $value")
        }
    }
}
