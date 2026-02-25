package com.example.taskmanager

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.data.TaskDataBase
import com.example.taskmanager.data.TaskEntity
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

        // getting internally EditText of SearchView for Styling
        val searchViewEdit =
            searchTask.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchViewEdit.setHintTextColor(Color.BLACK)
        searchViewEdit.setTextColor(Color.BLACK)

        searchTask.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.trim()

                if (query != null) {
                    val searchList = dataBase.taskDao.searchTask(query)
                    if (searchList.isEmpty()) {
                        Toast.makeText(this@MainActivity, "No task found!", Toast.LENGTH_SHORT)
                            .show()
                    }
                    taskAdapter.updateList(searchList)
                }
                return true
            }

        })

        floatBtn.setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }

        dataBase = TaskDataBase.getDB(this)

        val ReCycLstTask: RecyclerView = findViewById(R.id.ReCycLstTasks)
        val taskList = dataBase.taskDao.getAllTask()

        ReCycLstTask.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(taskList, onDeleteItem = { task ->
            showDeleteMessage(task)
        }) { task ->
            val intent = Intent(this, AddEditActivity::class.java)
            intent.putExtra("task_id", task.taskID)
            intent.putExtra("task_title", task.taskTitle)
            intent.putExtra("task_desc", task.taskDesc)
            intent.putExtra("task_priority", task.priority)
            startActivity(intent)
        }
        ReCycLstTask.adapter = taskAdapter

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