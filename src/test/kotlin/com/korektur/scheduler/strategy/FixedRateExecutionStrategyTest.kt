package com.korektur.scheduler.strategy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit


class FixedRateExecutionStrategyTest {

    @Test
    fun testTimeTillNextExecutionNotRegistered() {
        val strategy = FixedRateExecutionStrategy(10000)
        Assertions.assertNull(strategy.timeTillNextExecution(Instant.now()))
    }

    @Test
    fun testTimeTillNextExecutionAfterRegistration() {
        val strategy = FixedRateExecutionStrategy(1000, initialDelay = 1000)
        val currentTime = Instant.now()

        Assertions.assertNull(strategy.timeTillNextExecution(Instant.now()))

        strategy.register(currentTime)
        Assertions.assertEquals(1000L, strategy.timeTillNextExecution(currentTime))
        Assertions.assertEquals(0L, strategy.timeTillNextExecution(currentTime.plus(1000, ChronoUnit.MILLIS)))
        Assertions.assertEquals(0L, strategy.timeTillNextExecution(currentTime.plus(2000, ChronoUnit.MILLIS)))
    }

    @Test
    fun testTimeTillNextExecutionAfterExecution() {
        val strategy = FixedRateExecutionStrategy(1000)
        val currentTime = Instant.now()

        Assertions.assertNull(strategy.timeTillNextExecution(Instant.now()))

        strategy.register(currentTime)

        strategy.beforeExecution(currentTime.plus(500, ChronoUnit.MILLIS))
        strategy.afterExecution(currentTime.plus(1000, ChronoUnit.MILLIS))

        Assertions.assertEquals(1000L, strategy.timeTillNextExecution(currentTime.plus(500, ChronoUnit.MILLIS)))
        Assertions.assertEquals(500L, strategy.timeTillNextExecution(currentTime.plus(1000, ChronoUnit.MILLIS)))
        Assertions.assertEquals(0L, strategy.timeTillNextExecution(currentTime.plus(1500, ChronoUnit.MILLIS)))
    }
}