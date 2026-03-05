package com.example.taskmanager

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.data.TaskDataBase
import com.example.taskmanager.data.TaskEntity
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    lateinit var dataBase: TaskDataBase
    lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // enable fullscreen mode for app so remove the navigation bar view
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        val searchTask: SearchView = findViewById(R.id.searchTasks)
        val floatBtn: FloatingActionButton = findViewById(R.id.floatBtnAddTask)
        val imgBtnDelete: ImageButton = findViewById(R.id.imgBtnDelete)
        val chipGroup: ChipGroup = findViewById(R.id.taskFilterChipGroup)
        val message: TextView = findViewById(R.id.txtMessage)
        val imgBtnToggle: ImageButton = findViewById(R.id.imgBtnToggleTheme)

        dataBase = TaskDataBase.getDB(this)

        // getting internally EditText of SearchView for Styling
        val searchViewEdit =
            searchTask.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchViewEdit.setHintTextColor(Color.BLACK)
        searchViewEdit.setTextColor(Color.BLACK)

        // set the icon after activity restart
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            imgBtnToggle.setImageResource(R.drawable.icon_lightbulb)
        } else {
            imgBtnToggle.setImageResource(R.drawable.icon_moon)
        }

        searchTask.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.trim()

                if (query != null) {
                    val searchList = dataBase.taskDao.searchTask(query)
                    if (searchList.isEmpty()) {
                        message.text = "No Search Tasks Found!"
                        message.visibility = View.VISIBLE
                    } else {
                        message.visibility = View.GONE
                    }
                    taskAdapter.updateList(searchList)
                }
                return true
            }

        })

        // toggle theme for dark / light mode
        imgBtnToggle.setOnClickListener {
            val currentMode = AppCompatDelegate.getDefaultNightMode()

            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

        }

        floatBtn.setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }

        val ReCycLstTask: RecyclerView = findViewById(R.id.ReCycLstTasks)
        val taskList = dataBase.taskDao.getAllTask()

        // chip group selecting by chip for filter tasks
        chipGroup.setOnCheckedChangeListener { _, checkedID ->
            val list = when (checkedID) {
                R.id.filterChipAll -> dataBase.taskDao.getAllTask()
                R.id.filterChipCompleted -> dataBase.taskDao.getCompletedTasks()
                R.id.filterChipPending -> dataBase.taskDao.getPendingTasks()
                R.id.filterChipHigh -> dataBase.taskDao.getTasksByPriority("High")
                R.id.filterChipMedium -> dataBase.taskDao.getTasksByPriority("Medium")
                R.id.filterChipLow -> dataBase.taskDao.getTasksByPriority("Low")
                else -> dataBase.taskDao.getAllTask()
            }
            taskAdapter.updateList(list)
            taskAdapter.notifyDataSetChanged()
        }


        ReCycLstTask.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(
            taskList,
            onDeleteItem = ({ task -> showDeleteMessage(task) }),
            onTaskChecked = (
                    { updatedTask ->
                        dataBase.taskDao.updateTask(updatedTask)
                        loadTasks()
                    }
                    ),
            onEditItem = ({ task ->
                val intent = Intent(this, AddEditActivity::class.java)
                intent.putExtra("task_id", task.taskID)
                intent.putExtra("task_title", task.taskTitle)
                intent.putExtra("task_desc", task.taskDesc)
                intent.putExtra("task_priority", task.priority)
                startActivity(intent)
            })
        )


        imgBtnDelete.setOnClickListener {
            val dialogBox = AlertDialog.Builder(this)
            dialogBox.setTitle("Delete All Tasks")
            dialogBox.setMessage("Are you sure want to delete all tasks?")
            dialogBox.setPositiveButton("Yes") { _, _ ->
                try {
                    val deleteCount = dataBase.taskDao.deleteAllTasks()
                    if (deleteCount > 0) {
                        Toast.makeText(this, "All $deleteCount tasks deleted!!", Toast.LENGTH_SHORT)
                            .show()
                        loadTasks()
                    } else {
                        Toast.makeText(this, "No tasks available to delete!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (_: Exception) {
                    Toast.makeText(this, "Failed To Delete Tasks!!!", Toast.LENGTH_SHORT).show()
                }
            }

            dialogBox.setNegativeButton("No") { dialogBox, _ ->
                dialogBox.dismiss()
            }

            dialogBox.show()
        }

        if (taskList.isEmpty()) {
            imgBtnDelete.isEnabled = false
            imgBtnDelete.alpha = 0.5f
            message.visibility = View.VISIBLE
        }

        ReCycLstTask.adapter = taskAdapter
        loadTasks()
    }

    private fun loadTasks() {
        val taskList = dataBase.taskDao.getAllTask()
        taskAdapter.updateList(taskList)
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun showDeleteMessage(taskEntity: TaskEntity) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Delete Task")
        dialog.setMessage("Are you sure want to delete task :${taskEntity.taskTitle}?")
        dialog.setPositiveButton("Yes") { _, _ ->
            dataBase.taskDao.deleteTask(taskEntity)
            Toast.makeText(this, "Task: ${taskEntity.taskTitle} Deleted!", Toast.LENGTH_SHORT)
                .show()
            loadTasks()

        }
        dialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

}