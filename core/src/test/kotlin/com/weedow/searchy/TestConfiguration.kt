package com.weedow.searchy

import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun testSearchyContext(): SearchyContext {
        return TestSearchyContext()
    }

    @Bean
    @ConditionalOnMissingBean
    fun testSpecificationExecutorFactory(searchyContext: SearchyContext): SpecificationExecutorFactory {
        return TestSpecificationExecutorFactory(searchyContext)
    }

}