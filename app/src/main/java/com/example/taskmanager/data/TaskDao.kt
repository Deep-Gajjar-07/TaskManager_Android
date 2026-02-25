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

}