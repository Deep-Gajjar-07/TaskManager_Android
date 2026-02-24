package com.example.taskmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1)
abstract class TaskDataBase : RoomDatabase() {

    abstract val taskDao: TaskDao

    companion object {
        fun getDB(context: Context): TaskDataBase {
            return Room.databaseBuilder(context, TaskDataBase::class.java, "TaskDB")
                .allowMainThreadQueries().build()
        }
    }

}