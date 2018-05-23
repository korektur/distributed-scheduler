package com.korektur.scheduler.strategy

import org.slf4j.LoggerFactory
import java.lang.Long.max
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit.MILLIS
import java.time.temporal.TemporalUnit

abstract class BaseExecutionStrategy(private val initialDelay: Long = 0L,
                                     private val initialDelayTimeUnit: TemporalUnit = MILLIS) {

    companion object {
        private val LOG = LoggerFactory.getLogger(BaseExecutionStrategy::class.java)
    }

    @Volatile
    protected var lastExecutionTime: Instant? = null
    @Volatile
    protected var nextExecutionExpectedTime: Instant? = null
    @Volatile
    private var registered = false

    public fun register(currentTime: Instant) {
        if (!registered) {
            synchronized(this) {
                if (!registered) {
                    registered = true
                    nextExecutionExpectedTime = currentTime.plus(initialDelay, initialDelayTimeUnit)
                }
            }
        }
    }

    /**
     * This method is called before task is executed
     */
    public open fun beforeExecution(currentTime: Instant) {
        //do nothing
    }

    /**
     * This method is called after task is executed
     */
    public open fun afterExecution(currentTime: Instant) {
        //do nothing
    }

    /**
     * @param currentTime current time
     * @return time left until next execution in milliseconds, null if strategy wasn't registered
     */
    public fun timeTillNextExecution(currentTime: Instant): Long? {
        if (!registered) {
            if (LOG.isDebugEnabled) {
                LOG.debug("timeTillInitialDelayPassed check called on unregistered strategy")
            }
            return null
        }

        return Duration.between(nextExecutionExpectedTime, currentTime).abs().toMillis()
    }
}