package com.korektur.scheduler.lock

import org.slf4j.LoggerFactory
import javax.sql.DataSource
import org.apache.commons.dbutils.QueryRunner
import org.intellij.lang.annotations.Language

//TODO: check with both autocommit = false/true
abstract class SqlBasedTaskExecutionLock(capacity: Int,
                                         protected val tableName: String,
                                         dataSource: DataSource,
                                         @Language("SQL") protected val validationQuery: String) : BaseTaskExecutionLock(capacity) {

    companion object {
        @JvmStatic
        private val LOG = LoggerFactory.getLogger(SqlBasedTaskExecutionLock::class.java)
    }

    private val run = QueryRunner(dataSource)

    protected abstract fun initializationQuery(): String
    protected abstract fun lockQuery(): String

    //TODO: where and when to call this?
    open fun initialize() {
        validate()

        val initializationQuery = initializationQuery()

        if (initializationQuery.isBlank()) {
            LOG.info("initialization query is empty, skipping initialization")
            return
        }

        if (LOG.isDebugEnabled) {
            LOG.debug("initializing task locking database table with query=$initializationQuery")
        }

        run.execute(initializationQuery)
    }

    private fun validate() {
        if (LOG.isDebugEnabled) {
            LOG.debug("validating database with query=$validationQuery")
        }
        //TODO: check what happens in case query is wrong
        run.execute(validationQuery)
    }

    override fun tryLockInternal(): Boolean {

        return false
    }

    override fun unlockInternal() {

    }
}