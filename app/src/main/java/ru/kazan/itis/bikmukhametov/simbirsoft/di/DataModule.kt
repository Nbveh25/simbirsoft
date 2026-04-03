package ru.kazan.itis.bikmukhametov.simbirsoft.di

import androidx.room.Room
import org.koin.dsl.module
import ru.kazan.itis.bikmukhametov.simbirsoft.data.local.AppDatabase
import ru.kazan.itis.bikmukhametov.simbirsoft.data.repository.RoomTaskRepository
import ru.kazan.itis.bikmukhametov.simbirsoft.data.source.impl.JsonSeedDataSource
import ru.kazan.itis.bikmukhametov.simbirsoft.data.source.TaskSeedDataSource
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.repository.TaskRepository

val dataModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "planner.db"
        ).build()
    }

    single { get<AppDatabase>().taskDao() }

    factory<TaskSeedDataSource> { JsonSeedDataSource(get()) }

    single<TaskRepository> { RoomTaskRepository(get()) }
}
