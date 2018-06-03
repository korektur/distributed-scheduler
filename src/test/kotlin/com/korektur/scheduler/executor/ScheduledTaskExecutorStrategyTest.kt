package com.korektur.scheduler.executor

import com.korektur.scheduler.handler.ErrorHandler
import com.korektur.scheduler.task.SharedScheduledTask
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScheduledTaskExecutorStrategyTest {

    private lateinit var scheduledTask: SharedScheduledTask

    @BeforeEach
    internal fun setUp() {
        scheduledTask = mock {
            on { context } doReturn emptyMap()
        }
    }

    @Test
    fun testExecuteNextSuccessfulExecution() {
        val executor = ScheduledTaskExecutorStrategy()

        runBlocking { executor.execute(scheduledTask) }

        verify(scheduledTask, times(1)).execute()
    }

    @Test
    fun testExecuteNextFailedExecution() {
        val executor = ScheduledTaskExecutorStrategy()
        val errorHandlers = listOf<ErrorHandler>(mock(), mock())
        val exception = RuntimeException()
        whenever(scheduledTask.execute()) doThrow exception
        whenever(scheduledTask.errorHandlers) doReturn errorHandlers

        runBlocking { executor.execute(scheduledTask) }

        verify(scheduledTask, times(1)).execute()
        errorHandlers.forEach { verify(it).handle(emptyMap(), exception) }
    }
}