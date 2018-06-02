package com.korektur.scheduler.executor

import com.korektur.scheduler.COROUTINES_ENABLED
import com.korektur.scheduler.DEFAULT_THREAD_POOL_SIZE
import com.korektur.scheduler.task.SharedScheduledTask
import com.korektur.scheduler.task.TaskExecutionResult
import java.time.Clock
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class TaskScheduler(private val clock: Clock,
                    private val executorStrategy: ScheduledTaskExecutorStrategy = ScheduledTaskExecutorStrategy()) {

    public fun schedule(scheduledTask: SharedScheduledTask) {
        val schedulingStrategy = scheduledTask.strategy
        scheduledTask.strategy.register(clock.instant())
        if (scheduledTask.executorService == null && COROUTINES_ENABLED) {
            TODO("implement")
        } else {
            val executor = scheduledTask.executorService ?: Executors.newScheduledThreadPool(DEFAULT_THREAD_POOL_SIZE)
            val future: CompletableFuture<TaskExecutionResult>
            if (executor is ScheduledExecutorService) {
                future = CompletableFuture()
                executor.schedule(schedulerTaskWrapper(scheduledTask, future),
                        schedulingStrategy.timeTillNextExecution(clock.instant())!!,
                        TimeUnit.MILLISECONDS)
            } else {
                future = CompletableFuture.supplyAsync(taskWrapper(scheduledTask), executor)
            }
            future.thenApplyAsync { TODO("implement") }
        }
    }

    private fun taskWrapper(task: SharedScheduledTask): Supplier<TaskExecutionResult> {
        return Supplier {

            Thread.sleep(task.strategy.timeTillNextExecution(clock.instant())!!)
            executorStrategy.execute(task)
        }
    }

    private fun schedulerTaskWrapper(task: SharedScheduledTask,
                                     future: CompletableFuture<TaskExecutionResult>): Callable<TaskExecutionResult> {
        return Callable {
            val result = executorStrategy.execute(task)
            future.complete(result)
            result
        }
    }
}