package ru.kazan.itis.bikmukhametov.simbirsoft.data.source

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskJsonDto(
    @SerialName("id") val id: Long,
    @SerialName("date_start") val dateStart: Long,
    @SerialName("date_finish") val dateFinish: Long,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String
)
