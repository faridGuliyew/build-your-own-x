# Lightweight Kotlin DI

A zero-code-generation Service Locator library built in purely Kotlin, similar to early days of Koin.

## ✨ Features

* **Zero Code Generation:** No KAPT/KSP overhead, doesn't affect build time.
* **Limited Android Support:** Native support for `ViewModel`.
* **Thread Safety:** Requesting dependencies in multi-threaded environment works without any issues.
* **Graph Safety:** Circular dependency detection prevents `StackOverflowError` crashes.
* **Qualifiers & Runtime Parameters:** Allows for distinguishing between multiple instances of the same type and pass dynamic parameters at runtime using `CreationExtras`.
* **KMP Support:** Core architecture is decoupled from Android, making it easily adaptable for Kotlin Multiplatform (KMP).

---

## 🚀 Quick Start

### 1. Define your Modules
Use the `module` DSL to declare your dependencies. You can define `single` (singletons), `factory` (new instance every time), and `viewModel` dependencies.

```kotlin
val appModule = module {
    // A singleton dependency
    single { UserInfo("system_id_001") }

    // A factory dependency requiring another dependency (UserInfo)
    factory { User("DefaultUser", "password123", get()) }

    // A named/qualified dependency
    single("AdminUser") { User("Admin", "admin_pass", get()) }

    // A ViewModel dependency with SavedStateHandle support
    viewModel { creationExtras -> 
        MyViewModel(creationExtras.savedStateHandle) 
    }
}
```

### 2. Initialize the Graph
Start the injector in your Application class by passing your modules.

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        Inject.start(
            modules = listOf(appModule, networkModule, databaseModule)
        )
    }
}
```

### 3. Inject Dependencies
Implement the Injectable interface in your target class (like an Activity or Fragment) to unlock the inject() delegates.

```kotlin
class MainActivity : ComponentActivity(), Injectable {
    
    // Inject a ViewModel (lazy)
    private val viewModel by injectViewModel<MyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inject standard dependencies
        val defaultUser = inject<User>()
        
        // Inject with a qualifier
        val adminUser = inject<User>("AdminUser")
        
        println("Logged in as: ${adminUser.username}")
    }
}
```

### 🛠 Advanced Usage
Runtime Parameters (CreationExtras)
Sometimes you need to pass dynamic data to a dependency at the exact moment of its creation. You can do this using CreationExtras.
Definition:
```kotlin
val userModule = module {
    factory("DynamicUser") { extras -> 
        val order = extras?.params?.get("ORDER") as? Int ?: 0
        User("Dynamic_$order", "pwd", get())
    }
}
```
Injection:
```kotlin
val user1 = inject<User>(
    qualifier = "DynamicUser", 
    creationExtras = CreationExtras(mapOf("ORDER" to 1))
)
```


### ViewModel SavedStateHandle
When declaring a viewModel, the library automatically provides a VMCreationExtras object containing the SavedStateHandle.
```kotlin
val vmModule = module {
    viewModel { extras -> 
        MyViewModel(
            repository = get(), 
            savedStateHandle = extras.savedStateHandle
        ) 
    }
}
```
