package ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.impl

import java.time.LocalDate
import java.time.ZoneId
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.HourSlot
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.service.TaskService
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.GetDayHourSlotsUseCase

class GetDayHourSlotsUseCaseImpl(
    private val taskService: TaskService
) : GetDayHourSlotsUseCase {
    override suspend fun invoke(date: LocalDate, zoneId: ZoneId): List<HourSlot> {
        taskService.ensureInitialData()
        val tasks = taskService.getTasksForDate(date, zoneId)
        return taskService.buildHourSlots(tasks, date, zoneId)
    }
}
