package com.korektur.scheduler.executor

import com.korektur.scheduler.COROUTINES_ENABLED
import com.korektur.scheduler.DEFAULT_THREAD_POOL_SIZE
import com.korektur.scheduler.task.SharedScheduledTask
import com.korektur.scheduler.task.TaskExecutionResult
import com.korektur.scheduler.task.TaskExecutionResult.COMPLETE
import com.korektur.scheduler.task.TaskExecutionResult.FAILED
import com.korektur.scheduler.task.TaskExecutionResult.FINISHED
import com.korektur.scheduler.task.TaskExecutionResult.RETRY
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newCoroutineContext
import kotlinx.coroutines.experimental.runBlocking
import org.slf4j.LoggerFactory
import java.time.Clock
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Supplier
import kotlin.coroutines.experimental.CoroutineContext

class TaskScheduler(private val clock: Clock,
                    private val executorStrategy: ScheduledTaskExecutorStrategy = ScheduledTaskExecutorStrategy()) {

    companion object {
        @JvmStatic
        private val LOG = LoggerFactory.getLogger(TaskScheduler::class.java)
    }

    private val taskQueue = PriorityBlockingQueue<SharedScheduledTask>(11, this::taskComparator)

    private lateinit var workerThread: Job

    public fun init() {
        if (LOG.isDebugEnabled) {
            LOG.debug("initializing task consumer thread for scheduler")
        }

        workerThread = launch {
            while (isActive) {
                val nextTask = taskQueue.poll(100, TimeUnit.MILLISECONDS)
                if (nextTask != null) {
                    async { schedule(nextTask) }
                }
                //TODO: handle errors in scheduled tasks
            }
        }
    }

    public fun stop() {
        if (LOG.isDebugEnabled) {
            LOG.debug("stopping task consumer")
        }

        runBlocking {
            workerThread.cancelAndJoin()
        }
    }

    public fun addTask(task: SharedScheduledTask) {
        taskQueue.add(task)
    }

    private suspend fun schedule(task: SharedScheduledTask) {
        val schedulingStrategy = task.strategy
        task.strategy.register(clock.instant())
        if (task.executorService == null && COROUTINES_ENABLED) {
            if (LOG.isDebugEnabled) {
                LOG.debug("scheduling task with name ${task.name} using coroutine execution strategy")
            }

            async {
                var timeLeft = task.strategy.timeTillNextExecution(clock.instant())!!
                while (timeLeft > 0) {
                    delay(timeLeft)
                    timeLeft = task.strategy.timeTillNextExecution(clock.instant())!!
                }

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

    private fun taskComparator(first: SharedScheduledTask, second: SharedScheduledTask): Int {
        val currentTime = clock.instant()
        //comparing by time is ok since its linear and the result of comparison between two tasks will always be the same.
        return first.strategy.timeTillNextExecution(currentTime)!!.compareTo(
                second.strategy.timeTillNextExecution(currentTime)!!)
    }

    private fun taskWrapper(task: SharedScheduledTask): Supplier<TaskExecutionResult> {
        return Supplier {
            var timeLeft = task.strategy.timeTillNextExecution(clock.instant())!!
            while (timeLeft > 0) {
                Thread.sleep(timeLeft)
                timeLeft = task.strategy.timeTillNextExecution(clock.instant())!!
            }

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