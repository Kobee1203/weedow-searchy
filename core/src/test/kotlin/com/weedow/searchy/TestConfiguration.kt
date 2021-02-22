package com.weedow.searchy

import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun testSearchyContext(@Value("\${weedow.searchy.context.unknown-as-embedded:false}") isUnknownAsEmbedded: Boolean): SearchyContext {
        return TestSearchyContext(isUnknownAsEmbedded)
    }

    @Bean
    @ConditionalOnMissingBean
    fun testSpecificationExecutorFactory(searchyContext: SearchyContext): SpecificationExecutorFactory {
        return TestSpecificationExecutorFactory(searchyContext)
    }

}