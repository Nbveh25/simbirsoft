package ru.kazan.itis.bikmukhametov.simbirsoft.di

import org.koin.dsl.module
import ru.kazan.itis.bikmukhametov.simbirsoft.data.source.TaskSeedDataSource
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.service.impl.DefaultTaskService
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.service.TaskService
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.CreateTaskUseCase
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.impl.CreateTaskUseCaseImpl
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.GetDayHourSlotsUseCase
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.impl.GetDayHourSlotsUseCaseImpl
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.GetTaskByIdUseCase
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.usecase.impl.GetTaskByIdUseCaseImpl

val domainModule = module {
    factory<TaskService> {
        DefaultTaskService(
            taskRepository = get(),
            initialDataProvider = { get<TaskSeedDataSource>().loadTasks() }
        )
    }
    factory<GetDayHourSlotsUseCase> { GetDayHourSlotsUseCaseImpl(get()) }
    factory<GetTaskByIdUseCase> { GetTaskByIdUseCaseImpl(get()) }
    factory<CreateTaskUseCase> { CreateTaskUseCaseImpl(get()) }
}
