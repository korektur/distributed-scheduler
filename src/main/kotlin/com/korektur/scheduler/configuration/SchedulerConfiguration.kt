package com.korektur.scheduler.configuration

class SchedulerConfiguration internal constructor()  {

    companion object {
        @JvmStatic
        public val instance: SchedulerConfiguration by lazy { Holder.INSTANCE }
    }

    private object Holder { val INSTANCE = SchedulerConfigurationPropertiesParser().parse() }

}