package com.example.createtasks

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.createtasks.databinding.ItemTaskBinding // fixed: was task_tracker_v2

class TaskAdapter(
    private val onToggle: (Task) -> Unit,
    private val onDelete: (Task) -> Unit,
    private val onShare: (Task) -> Unit,
    private val onItemClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.textTitle.text = task.title
            binding.textDueDate.text = task.dueDate
            binding.textDueDate.isVisible = task.dueDate.isNotBlank()

            val titleFlags = binding.textTitle.paintFlags
            binding.textTitle.paintFlags = if (task.isDone) {
                titleFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                titleFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // Reset the listener so binding a recycled row does not trigger a toggle.
            binding.checkboxDone.setOnCheckedChangeListener(null)
            binding.checkboxDone.isChecked = task.isDone
            binding.checkboxDone.setOnCheckedChangeListener { _, _ -> onToggle(task) }

            binding.buttonDelete.setOnClickListener { onDelete(task) }
            binding.buttonShare.setOnClickListener { onShare(task) }
            binding.root.setOnClickListener { onItemClick(task) }
        }
    }

    companion object {
        private val TaskDiffCallback = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }
        }
    }
}