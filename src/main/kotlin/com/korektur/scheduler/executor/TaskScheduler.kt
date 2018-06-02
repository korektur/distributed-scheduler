package com.korektur.scheduler.executor

import com.korektur.scheduler.COROUTINES_ENABLED
import com.korektur.scheduler.DEFAULT_THREAD_POOL_SIZE
import com.korektur.scheduler.task.SharedScheduledTask
import com.korektur.scheduler.task.TaskExecutionResult
import java.time.Clock
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class TaskScheduler(private val clock: Clock) {

    public fun schedule(scheduledTask: SharedScheduledTask) {
        val schedulingStrategy = scheduledTask.strategy
        val scheduledTaskExecutorStrategy = ScheduledTaskExecutorStrategy(scheduledTask)
        scheduledTask.strategy.register(clock.instant())
        if (scheduledTask.executorService == null && COROUTINES_ENABLED) {
//            scheduledTask.executorService.submit(::execute)
        } else {
            val executor = scheduledTask.executorService ?: Executors.newScheduledThreadPool(DEFAULT_THREAD_POOL_SIZE)

            if (executor is ScheduledExecutorService) {

                val future = executor.schedule(scheduledTaskExecutorStrategy::execute,
                        schedulingStrategy.timeTillNextExecution(clock.instant())!!,
                        TimeUnit.MILLISECONDS)



            } else {
                val supplyAsync = CompletableFuture.supplyAsync(
                        scheduledTaskExecutorStrategy::execute as Supplier<TaskExecutionResult>, executor)
            }

        }

    }
}