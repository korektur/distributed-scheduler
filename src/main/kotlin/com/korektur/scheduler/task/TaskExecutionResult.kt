package com.korektur.scheduler.task

enum class TaskExecutionResult {
    /**
     * Task successfully competed, next execution will happen according to specified execution strategy
     */
    COMPLETE,
    /**
     * Task completed with error, immediate retry required
     */
    RETRY,
    /**
     * Task failed, no further executions of this task are allowed, unschedule task
     */
    FAILED,
    /**
     * Task successfully competed, no further executions required, unschedule task
     */
    FINISHED;
}