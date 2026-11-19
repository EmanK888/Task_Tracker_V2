
package com.example.task_tracker_v2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragment = supportFragmentManager
            .findFragmentById(R.id.task_list_container) as? TaskListFragment
            ?: TaskListFragment()

        // Open CreateTasksActivity to add a new task
        fragment.onAddClick = {
            val intent = Intent(
                this@MainActivity,
                CreateTasksActivity::class.java
            )
            startActivity(intent)
        }

        // Open CreateTasksActivity to edit an existing task
        fragment.onEditClick = { task ->
            val intent = Intent(
                this@MainActivity,
                CreateTasksActivity::class.java
            ).apply {
                putExtra("task_id", task.id)
                putExtra("task_title", task.title)
                putExtra("task_due_date", task.dueDate)
                putExtra("task_description", task.description)
                putExtra("task_is_done", task.isDone)
            }

            startActivity(intent)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.task_list_container, fragment)
                .commit()
        }
    }
}
