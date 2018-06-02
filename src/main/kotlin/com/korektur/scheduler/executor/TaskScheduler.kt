package com.korektur.scheduler.executor

import com.korektur.scheduler.COROUTINES_ENABLED
import com.korektur.scheduler.DEFAULT_THREAD_POOL_SIZE
import com.korektur.scheduler.task.SharedScheduledTask
import com.korektur.scheduler.task.TaskExecutionResult
import com.korektur.scheduler.task.TaskExecutionResult.COMPLETE
import com.korektur.scheduler.task.TaskExecutionResult.FAILED
import com.korektur.scheduler.task.TaskExecutionResult.FINISHED
import com.korektur.scheduler.task.TaskExecutionResult.RETRY
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.time.Clock
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class TaskScheduler(private val clock: Clock,
                    private val executorStrategy: ScheduledTaskExecutorStrategy = ScheduledTaskExecutorStrategy()) {


    private val taskQueue = PriorityBlockingQueue<SharedScheduledTask>()

    private lateinit var workerThread: Job

    public fun init() {
        workerThread = launch {
            while (true) {
                val nextTask = taskQueue.take()
                schedule(nextTask)
                //TODO: handle errors in scheduled tasks
            }
        }
    }

    public fun stop() {
        runBlocking {
            workerThread.cancelAndJoin()
        }
    }

    private fun schedule(task: SharedScheduledTask) {
        val schedulingStrategy = task.strategy
        task.strategy.register(clock.instant())
        if (task.executorService == null && COROUTINES_ENABLED) {
            async {
                delay(task.strategy.timeTillNextExecution(clock.instant())!!)
                task.strategy.beforeExecution(clock.instant())
                val result = executorStrategy.execute(task)
                schedulingStrategy.afterExecution(clock.instant())
                handleResult(task, result)
                result
            }
        } else {
            val executor = task.executorService ?: Executors.newScheduledThreadPool(DEFAULT_THREAD_POOL_SIZE)
            val future: CompletableFuture<TaskExecutionResult>
            if (executor is ScheduledExecutorService) {
                future = CompletableFuture()
                executor.schedule(schedulerTaskWrapper(task),
                        schedulingStrategy.timeTillNextExecution(clock.instant())!!,
                        TimeUnit.MILLISECONDS)
            } else {
                future = CompletableFuture.supplyAsync(taskWrapper(task), executor)
            }
            future
                    .thenApplyAsync {
                        task.strategy.afterExecution(clock.instant())
                        it
                    }
                    .thenApplyAsync { handleResult(task, it) }


        }
    }

    private fun taskWrapper(task: SharedScheduledTask): Supplier<TaskExecutionResult> {
        return Supplier {
            Thread.sleep(task.strategy.timeTillNextExecution(clock.instant())!!)

            task.strategy.beforeExecution(clock.instant())
            executorStrategy.execute(task)
        }
    }

    private fun schedulerTaskWrapper(task: SharedScheduledTask): Callable<TaskExecutionResult> {
        return Callable {
            task.strategy.beforeExecution(clock.instant())
            executorStrategy.execute(task)
        }
    }

    private fun handleResult(task: SharedScheduledTask, result: TaskExecutionResult) {
        when (result) {
            COMPLETE -> taskQueue.add(task)
            RETRY -> launch { schedule(task) }
            FINISHED -> return
            FAILED -> return
        }
    }
}