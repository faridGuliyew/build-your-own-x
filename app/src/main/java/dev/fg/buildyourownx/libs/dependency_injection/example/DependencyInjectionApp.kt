package dev.fg.buildyourownx.libs.dependency_injection.example

import android.app.Application
import dev.fg.buildyourownx.libs.dependency_injection.injector.Inject
import dev.fg.buildyourownx.libs.dependency_injection.injector.InjectorImpl

class DependencyInjectionApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Inject.start(
            modules = listOf(
                appModule,
                otherModule
            )
        )
    }
}