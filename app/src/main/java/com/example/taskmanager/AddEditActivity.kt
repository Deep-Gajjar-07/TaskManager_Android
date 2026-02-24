package com.example.taskmanager

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanager.data.TaskDataBase
import com.example.taskmanager.data.TaskEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup

class AddEditActivity : AppCompatActivity() {

    lateinit var TaskTitle: EditText
    lateinit var TaskDesc: EditText
    lateinit var btnTask: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit)


        val chipGroup: ChipGroup = findViewById(R.id.chipGroupPriority)
        val database = TaskDataBase.getDB(this)

        TaskTitle = findViewById(R.id.editTaskTitle)
        TaskDesc = findViewById(R.id.editTaskDesc)
        btnTask = findViewById(R.id.btnSaveTask)

        btnTask.setOnClickListener {
            //getting selected chip id from chip group
            val selectedChipID = chipGroup.checkedChipId
            // add value to priority variable of that selected chip ID
            val priority = when (selectedChipID) {
                R.id.chipHigh -> "High"
                R.id.chipMedium -> "Medium"
                R.id.chipLow -> "Low"
                else -> "Low"
            }

            if (TaskTitle.text.isEmpty()) {
                TaskTitle.error = "Please Enter Task Title!"
            } else {
                val task_title = TaskTitle.text.toString().trim()
                val task_desc = TaskDesc.text.toString().trim()
                val task = TaskEntity(
                    taskTitle = task_title, taskDesc = task_desc, priority = priority,
                )
                val inserted = database.taskDao.insertTask(task)
                if (inserted > 0) {
                    Toast.makeText(this, "Task: $task_title Inserted!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Failed To Insert Task!!!!", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
}