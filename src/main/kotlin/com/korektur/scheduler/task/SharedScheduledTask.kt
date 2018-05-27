package com.korektur.scheduler.task

import com.korektur.scheduler.strategy.SchedulingStrategy
import java.util.concurrent.ConcurrentHashMap

public abstract class SharedScheduledTask(
        private val strategy: SchedulingStrategy,
        private val context: Map<String, Any> = ConcurrentHashMap()) {



    public abstract fun execute(): TaskExecutionResult

}