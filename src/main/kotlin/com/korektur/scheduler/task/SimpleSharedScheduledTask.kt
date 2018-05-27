package com.korektur.scheduler.task

import com.korektur.scheduler.strategy.SchedulingStrategy
import com.korektur.scheduler.task.TaskExecutionResult.COMPLETE
import java.util.Objects

class SimpleSharedScheduledTask(
        private val runnable: Runnable,
        schedulingStrategy: SchedulingStrategy) : SharedScheduledTask(schedulingStrategy) {

    override fun execute(): TaskExecutionResult {
        runnable.run()
        return COMPLETE
    }
}