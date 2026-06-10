# MyCoroutines
An education-focused, low-level implementation of Kotlin Coroutines from scratch. 
It is built for learning purposes to demystify Kotlin's continuation mechanics, CoroutineContext manipulation, structured concurrency, and cooperative cancellation boundaries.

This project recreates core components of `kotlinx.coroutines` (such as `launch`, `withContext`, `delay`, and custom dispatchers) using standard library primitives (`createCoroutine`, `suspendCoroutine`).

---

## 📌 Project Overview

## 🔥 Key Implementation

* **Continuation Mechanics**
* **Structured Concurrency**
* **Non-Blocking Delay**
* **Context Manipulation**

---

## ⚠️ Important Implementation Shortcuts

* **No Thread-safety:** This introduces race conditions when multiple concurrent threads read and write states simultaneously, skipping necessary Compare-And-Swap (CAS) guardrails
* **Unchecked Elements:** The system assumes key context elements are always present during suspension, and throws NPE if not present
* **Thread contention:** Managing active jobs via a `ReentrantReadWriteLock` introduces high lock contention under heavy concurrent load
* **Limited functionality::** I built only core concepts, skipping over many useful functionalities
---

## 🏗️ Architectural Shortcuts

###  `withContext`
In the standard library, `withContext` optimizes execution by altering the underlying dispatcher directly without allocating an independent job. 
In contrast, this implementation relies on a heavier architectural shortcut (which creates a new job to achieve it):
```kotlin
suspend fun withContext(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit) {
    val scope = CoroutineScope(currentCoroutineContext())
    scope.launch(context = context, block = block)?.join()
}
```

## 🧪 Concrete Demonstration & Testing Pipeline
You can track execution traces, thread switching, and cancellation behavior inside MyCoroutinesActivity.kt


🎓 Learning Takeaways
Continuations are Callbacks: At their core, coroutines transform complex, sequential code blocks into efficient, stateful callbacks with the help of kotlin compiler magic
Context is a Type-Safe Map: The CoroutineContext is just a special map, nothing more
Structured Concurrency is Explicit Tree Maintenance: Managing coroutines is maintaining parent-child tree graphs, ensuring lifetimes are strictly bounded and cleaned up cleanly.
"""
