package com.korektur.scheduler.task

import com.korektur.scheduler.lock.TaskExecutionLock
import com.korektur.scheduler.strategy.SchedulingStrategy
import com.korektur.scheduler.task.TaskExecutionResult.COMPLETE
import java.util.concurrent.ExecutorService

class SimpleSharedScheduledTask(
        name: String,
        private val runnable: Runnable,
        schedulingStrategy: SchedulingStrategy,
        taskExecutionLock: TaskExecutionLock,
        executor: ExecutorService? = null) : SharedScheduledTask(name, schedulingStrategy,
        taskExecutionLock = taskExecutionLock, executorService = executor) {

    override fun execute(): TaskExecutionResult {
        if (taskExecutionLock.tryLock()) {
            runnable.run()
            taskExecutionLock.unlock()
        }
        return COMPLETE
    }
}