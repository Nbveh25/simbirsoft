package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.create

sealed interface CreateTaskIntent {
    data class Submit(
        val name: String,
        val description: String,
        val startAtMillis: Long,
        val endAtMillis: Long
    ) : CreateTaskIntent
}
