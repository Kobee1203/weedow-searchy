package com.weedow.spring.data.search.sample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan("com.weedow.spring.data.search.common.model")
@EnableJpaRepositories("com.weedow.spring.data.search.common.repository")
class SampleApplication

fun main(args: Array<String>) {
    runApplication<SampleApplication>(*args)
}
