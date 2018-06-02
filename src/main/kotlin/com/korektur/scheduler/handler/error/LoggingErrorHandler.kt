package com.korektur.scheduler.handler.error

import com.korektur.scheduler.handler.ErrorHandler
import org.slf4j.LoggerFactory

class LoggingErrorHandler: ErrorHandler{

    companion object {
        private val LOG = LoggerFactory.getLogger(LoggingErrorHandler::class.java)
    }

    override fun handle(context: Map<String, Any>, throwable: Throwable) {
        LOG.error("uncaught exception during task execution: ${throwable.localizedMessage}", throwable)
    }

}