package com.korektur.scheduler.lock

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class H2BasedTaskExecutionLockTestL : H2BasedTest() {

    private lateinit var lock: H2BasedTaskExecutionLock

    @BeforeEach
    fun setUp() {
        createServer()
        lock = H2BasedTaskExecutionLock(2, dataSource, "public")
    }

    @Test
    fun initializationQuery() {
        lock.initialize()
    }
}