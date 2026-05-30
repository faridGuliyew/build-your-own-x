package dev.fg.buildyourownx.libs.dependency_injection.example

import dev.fg.buildyourownx.libs.dependency_injection.module

val appModule = module {

    single("SingleUser") { User("admin", "admin_pwd", get()) }
    factory("FactoryUser") { User("normal", "normal_pwd", get()) }
    factory { User("normal", "normal_pwd", get()) }
}

val otherModule = module {
    single { UserInfo("general id") }
}


class User(
    val username: String,
    val password: String,
    val info: UserInfo
)

class UserInfo(
    val id: String
)