package com.weedow.spring.data.search

import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.query.specification.SpecificationExecutorFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun testDataSearchContext(): DataSearchContext {
        return TestDataSearchContext()
    }

    @Bean
    @ConditionalOnMissingBean
    fun testSpecificationExecutorFactory(dataSearchContext: DataSearchContext): SpecificationExecutorFactory {
        return TestSpecificationExecutorFactory(dataSearchContext)
    }

}