package com.korektur.scheduler.strategy

import java.time.Instant
import java.time.temporal.ChronoUnit.MILLIS
import java.time.temporal.TemporalUnit

/**
 * Strategy that calculates next execution time based on fixed delay since start of the last execution.
 * @param rate fixed period between the start of the last invocation and the start of the next.
 * @param timeUnit time unit for the rate
 * @param initialDelay Number of milliseconds to wait before the first execution after task was registered
 * @param initialDelayTimeUnit time unit for the initial delay
 */
open class FixedRateSchedulingStrategy(private val rate: Long,
                                  private val timeUnit: TemporalUnit = MILLIS,
                                  initialDelay: Long = 0L,
                                  initialDelayTimeUnit: TemporalUnit = MILLIS) : BaseSchedulingStrategy(initialDelay, initialDelayTimeUnit) {

    override fun beforeExecution(currentTime: Instant) {
        lastExecutionTime = currentTime
        nextExecutionExpectedTime = currentTime.plus(rate, timeUnit)
    }
}