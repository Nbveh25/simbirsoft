package ru.kazan.itis.bikmukhametov.simbirsoft.data.mapper

import ru.kazan.itis.bikmukhametov.simbirsoft.data.local.TaskEntity
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem

fun TaskEntity.toDomain(): TaskItem = TaskItem(
    id = id,
    dateStart = dateStart,
    dateFinish = dateFinish,
    name = name,
    description = description
)

fun TaskItem.toEntity(): TaskEntity = TaskEntity(
    id = id,
    dateStart = dateStart,
    dateFinish = dateFinish,
    name = name,
    description = description
)
