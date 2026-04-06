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
