package com.weedow.searchy.sample

import com.weedow.searchy.jpa.repository.JpaSearchyRepositoryFactoryBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan("com.weedow.searchy.common.model")
@EnableJpaRepositories("com.weedow.searchy.sample.repository", repositoryFactoryBeanClass = JpaSearchyRepositoryFactoryBean::class)
class SampleApplication

fun main(args: Array<String>) {
    runApplication<SampleApplication>(*args)
}
