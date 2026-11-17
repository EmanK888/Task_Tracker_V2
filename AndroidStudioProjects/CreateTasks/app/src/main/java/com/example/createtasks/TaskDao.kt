package com.example.createtasks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao

interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id:Int):Task?
    @Update
    suspend fun updateTask(task:Task)
    @Delete
    suspend fun deleteTask(task:Task)
}