package com.example.task_tracker_v2
import com.example.task_tracker_v2.Task
import com.example.task_tracker_v2.TaskDao
import kotlinx.coroutines.flow.Flow
class TaskRepository(private val taskDao: TaskDao) {
val AllTasks: Flow<List<Task>> = taskDao.getAllTasks()
    suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id)
    }
    suspend fun AddTask(task: Task){
        taskDao.insertTask(task)
    }
    suspend fun UpdateTask(task: Task){
        taskDao.updateTask(task)
    }
    suspend fun DeleteTask(task: Task){
        taskDao.deleteTask(task)
    }
}


