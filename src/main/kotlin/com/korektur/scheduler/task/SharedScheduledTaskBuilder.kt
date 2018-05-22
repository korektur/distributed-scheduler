package com.korektur.scheduler.task

import java.util.Objects
import java.util.concurrent.Callable

class SharedScheduledTaskBuilder {

    private var task: Callable<Any>? = null
    private var context = HashMap<String, Any>()

    public fun setTask(task: Callable<Any>): SharedScheduledTaskBuilder {
        this.task = task
        return this
    }

    public fun setTask(task: Runnable): SharedScheduledTaskBuilder {
        this.task = Callable { task.run() }
        return this
    }

    public fun addContextValue(key: String, value: Any): SharedScheduledTaskBuilder {
        context[key] = value
        return this
    }

    public fun build(): SharedScheduledTask {
        Objects.requireNonNull(task, "task should be set")
        return SharedScheduledTask(task!!, context)
    }
}