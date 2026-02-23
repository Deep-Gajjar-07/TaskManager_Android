package com.example.taskmanager

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // enable fullscreen mode for app so remove the navigation bar view
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        val searchTask: SearchView = findViewById(R.id.searchTasks)
        val floatBtn : FloatingActionButton = findViewById(R.id.floatBtnAddTask)

        // getting internally EditText of SearchView for Styling
        val searchViewEdit =
            searchTask.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchViewEdit.setHintTextColor(Color.BLACK)
        searchViewEdit.setTextColor(Color.BLACK)

        floatBtn.setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }

    }
}