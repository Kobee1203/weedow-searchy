package com.weedow.spring.data.search.sample

import com.weedow.spring.data.search.jpa.repository.DataSearchJpaRepositoryFactoryBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan("com.weedow.spring.data.search.common.model")
@EnableJpaRepositories("com.weedow.spring.data.search.sample.repository", repositoryFactoryBeanClass = DataSearchJpaRepositoryFactoryBean::class)
class SampleApplication

fun main(args: Array<String>) {
    runApplication<SampleApplication>(*args)
}
