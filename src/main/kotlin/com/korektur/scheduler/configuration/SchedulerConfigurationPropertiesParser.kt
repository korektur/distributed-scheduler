package com.korektur.scheduler.configuration

import org.slf4j.LoggerFactory
import java.nio.file.Paths

class SchedulerConfigurationPropertiesParser {

    companion object {
        @JvmStatic
        private val LOG = LoggerFactory.getLogger(SchedulerConfigurationPropertiesParser::class.java)
        @JvmStatic
        private val DEFAULT_CONFIGURATION_PATH = "scheduling.properties"
        @JvmStatic
        private val CONFIGURATION_PATH_PROPERTY = "scheduler.config.path"
    }

    /**
     * Parses properties file to configure global scheduler properties.
     *
     * Default properties file name is  `scheduling.properties`.
     * Can be overwritten by setting `scheduler.config.path` system property.
     */
    public fun parse(): SchedulerConfiguration {
        val filePath = System.getProperty(CONFIGURATION_PATH_PROPERTY, DEFAULT_CONFIGURATION_PATH)
        val file = Paths.get(filePath).toFile()

        if (!file.isFile) {
            LOG.warn("configuration file $filePath not found, using default configuration")
        }



        TODO("implement")
        return SchedulerConfiguration()
    }
}