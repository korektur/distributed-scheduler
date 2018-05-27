package com.korektur.scheduler.executor

import com.korektur.scheduler.task.SharedScheduledTask
import org.slf4j.LoggerFactory

public class ScheduledTaskExecutor() {

    companion object {
        private val LOG = LoggerFactory.getLogger(ScheduledTaskExecutor::class.java)
    }

    public fun executeNext(sharedScheduledTask: SharedScheduledTask) {
        try {
            sharedScheduledTask.execute()
        } catch (e: Throwable) {
            LOG.error("uncaught exception during task execution: ${e.localizedMessage}", e)

        }
    }

}