package ru.kazan.itis.bikmukhametov.simbirsoft.domain.model

data class HourSlot(
    val hourStart: Int,
    val hourEnd: Int,
    val tasks: List<TaskItem>
)
