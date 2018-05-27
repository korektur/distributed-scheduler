package com.korektur.scheduler.strategy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit.MILLIS

class FixedDelaySchedulingStrategyTest {

    @Test
    fun testTimeTillNextExecutionNotRegistered() {
        val strategy = FixedDelaySchedulingStrategy(10000)
        assertNull(strategy.timeTillNextExecution(Instant.now()))
    }

    @Test
    fun testTimeTillNextExecutionAfterRegistration() {
        val strategy = FixedDelaySchedulingStrategy(1000, initialDelay = 1000)
        val currentTime = Instant.now()

        assertNull(strategy.timeTillNextExecution(Instant.now()))

        strategy.register(currentTime)

        assertEquals(1000L,  strategy.timeTillNextExecution(currentTime))
        assertEquals(0L,  strategy.timeTillNextExecution(currentTime.plus(1000, MILLIS)))
        assertEquals(0L,  strategy.timeTillNextExecution(currentTime.plus(2000, MILLIS)))
    }

    @Test
    fun testTimeTillNextExecutionAfterExecution() {
        val strategy = FixedDelaySchedulingStrategy(1000)
        val currentTime = Instant.now()

        assertNull(strategy.timeTillNextExecution(Instant.now()))

        strategy.register(currentTime)

        strategy.beforeExecution(currentTime.plus(500, MILLIS))
        strategy.afterExecution(currentTime.plus(1000, MILLIS))

        assertEquals(1000L,  strategy.timeTillNextExecution(currentTime.plus(1000, MILLIS)))
        assertEquals(0L,  strategy.timeTillNextExecution(currentTime.plus(2000, MILLIS)))
    }

}