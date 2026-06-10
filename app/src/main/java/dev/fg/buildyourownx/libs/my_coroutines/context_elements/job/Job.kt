package dev.fg.buildyourownx.libs.my_coroutines.context_elements.job

import java.util.LinkedList
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class Job(val name: String?, val parent: Job?) : AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<Job>

    // Children
    private val childrenRwl = ReentrantReadWriteLock()
    val children: LinkedList<Job> = LinkedList()
    // Detach listeners
    val detachListeners: ConcurrentLinkedQueue<() -> Unit> = ConcurrentLinkedQueue()

    @Volatile
    var state = STATE.ACTIVE

    val isActive get() = state.acceptsChildren

    fun attachChild(child: Job): Boolean {
        // If active, attach it (This whole thing must be atomic, use CAS for atomicity to make sure it is still active after a check)
        if (!state.acceptsChildren) {
//            println("Could not attach child: ${child.hashCode()} to parent: ${hashCode()} with state $state")
            return false
        }
        childrenRwl.write {
            children.add(child)
        }
        return true
    }

    private fun detachChild(child: Job) {
        childrenRwl.write {
            children.remove(child)
        }
    }

    fun cancel() {
        state = STATE.CANCELLING
        if (childrenRwl.read { children.isEmpty() }) {
            state = STATE.CANCELLED
//            println("$name is CANCELLED and removed from the child list of parent: ${parent?.name}")
            parent?.detachChild(this)
            parent?.cancel()
            cleanUp()
        } else {
//            println("$name is CANCELLING. Calls cancel() on all children: ${children.map { it.name }}")
            val snapshot = childrenRwl.read { children.toList() }
            for (child in snapshot) {
                child.cancel()
            }
        }
    }

    fun complete(isSelf: Boolean) {
        if (isSelf) {
            state = STATE.COMPLETING
        }

        if (childrenRwl.read { children.isEmpty() } && state == STATE.COMPLETING) {
            state = STATE.COMPLETED
//            println("$name is COMPLETED and removed from the child list of parent: ${parent?.name}")
            parent?.detachChild(this)
            parent?.complete(false)
            cleanUp()
        }
    }

    fun invokeOnDetach(block: () -> Unit) {
        detachListeners.offer(block)
    }

    private fun cleanUp() {
        while (detachListeners.isNotEmpty()) {
            detachListeners.poll()?.invoke()
        }
    }

    enum class STATE(val acceptsChildren: Boolean) {
        ACTIVE(acceptsChildren = true), // Job is active, didn't finish its own execution yet
        COMPLETING(acceptsChildren = true), // Job is active, finished its own execution, but there are one or more active children, waiting
        COMPLETED(acceptsChildren = false), // Job is not active anymore, finished its execution + all children. Completed successfully + notified parent
        CANCELLING(acceptsChildren = false), // Job itself is cancelled(crashed), or any of its children is cancelled(crashed), and is currently propagating cancellation below
        CANCELLED(acceptsChildren = false) // Job itself is cancelled + all of its children. Completed unsuccessfully + notified parent
    }
}