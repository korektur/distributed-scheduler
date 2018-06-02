package com.korektur.scheduler

public val COROUTINES_ENABLED = System.getProperty("distributed-scheduler.coroutines.enabled", "true")!!.toBoolean()
public val DEFAULT_THREAD_POOL_SIZE = System.getProperties().getOrDefault("distributed-scheduler.coroutines.enabled",
        Runtime.getRuntime().availableProcessors()) as Int