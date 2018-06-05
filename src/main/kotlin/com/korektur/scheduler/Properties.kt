package com.korektur.scheduler

//TODO remove all properties
public val COROUTINES_ENABLED = System.getProperty("distributed-scheduler.coroutines.enabled", "true")!!.toBoolean()
public val DEFAULT_THREAD_POOL_SIZE = System.getProperties().getOrDefault("distributed-scheduler.thread.pool",
        Runtime.getRuntime().availableProcessors()) as Int