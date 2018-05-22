package com.korektur.scheduler.strategy

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

class FixedDelayExecutionStrategy(val delay: Long,
                                  val timeUnit: TimeUnit = MILLISECONDS,
                                  val initialDelay: Long = 0L,
                                  val initialDelayTimeUnit: TimeUnit = MILLISECONDS) : BaseExecutionStrategy {
    override fun timeToNextExecution(): Long? {
        TODO("not implemented")
    }

}