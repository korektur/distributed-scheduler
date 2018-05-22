package com.korektur.scheduler.strategy

import java.util.TimeZone

class CronBasedExecutionStrategy(val cronExpression: String,
                                 val timeZone: TimeZone) : BaseExecutionStrategy {
    override fun timeToNextExecution(): Long? {
        TODO("not implemented")
    }

}