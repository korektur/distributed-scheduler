package com.korektur.scheduler.executor

import com.korektur.scheduler.strategy.FixedDelaySchedulingStrategy
import com.korektur.scheduler.task.SharedScheduledTask
import com.korektur.scheduler.task.TaskExecutionResult
import com.korektur.scheduler.task.TaskExecutionResult.FINISHED
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.spy
import java.time.Clock
import java.time.Instant
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TaskSchedulerTest {

    companion object {
        private val timestampRegister = Instant.now()
        private val timestampBeforeExecution = Instant.now().plusMillis(1000)
        private val timestampAfterExecution = Instant.now().plusMillis(2000)
    }

    private lateinit var clock: Clock
    private lateinit var taskScheduler: TaskScheduler

    @BeforeEach
    internal fun setUp() {
        clock = mock()
        whenever(clock.instant()).thenReturn(timestampRegister, timestampBeforeExecution)

        taskScheduler = TaskScheduler(clock)
        taskScheduler.init()
    }

    @AfterEach
    internal fun tearDown() {
        taskScheduler.stop()
    }

    @Test
    internal fun testAddSimpleTaskWithoutExecutor() {
        val strategy = spy(FixedDelaySchedulingStrategy(100, initialDelay = 200))
        val latch = CountDownLatch(1)
        val awaitLatch = CountDownLatch(1)
        val task = object : SharedScheduledTask("test", strategy) {
            override fun execute(): TaskExecutionResult {
                awaitLatch.countDown()
                latch.await()
                return FINISHED
            }

        }

        taskScheduler.addTask(task)

        assertTrue(awaitLatch.await(10000, TimeUnit.MILLISECONDS))
        verify(strategy, times(1)).register(timestampRegister)
        verify(strategy, times(1)).beforeExecution(timestampBeforeExecution)
        verify(strategy, never()).afterExecution(any())

        whenever(clock.instant()).thenReturn(timestampAfterExecution)

        latch.countDown()

        //TODO: replace with decent waiting
        Thread.sleep(100)

        verify(strategy, times(1)).afterExecution(timestampAfterExecution)
    }
}