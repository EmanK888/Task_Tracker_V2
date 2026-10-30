package com.example.task_tracker_v2

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

    @RunWith(AndroidJUnit4::class) class TaskDaoTest {

        private lateinit var db: TaskDatabase
        private lateinit var dao: TaskDao

        @Before
        fun setup() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            db = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java).build()
            dao = db.taskDao()
        }

        @After
        fun tearDown() {
            db.close()
        }

        @Test
        fun insert_thenRead_returnsTask() = runTest {
            dao.insertTask(Task(title = "Study Kotlin"))

            val tasks = dao.getAllTasks().first()

            assertEquals(1, tasks.size)
            assertEquals("Study Kotlin", tasks[0].title)
        }

        @Test
        fun update_changesTask() = runTest {
            dao.insertTask(Task(title = "Old title"))
            val saved = dao.getAllTasks().first()[0]

            dao.updateTask(saved.copy(title = "New title", isDone = true))

            val updated = dao.getTaskById(saved.id)
            assertEquals("New title", updated?.title)
            assertTrue(updated?.isDone == true)
        }

        @Test
        fun delete_removesTask() = runTest {
            dao.insertTask(Task(title = "Delete me"))
            val saved = dao.getAllTasks().first()[0]

            dao.deleteTask(saved)

            assertTrue(dao.getAllTasks().first().isEmpty())
        }

        @Test
        fun getTaskById_missingId_returnsNull() = runTest {
            assertNull(dao.getTaskById(999))
        }
    }
