package com.example.task_tracker_v2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Update
import com.example.task_tracker_v2.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository): ViewModel()
{
    val task: StateFlow<List<Task>> = repository.AllTasks.stateIn(scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun AddTask(task: Task){
        viewModelScope.launch {
            repository.AddTask(task)
        }
    }
    fun UpdateTask(task: Task){
        viewModelScope.launch {
            repository.UpdateTask(task)
        }
    }
    fun DeleteTask(task: Task){
        viewModelScope.launch {
            repository.DeleteTask(task)
        }
    }
    fun ToggleDone(task: Task){
        val updatedtask=task.copy(
            isDone = !task.isDone
        )
        UpdateTask(updatedtask)
    }
}