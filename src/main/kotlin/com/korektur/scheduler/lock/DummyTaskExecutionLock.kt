package com.korektur.scheduler.lock

/**
 * Lock implementation which always permits task execution
 */
class DummyTaskExecutionLock : TaskExecutionLock {

    override fun tryLock(): Boolean {
        return true
    }

    override fun unlock() {
        //do nothing as there is no real lock in this implementation
    }

}