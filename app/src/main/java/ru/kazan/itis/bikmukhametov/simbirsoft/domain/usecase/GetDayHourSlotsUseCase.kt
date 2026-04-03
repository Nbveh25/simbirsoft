package ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase

import java.time.LocalDate
import java.time.ZoneId
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.HourSlot

fun interface GetDayHourSlotsUseCase {
    suspend operator fun invoke(date: LocalDate, zoneId: ZoneId): List<HourSlot>
}
