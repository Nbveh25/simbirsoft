package ru.kazan.itis.bikmukhametov.simbirsoft.domain.service

import java.time.LocalDate
import java.time.ZoneOffset
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.repository.TaskRepository
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.service.impl.DefaultTaskService

class DefaultTaskServiceTest {

    private val zone = ZoneOffset.UTC
    private val testDate = LocalDate.of(2026, 4, 1)

    @Test
    fun ensureInitialData_insertsSeedWhenDatabaseEmpty() = runTest {
        val repository = FakeTaskRepository()
        val seed = listOf(TaskItem(1, 1000L, 2000L, "Seed", "d"))
        val service = DefaultTaskService(repository) { seed }

        service.ensureInitialData()

        assertEquals(1, repository.count())
        assertEquals("Seed", repository.getById(1L)?.name)
    }

    @Test
    fun ensureInitialData_doesNothingWhenDatabaseNotEmpty() = runTest {
        val repository = FakeTaskRepository().apply {
            runBlocking { insert(TaskItem(99, 1L, 2L, "Existing", "")) }
        }
        var seedCalled = false
        val service = DefaultTaskService(repository) {
            seedCalled = true
            error("should not be called")
        }

        service.ensureInitialData()

        assertEquals(1, repository.count())
        assertEquals(false, seedCalled)
    }

    @Test
    fun getTaskById_delegatesToRepository() = runTest {
        val task = TaskItem(5, 1L, 2L, "T", "")
        val repository = FakeTaskRepository().apply { runBlocking { insert(task) } }
        val service = DefaultTaskService(repository) { emptyList() }

        assertEquals(task, service.getTaskById(5L))
        assertNull(service.getTaskById(999L))
    }

    @Test
    fun getTasksForDate_returnsOnlyTasksIntersectingDay() = runTest {
        val dayStart = testDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val dayEnd = testDate.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val before = TaskItem(1, dayStart - HOUR_MILLIS, dayStart, "before", "")
        val inside = TaskItem(2, dayStart + HOUR_MILLIS, dayStart + 2 * HOUR_MILLIS, "inside", "")
        val after = TaskItem(3, dayEnd, dayEnd + HOUR_MILLIS, "after", "")
        val repository = FakeTaskRepository().apply {
            runBlocking {
                insert(before)
                insert(inside)
                insert(after)
            }
        }
        val service = DefaultTaskService(repository) { emptyList() }

        val result = service.getTasksForDate(testDate, zone)

        assertEquals(listOf(inside), result)
    }

    @Test
    fun createTask_trimsFieldsAndReturnsNewId() = runTest {
        val repository = FakeTaskRepository()
        val service = DefaultTaskService(repository) { emptyList() }

        val id = service.createTask(
            name = "  Name  ",
            startAtMillis = 100L,
            endAtMillis = 200L,
            description = "  desc  "
        )

        assertEquals(1L, id)
        val saved = repository.getById(1L)!!
        assertEquals("Name", saved.name)
        assertEquals("desc", saved.description)
        assertEquals(100L, saved.dateStart)
        assertEquals(200L, saved.dateFinish)
    }

    @Test
    fun buildHourSlots_returns24HoursWithExpectedRanges() {
        val service = DefaultTaskService(FakeTaskRepository()) { emptyList() }
        val slots = service.buildHourSlots(emptyList(), testDate, zone)

        assertEquals(24, slots.size)
        slots.forEachIndexed { index, slot ->
            assertEquals(index, slot.hourStart)
            assertEquals(index + 1, slot.hourEnd)
            assertTrue(slot.tasks.isEmpty())
        }
    }

    @Test
    fun buildHourSlots_placesTaskInOverlappingHoursAndSortsByStart() {
        val dayStart = testDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val service = DefaultTaskService(FakeTaskRepository()) { emptyList() }
        val later = TaskItem(2, dayStart + 14 * HOUR_MILLIS + 30 * MINUTE_MILLIS, dayStart + 16 * HOUR_MILLIS, "B", "")
        val earlier = TaskItem(1, dayStart + 14 * HOUR_MILLIS, dayStart + 15 * HOUR_MILLIS, "A", "")
        val tasks = listOf(later, earlier)

        val slots = service.buildHourSlots(tasks, testDate, zone)

        assertEquals(2, slots[14].tasks.size)
        assertEquals(listOf(earlier, later), slots[14].tasks)
        assertEquals(1, slots[15].tasks.size)
        assertEquals(later, slots[15].tasks.single())
        assertTrue(slots[13].tasks.isEmpty())
    }

    @Test
    fun buildHourSlots_oneHourTask_appearsOnlyInMatchingSlot() {
        val dayStart = testDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val service = DefaultTaskService(FakeTaskRepository()) { emptyList() }
        val task = TaskItem(
            1,
            dayStart + 14 * HOUR_MILLIS,
            dayStart + 15 * HOUR_MILLIS,
            "hour14",
            ""
        )

        val slots = service.buildHourSlots(listOf(task), testDate, zone)

        assertTrue(slots[13].tasks.isEmpty())
        assertEquals(listOf(task), slots[14].tasks)
        assertTrue(slots[15].tasks.isEmpty())
    }

    @Test
    fun formatDateTimeRange_containsBothDatesAndSeparator() {
        val service = DefaultTaskService(FakeTaskRepository()) { emptyList() }
        val task = TaskItem(1, 0L, 60_000L, "x", "")

        val text = service.formatDateTimeRange(task, ZoneOffset.UTC)

        assertTrue(text.contains("1970-01-01"))
        assertTrue(text.contains(" - "))
        assertEquals(2, text.split("1970-01-01").size - 1)
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

        override suspend fun getTasksInRange(startInclusive: Long, endExclusive: Long): List<TaskItem> =
            tasks.filter { task ->
                task.dateStart < endExclusive && task.dateFinish > startInclusive
            }
    }

    private companion object {
        private const val HOUR_MILLIS = 60L * 60L * 1000L
        private const val MINUTE_MILLIS = 60L * 1000L
    }
}
