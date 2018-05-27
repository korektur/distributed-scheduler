package com.korektur.scheduler.strategy

import org.slf4j.LoggerFactory
import java.lang.Long.max
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit.MILLIS
import java.time.temporal.TemporalUnit

abstract class BaseSchedulingStrategy(private val initialDelay: Long = 0L,
                                      private val initialDelayTimeUnit: TemporalUnit = MILLIS): SchedulingStrategy {

    companion object {
        private val LOG = LoggerFactory.getLogger(BaseSchedulingStrategy::class.java)
    }

    @Volatile
    protected var lastExecutionTime: Instant? = null
    @Volatile
    protected var nextExecutionExpectedTime: Instant? = null
    @Volatile
    private var registered = false

    public override fun register(currentTime: Instant) {
        if (!registered) {
            synchronized(this) {
                if (!registered) {
                    registered = true
                    nextExecutionExpectedTime = currentTime.plus(initialDelay, initialDelayTimeUnit)
                }
            }
        }
    }

    public override fun timeTillNextExecution(currentTime: Instant): Long? {
        if (!registered) {
            if (LOG.isDebugEnabled) {
                LOG.debug("timeTillInitialDelayPassed check called on unregistered strategy")
            }
            return null
        }

        return max(Duration.between(currentTime, nextExecutionExpectedTime).toMillis(), 0)
    }
}