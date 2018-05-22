package com.korektur.scheduler.strategy

interface BaseExecutionStrategy {

    fun timeToNextExecution(): Long?
}