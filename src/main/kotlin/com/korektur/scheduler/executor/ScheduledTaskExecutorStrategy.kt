package com.korektur.scheduler.executor

import com.korektur.scheduler.task.SharedScheduledTask
import com.korektur.scheduler.task.TaskExecutionResult
import com.korektur.scheduler.task.TaskExecutionResult.FAILED
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory
import java.time.Clock
import java.util.concurrent.Callable
import kotlin.coroutines.experimental.CoroutineContext

public open class ScheduledTaskExecutorStrategy {

    companion object {
        private val LOG = LoggerFactory.getLogger(ScheduledTaskExecutorStrategy::class.java)
    }

    public fun execute(sharedScheduledTask: SharedScheduledTask): TaskExecutionResult {
        try {
            if (LOG.isDebugEnabled) {
                LOG.debug("executing scheduledTask: ${sharedScheduledTask.name}")
            }

            val result = sharedScheduledTask.execute()

            if (LOG.isDebugEnabled) {
                LOG.debug("execution successfully finished: ${sharedScheduledTask.name}")
            }

            return result
        } catch (t: Throwable) {
            sharedScheduledTask.errorHandlers.forEach { it.handle(sharedScheduledTask.context, t) }
            return FAILED
        }
    }

}