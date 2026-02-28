package com.example.taskmanager.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(taskEntity: TaskEntity): Long

    @Query("SELECT * FROM tasks ORDER BY taskID DESC")
    fun getAllTask(): List<TaskEntity>

    @Delete
    fun deleteTask(taskEntity: TaskEntity)

    @Update
    fun updateTask(taskEntity: TaskEntity)

    @Query("SELECT * FROM Tasks WHERE taskTitle LIKE '%'||:query||'%'")
    fun searchTask(query: String): List<TaskEntity>

    @Query("DELETE FROM Tasks")
    fun deleteAllTasks(): Int

    @Query("SELECT * FROM Tasks WHERE isCompleted = 1")
    fun getCompletedTasks(): List<TaskEntity>

    @Query("SELECT * FROM Tasks WHERE isCompleted = 0")
    fun getPendingTasks(): List<TaskEntity>

    @Query("SELECT * FROM Tasks WHERE priority = :priority")
    fun getTasksByPriority(priority: String): List<TaskEntity>

}