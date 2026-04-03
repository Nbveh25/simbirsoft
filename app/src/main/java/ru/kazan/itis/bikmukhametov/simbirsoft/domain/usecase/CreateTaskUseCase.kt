package ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase

fun interface CreateTaskUseCase {
    suspend operator fun invoke(
        name: String,
        description: String,
        startAtMillis: Long,
        endAtMillis: Long
    ): Long
}
