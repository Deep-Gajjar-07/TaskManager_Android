package com.example.taskmanager

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanager.data.TaskDataBase
import com.example.taskmanager.data.TaskEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup

class AddEditActivity : AppCompatActivity() {

    private lateinit var TaskTitle: EditText
    private lateinit var TaskDesc: EditText
    private lateinit var btnTask: MaterialButton
    private lateinit var pageTitle: TextView

    private var taskID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit)

        val chipGroup: ChipGroup = findViewById(R.id.chipGroupPriority)
        val database = TaskDataBase.getDB(this)

        TaskTitle = findViewById(R.id.editTaskTitle)
        TaskDesc = findViewById(R.id.editTaskDesc)
        btnTask = findViewById(R.id.btnSaveTask)
        pageTitle = findViewById(R.id.txtTitlePage)

        // check for edit operation or not
        taskID = intent.getIntExtra("task_id", -1)

        if (taskID != -1) {
            //change button and page title text
            pageTitle.text = "Update Task"
            btnTask.text = "Save Changes"

            val task_title = intent.getStringExtra("task_title")
            val desc = intent.getStringExtra("task_desc")
            val priority = intent.getStringExtra("task_priority")

            TaskTitle.setText(task_title)
            TaskDesc.setText(desc)

            when (priority) {
                "High" -> chipGroup.check(R.id.chipHigh)
                "Medium" -> chipGroup.check(R.id.chipMedium)
                "Low" -> chipGroup.check(R.id.chipLow)
            }

        }

        btnTask.setOnClickListener {
            val task_title = TaskTitle.text.toString().trim()
            val task_desc = TaskDesc.text.toString().trim()

            if (TaskTitle.text.isEmpty()) {
                TaskTitle.error = "Please Enter Task Title!"
            }

            //getting selected chip id from chip group
            val selectedChipID = chipGroup.checkedChipId
            // add value to priority variable of that selected chip ID
            val priority = when (selectedChipID) {
                R.id.chipHigh -> "High"
                R.id.chipMedium -> "Medium"
                R.id.chipLow -> "Low"
                else -> "Low"
            }

            if (taskID == -1) {
                // insert operation
                val task = TaskEntity(
                    taskTitle = task_title, taskDesc = task_desc, priority = priority,
                )
                val inserted = database.taskDao.insertTask(task)
                if (inserted > 0) {
                    Toast.makeText(this, "Task: $task_title Inserted!", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Failed To Insert Task!!!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Update Operation
                val taskUpdate = TaskEntity(
                    taskID = taskID,
                    taskTitle = task_title,
                    taskDesc = task_desc,
                    priority = priority
                )
                database.taskDao.updateTask(taskUpdate)
                Toast.makeText(this, "Task: $task_title Updated!!", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()


            }
        }


    }
}