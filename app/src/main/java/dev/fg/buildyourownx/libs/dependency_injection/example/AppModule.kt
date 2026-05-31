package dev.fg.buildyourownx.libs.dependency_injection.example

import dev.fg.buildyourownx.libs.dependency_injection.convenience.module

val appModule = module {

    single("SingleUser") { User("single", "single_pwd", get()) }
    factory("FactoryUser") { User("factory", "factory_pwd with extra: ${it!!.params!!.get("ORDER") as Int}", get()) }
    factory { User("NoQualifierFactoryUser", "normal_pwd", get()) }
    viewModel { DependencyInjectionViewModel(it.savedStateHandle) }
}

val otherModule = module {
    single { UserInfo("single_id") }
}


class User(
    val username: String,
    val password: String,
    val info: UserInfo
)

class UserInfo(
    val id: String
)