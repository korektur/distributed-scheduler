package com.korektur.scheduler.lock

import org.intellij.lang.annotations.Language
import javax.sql.DataSource

class H2BasedTaskExecutionLock(capacity: Int,
                               dataSource: DataSource,
                               schemaName: String,
                               tableName: String = "DIST_SCHEDULER_LOCKS",
                               @Language("SQL") validationQuery: String = "select * from dual") :
        SqlBasedTaskExecutionLock(capacity, tableName, dataSource, validationQuery) {

    @Language("SQL")
    private val createTableQuery = """CREATE TABLE IF NOT EXISTS $schemaName.$tableName(
        ID VARCHAR(255) NOT NULL,
        LOCK_INDEX INT NOT NULL,
        LAST_EXECUTION TIMESTAMP WITH TIME ZONE);""".trimIndent()

    @Language("SQL")
    private val lockQuery = "SELECT * FROM $schemaName.$tableName WHERE ID=? AND INDEX=? FOR UPDATE"

    override fun initializationQuery(): String {
        return createTableQuery
    }

    override fun lockQuery(): String {
        return lockQuery
    }
}