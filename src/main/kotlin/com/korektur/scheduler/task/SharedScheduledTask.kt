package com.korektur.scheduler.task

import java.util.concurrent.Callable

data class SharedScheduledTask internal constructor(val task: Callable<Any>) {

}