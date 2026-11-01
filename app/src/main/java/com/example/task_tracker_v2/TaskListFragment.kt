package com.example.task_tracker_v2

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.task_tracker.TaskRepository
import com.example.task_tracker.TaskViewModel
import com.example.task_tracker.TaskViewModelFactory
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
            onItemClick = { task -> onEditClick?.invoke(task.id) }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
