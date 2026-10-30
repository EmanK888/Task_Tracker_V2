package com.example.task_tracker_v2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task (
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    val title:String,
    val dueDate:String="",
    val description:String="",
    val isDone: Boolean= false
)