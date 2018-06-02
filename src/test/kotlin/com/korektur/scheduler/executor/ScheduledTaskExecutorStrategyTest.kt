package com.korektur.scheduler.executor

import com.korektur.scheduler.handler.ErrorHandler
import com.korektur.scheduler.task.SharedScheduledTask
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.only
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock

class ScheduledTaskExecutorStrategyTest {

    private lateinit var scheduledTask: SharedScheduledTask

    @BeforeEach
    internal fun setUp() {
        scheduledTask = mock {
            whenever(scheduledTask.context) doReturn emptyMap()
        }
    }

    @Test
    fun testExecuteNextSuccessfulExecution() {
        val executor = ScheduledTaskExecutorStrategy(scheduledTask)

        executor.execute()

        verify(scheduledTask, only()).execute()
    }

    @Test
    fun testExecuteNextFailedExecution() {
        val executor = ScheduledTaskExecutorStrategy(scheduledTask)
        val errorHandlers = listOf<ErrorHandler>(mock(), mock())
        val exception = RuntimeException()
        whenever(scheduledTask.execute()) doThrow exception
        whenever(scheduledTask.errorHandlers) doReturn errorHandlers

        executor.execute()

        verify(scheduledTask, only()).execute()
        scheduledTask.
        errorHandlers.forEach { verify(it).handle(emptyMap(), exception) }
    }
}