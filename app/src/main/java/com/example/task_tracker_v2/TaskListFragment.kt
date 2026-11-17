package com.example.task_tracker_v2

import android.content.Intent
import android.os.Bundle
import android.view.View
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
    var onEditClick: ((Int) -> Unit)? = null

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
            onShare = { _ -> /* TODO Part 6: connect share Intent */ },
            onItemClick = { task ->
                val intent = Intent(requireContext(), CreateTasksActivity::class.java).apply {
                    putExtra("task_id", task.id)
                    putExtra("task_title", task.title)
                    putExtra("task_description", task.description)
                    putExtra("task_due_date", task.dueDate)
                    putExtra("task_is_done", task.isDone)
                }
                startActivity(intent)
            }
        )

        binding.recyclerTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTasks.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), CreateTasksActivity::class.java)
            startActivity(intent)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}