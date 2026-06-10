package dev.fg.buildyourownx.libs.my_coroutines.misc.extensions

import dev.fg.buildyourownx.libs.my_coroutines.context_elements.job.Job

fun Job.ensureActive() {
    if (!isActive) throw Exception("Job is cancelled!")
}