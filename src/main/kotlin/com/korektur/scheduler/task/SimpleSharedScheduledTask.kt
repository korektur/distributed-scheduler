package com.korektur.scheduler.task

import com.korektur.scheduler.strategy.SchedulingStrategy
import com.korektur.scheduler.task.TaskExecutionResult.COMPLETE
import java.util.concurrent.ExecutorService

class SimpleSharedScheduledTask(
        name: String,
        private val runnable: Runnable,
        schedulingStrategy: SchedulingStrategy,
        executor: ExecutorService? = null) : SharedScheduledTask(name, schedulingStrategy, executorService = executor) {

    override fun execute(): TaskExecutionResult {
        runnable.run()
        return COMPLETE
    }
}