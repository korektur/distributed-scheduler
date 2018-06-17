package com.korektur.scheduler.lock

import com.korektur.scheduler.lock.BaseTaskExecutionLock.State.ACQUIRING
import com.korektur.scheduler.lock.BaseTaskExecutionLock.State.LOCKED
import com.korektur.scheduler.lock.BaseTaskExecutionLock.State.OPEN
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference

abstract class BaseTaskExecutionLock(protected val capacity: Int) : TaskExecutionLock {

    companion object {
        @JvmStatic
        private val LOG = LoggerFactory.getLogger(BaseTaskExecutionLock::class.java)
    }

    private enum class State {
        OPEN,
        ACQUIRING,
        LOCKED
    }

    private var state = AtomicReference<State>(OPEN)


    override fun tryLock(): Boolean {
        if (state.compareAndSet(OPEN, ACQUIRING)) {
            if (tryLockInternal()) {
                if (!state.compareAndSet(ACQUIRING, LOCKED)) {
                    LOG.error("inconsistent lock state, current state is ${state.get()}, " +
                            "when expected ${State.ACQUIRING}, forcibly updating it to ${State.LOCKED}")
                    state.set(LOCKED)
                }

                return true
            }
        }

        if (LOG.isDebugEnabled) {
            LOG.debug("failed to acquire lock $this")
        }

        return false
    }

    override fun unlock() {
        if (state.get() != LOCKED) {
            LOG.warn("unlocked called on lock in ${state.get()} state, expected state is ${State.LOCKED}")
            return
        }

        unlockInternal()
        if (!state.compareAndSet(LOCKED, OPEN)) {
            LOG.warn("unexpected state after unlock execution, expected , current state is ${state.get()}, cannot open lock")
        }
    }

    abstract fun tryLockInternal(): Boolean
    abstract fun unlockInternal()

    override fun toString(): String {
        return "BaseTaskExecutionLock(capacity=$capacity, state=$state)"
    }
}