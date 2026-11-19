package com.example.task_tracker_v2

import android.os.Bundle
import android.view.View
import android.content.Intent
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.task_tracker_v2.TaskRepository
import com.example.task_tracker_v2.TaskViewModel
import com.example.task_tracker_v2.TaskViewModelFactory
import com.example.task_tracker_v2.databinding.FragmentTaskListBinding
import kotlinx.coroutines.launch

class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    // Part 5 can set these callbacks without editing this fragment.
    var onAddClick: (() -> Unit)? = null
    var onEditClick: ((Task) -> Unit)? = null

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            TaskRepository(TaskDatabase.getInstance(requireContext()).taskDao())
        )
    }

    private lateinit var adapter: TaskAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTaskListBinding.bind(view)

        adapter = TaskAdapter(
            onToggle = viewModel::ToggleDone,
            onDelete = viewModel::DeleteTask,
            onShare = { task -> shareTask(task) },
            onItemClick = { task -> onEditClick?.invoke(task) }
        )

        binding.recyclerTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTasks.adapter = adapter

        binding.fabAdd.setOnClickListener {
            onAddClick?.invoke() // TODO Part 5: navigate to Add task screen
        }

        // Collect the StateFlow only while the fragment view is started.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.task.collect { tasks ->
                    adapter.submitList(tasks)
                    binding.emptyState.isVisible = tasks.isEmpty()
                }
            }
        }
    }
    private fun shareTask(task: Task) {

        val shareText = """
        Task: ${task.title}
        
        Description: ${task.description}
        
        Due Date: ${task.dueDate}
    """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        val chooser = Intent.createChooser(
            shareIntent,
            "Share Task"
        )

        startActivity(chooser)
    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}