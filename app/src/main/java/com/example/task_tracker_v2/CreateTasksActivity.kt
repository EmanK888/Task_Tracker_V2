package com.example.task_tracker_v2

import android.app.DatePickerDialog
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
import java.util.Calendar
import java.util.Locale

class CreateTasksActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels {
        val dao = TaskDatabase.getInstance(applicationContext).taskDao()
        TaskViewModelFactory(TaskRepository(dao))
    }

    private var editingTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_tasks)

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

        setupDueDatePicker(editDueDate)

        buttonBack.setOnClickListener { finish() }

        buttonSave.setOnClickListener {
            val title = editTitle.text.toString().trim()

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
                viewModel.AddTask(
                    Task(
                        title = title,
                        dueDate = dueDate,
                        description = description,
                        isDone = isDone
                    )
                )
            } else {
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

    private fun setupDueDatePicker(editDueDate: EditText) {
        editDueDate.isFocusable = false
        editDueDate.isClickable = true

        editDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val currentDate = editDueDate.text?.toString()?.trim()
            if (!currentDate.isNullOrEmpty()) {
                val parts = currentDate.split("-")
                if (parts.size == 3) {
                    val year = parts[0].toIntOrNull()
                    val month = parts[1].toIntOrNull()?.minus(1)
                    val day = parts[2].toIntOrNull()
                    if (year != null && month != null && day != null && month in 0..11 && day in 1..31) {
                        calendar.set(year, month, day)
                    }
                }
            }

            DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format(
                        Locale.getDefault(),
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )
                    editDueDate.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}