package ru.kazan.itis.bikmukhametov.simbirsoft

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.kazan.itis.bikmukhametov.simbirsoft.di.dataModule
import ru.kazan.itis.bikmukhametov.simbirsoft.di.domainModule
import ru.kazan.itis.bikmukhametov.simbirsoft.di.presentationModule

class SimbirSoftApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SimbirSoftApp)
            modules(dataModule, domainModule, presentationModule)
        }
    }
}
