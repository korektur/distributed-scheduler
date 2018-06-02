package com.korektur.scheduler.handler

interface ErrorHandler {

    fun handle(context: Map<String, Any>, throwable: Throwable)
}