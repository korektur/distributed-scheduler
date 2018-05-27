package com.korektur.scheduler.strategy

import java.time.Instant

interface SchedulingStrategy {
    /**
     * Registers strategy for execution.
     * Should be when registering task for execution.
     */
    fun register(currentTime: Instant)

    /**
     * This method is called before task is executed
     */
    fun beforeExecution(currentTime: Instant) {
        //do nothing by default
    }

    /**
     * This method is called after task is executed
     */
    fun afterExecution(currentTime: Instant) {
        //do nothing by default
    }

    /**
     * @param currentTime current time
     * @return time left until next execution in milliseconds, null if strategy wasn't registered
     */
    fun timeTillNextExecution(currentTime: Instant): Long?
}