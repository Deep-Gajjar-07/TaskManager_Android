package com.example.taskmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val taskID: Int = 0,

    val taskTitle: String,
    val taskDesc: String,
    val priority: String,
    val isCompleted: Boolean = false
)