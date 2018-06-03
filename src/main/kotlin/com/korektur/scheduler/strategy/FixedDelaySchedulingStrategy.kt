package com.korektur.scheduler.strategy

import java.time.Instant
import java.time.temporal.ChronoUnit.MILLIS
import java.time.temporal.TemporalUnit

/**
 * Strategy that calculates next execution time based on fixed delay since end of the last execution.
 * @param delay fixed period between the end of the last invocation and the start of the next.
 * @param timeUnit time unit for the delay
 * @param initialDelay Number of milliseconds to wait before the first execution after task was registered
 * @param initialDelayTimeUnit time unit for the initial delay
 */
open class FixedDelaySchedulingStrategy(private val delay: Long,
                                   private val timeUnit: TemporalUnit = MILLIS,
                                   initialDelay: Long = 0L,
                                   initialDelayTimeUnit: TemporalUnit = MILLIS) : BaseSchedulingStrategy(initialDelay, initialDelayTimeUnit) {

    override fun afterExecution(currentTime: Instant) {
        lastExecutionTime = currentTime
        nextExecutionExpectedTime = currentTime.plus(delay, timeUnit)
    }
}