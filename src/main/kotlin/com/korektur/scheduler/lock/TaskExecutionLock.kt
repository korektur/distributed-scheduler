package com.korektur.scheduler.lock

/**
 * Interface for locking before task execution.
 */
interface TaskExecutionLock {

    /**
     * Method which tries to acquire the lock before task execution.
     *
     * @return true in case lock was acquired, false otherwise.
     */
    fun tryLock(): Boolean

    fun unlock()
}