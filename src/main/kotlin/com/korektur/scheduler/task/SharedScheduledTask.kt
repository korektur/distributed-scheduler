package com.korektur.scheduler.task

import com.korektur.scheduler.handler.ErrorHandler
import com.korektur.scheduler.lock.TaskExecutionLock
import com.korektur.scheduler.strategy.SchedulingStrategy
import java.util.Collections.unmodifiableList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

public abstract class SharedScheduledTask(
        val name: String,
        open val strategy: SchedulingStrategy,
        val taskExecutionLock: TaskExecutionLock,
        open val context: Map<String, Any> = ConcurrentHashMap(),
        val executorService: ExecutorService? = null) {

    private val _errorHandlers: MutableList<ErrorHandler> = ArrayList()
    open val errorHandlers: List<ErrorHandler>
        get() = unmodifiableList(_errorHandlers)

    public fun addErrorHandler(handler: ErrorHandler) {
        _errorHandlers.add(handler)
    }

    public abstract fun execute(): TaskExecutionResult

}