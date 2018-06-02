package com.korektur.scheduler.task

import com.korektur.scheduler.strategy.SchedulingStrategy
import com.korektur.scheduler.task.TaskExecutionResult.COMPLETE

class SimpleSharedScheduledTask(
        name: String,
        private val runnable: Runnable,
        schedulingStrategy: SchedulingStrategy) : SharedScheduledTask(name, schedulingStrategy) {

    override fun execute(): TaskExecutionResult {
        runnable.run()
        return COMPLETE
    }
}