package com.korektur.scheduler.strategy

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS

class FixedDelayExecutionStrategyTest {

    @Test
    fun testTimeTillNextExecutionNotRegistered() {
        val strategy = FixedDelayExecutionStrategy(10, DAYS)
        assertNull(strategy.timeTillNextExecution(Instant.now()))
    }
}