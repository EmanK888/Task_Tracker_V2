package com.example.createtasks

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // ViewModel built through the factory so it receives the repository
    private val viewModel: TaskViewModel by viewModels {
        val dao = TaskDatabase.getInstance(applicationContext).taskDao()
        TaskViewModelFactory(TaskRepository(dao))
    }

    // null means "create a new task"; otherwise we are editing this task
    private var editingTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editTitle = findViewById<EditText>(R.id.editTextText2)
        val editDescription = findViewById<EditText>(R.id.editTextTextMultiLine)
        val editDueDate = findViewById<EditText>(R.id.textDueDate)
        val textError = findViewById<TextView>(R.id.textView3)
        val textHeader = findViewById<TextView>(R.id.textView)
        val buttonSave = findViewById<Button>(R.id.button2)
        val buttonBack = findViewById<Button>(R.id.button3)
        val checkBox = findViewById<CheckBox>(R.id.checkBox)

        // Edit mode: the task fields arrive as intent extras
        val taskId = intent.getIntExtra("task_id", 0)
        if (taskId != 0) {
            val task = Task(
                id = taskId,
                title = intent.getStringExtra("task_title") ?: "",
                dueDate = intent.getStringExtra("task_due_date") ?: "",
                description = intent.getStringExtra("task_description") ?: "",
                isDone = intent.getBooleanExtra("task_is_done", false)
            )
            editingTask = task
            textHeader.text = "Edit Task"
            editTitle.setText(task.title)
            editDescription.setText(task.description)
            editDueDate.setText(task.dueDate)
            checkBox.isChecked = task.isDone
        }

        buttonBack.setOnClickListener { finish() }

        buttonSave.setOnClickListener {
            val title = editTitle.text.toString().trim()

            // Validation: the title is required
            if (title.isEmpty()) {
                textError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            textError.visibility = View.GONE

            val description = editDescription.text.toString().trim()
            val dueDate = editDueDate.text.toString().trim()
            val isDone = checkBox.isChecked
            val existing = editingTask

            if (existing == null) {
                // Create: Room generates the id because id = 0
                viewModel.AddTask(
                    Task(
                        title = title,
                        dueDate = dueDate,
                        description = description,
                        isDone = isDone
                    )
                )
            } else {
                // Edit: keep the same id so Room updates the existing row
                viewModel.UpdateTask(
                    existing.copy(
                        title = title,
                        dueDate = dueDate,
                        description = description,
                        isDone = isDone
                    )
                )
            }
            finish()
        }
    }
}