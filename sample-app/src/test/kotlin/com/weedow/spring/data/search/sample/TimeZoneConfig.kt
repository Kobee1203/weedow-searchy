package com.weedow.spring.data.search.sample

import com.weedow.spring.data.search.utils.klogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class TimeZoneConfig {

    companion object {
        private val log by klogger()

        private const val TIME_ZONE_ID = "UTC"
    }

    @Bean
    fun timeZone(): TimeZone {
        val defaultTimeZone = TimeZone.getTimeZone(TIME_ZONE_ID)
        TimeZone.setDefault(defaultTimeZone)
        log.info("Spring boot application running in '$TIME_ZONE_ID' timezone :" + Date())
        return defaultTimeZone
    }
}