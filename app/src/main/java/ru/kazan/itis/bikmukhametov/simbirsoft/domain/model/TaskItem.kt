package ru.kazan.itis.bikmukhametov.simbirsoft.domain.model

data class TaskItem(
    val id: Long,
    val dateStart: Long,
    val dateFinish: Long,
    val name: String,
    val description: String
)
