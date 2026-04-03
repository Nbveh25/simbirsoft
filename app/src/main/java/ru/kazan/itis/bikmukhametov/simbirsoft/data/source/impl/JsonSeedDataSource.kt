package ru.kazan.itis.bikmukhametov.simbirsoft.data.source.impl

import android.content.Context
import kotlinx.serialization.json.Json
import ru.kazan.itis.bikmukhametov.simbirsoft.data.source.TaskJsonDto
import ru.kazan.itis.bikmukhametov.simbirsoft.data.source.TaskSeedDataSource
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem

class JsonSeedDataSource(
    private val context: Context,
    private val jsonParser: Json = Json { ignoreUnknownKeys = true }
) : TaskSeedDataSource {
    override fun loadTasks(): List<TaskItem> {
        val json = context.assets.open(FILE_NAME).bufferedReader().use { it.readText() }
        val rawItems = jsonParser.decodeFromString<List<TaskJsonDto>>(json)
        return rawItems.map { dto ->
            TaskItem(
                id = dto.id,
                dateStart = dto.dateStart,
                dateFinish = dto.dateFinish,
                name = dto.name,
                description = dto.description
            )
        }
    }

    private companion object {
        private const val FILE_NAME = "tasks.json"
    }
}
