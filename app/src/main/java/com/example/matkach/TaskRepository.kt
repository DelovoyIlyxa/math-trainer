package com.example.matkach

import android.content.Context
import com.google.gson.Gson

data class TaskList(val tasks: List<Task>)
const val FILENAME = "tasks.json"

class TaskRepository(private val context: Context) {

    fun loadTasks(): List<Task> {
        val json = context.assets.open(FILENAME)
            .bufferedReader()
            .use { it.readText() }

        return Gson().fromJson(json, TaskList::class.java).tasks
    }
}
