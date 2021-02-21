package com.weedow.searchy.sample

import com.weedow.searchy.utils.klogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@Configuration
class TimeZoneConfig {

    companion object {
        private val log by klogger()

        private const val DEFAULT_TIME_ZONE = "UTC"
    }

    @Bean
    fun timeZone(@Value("\${spring.jpa.properties.hibernate.jdbc.time_zone:$DEFAULT_TIME_ZONE}") timeZone: String): TimeZone {
        System.setProperty("user.timezone", timeZone);
        val defaultTimeZone = TimeZone.getTimeZone(timeZone)
        TimeZone.setDefault(defaultTimeZone)
        log.info("Spring boot application running in '$timeZone' timezone :" + Date())
        return defaultTimeZone
    }
}