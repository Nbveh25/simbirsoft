package ru.kazan.itis.bikmukhametov.simbirsoft

import java.time.LocalDate
import java.time.ZoneOffset
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.repository.TaskRepository
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.service.impl.DefaultTaskService

class ExampleUnitTest {
    @Test
    fun buildHourSlots_marksOverlappingTasks() {
        val service = DefaultTaskService(FakeTaskRepository(), initialDataProvider = { emptyList() })
        val date = LocalDate.of(2026, 4, 1)
        val dayStart = date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val tasks = listOf(
            TaskItem(
                id = 1,
                dateStart = dayStart + 14 * HOUR_MILLIS,
                dateFinish = dayStart + 15 * HOUR_MILLIS,
                name = "Task 14",
                description = "desc"
            ),
            TaskItem(
                id = 2,
                dateStart = dayStart + (14 * HOUR_MILLIS) + (30 * MINUTE_MILLIS),
                dateFinish = dayStart + 16 * HOUR_MILLIS,
                name = "Task 14:30",
                description = "desc"
            )
        )

        val slots = service.buildHourSlots(tasks, date, ZoneOffset.UTC)

        assertEquals(24, slots.size)
        assertEquals(2, slots[14].tasks.size)
        assertEquals(1, slots[15].tasks.size)
        assertTrue(slots[13].tasks.isEmpty())
    }

    @Test
    fun ensureInitialData_insertsSeedWhenDatabaseEmpty() = runBlocking {
        val repository = FakeTaskRepository()
        val seedTasks = listOf(
            TaskItem(1, 1000L, 2000L, "Seed", "Seed desc")
        )
        val service = DefaultTaskService(repository, initialDataProvider = { seedTasks })

        service.ensureInitialData()

        assertEquals(1, repository.count())
        assertEquals("Seed", repository.getById(1L)?.name)
    }

    private class FakeTaskRepository : TaskRepository {
        private val tasks = mutableListOf<TaskItem>()

        override suspend fun count(): Int = tasks.size

        override suspend fun insertAll(tasks: List<TaskItem>) {
            this.tasks.addAll(tasks)
        }

        override suspend fun insert(task: TaskItem): Long {
            val id = if (task.id == 0L) (tasks.maxOfOrNull { it.id } ?: 0L) + 1 else task.id
            tasks.add(task.copy(id = id))
            return id
        }

        override suspend fun getById(id: Long): TaskItem? = tasks.firstOrNull { it.id == id }

        override suspend fun getTasksInRange(startInclusive: Long, endExclusive: Long): List<TaskItem> {
            return tasks.filter { task ->
                task.dateStart < endExclusive && task.dateFinish > startInclusive
            }
        }
    }

    private companion object {
        private const val HOUR_MILLIS = 60L * 60L * 1000L
        private const val MINUTE_MILLIS = 60L * 1000L
    }
}
