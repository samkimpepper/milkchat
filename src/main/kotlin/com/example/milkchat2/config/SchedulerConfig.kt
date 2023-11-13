package com.example.milkchat2.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class SchedulerConfig {

    @Bean
    fun taskScheduler(): ThreadPoolTaskScheduler {
        var scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 10
        scheduler.setThreadNamePrefix("task-scheduler-")
        scheduler.initialize()
        return scheduler
    }
}