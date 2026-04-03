package ru.kazan.itis.bikmukhametov.simbirsoft.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.kazan.itis.bikmukhametov.simbirsoft.presentation.create.CreateTaskViewModel
import ru.kazan.itis.bikmukhametov.simbirsoft.presentation.detail.TaskDetailViewModel
import ru.kazan.itis.bikmukhametov.simbirsoft.presentation.main.TaskListViewModel

val presentationModule = module {
    viewModel { TaskListViewModel(get()) }
    viewModel { TaskDetailViewModel(get()) }
    viewModel { CreateTaskViewModel(get()) }
}
