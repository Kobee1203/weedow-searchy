package com.weedow.spring.data.search.querydsl.specification

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class QueryDslSpecificationExecutorFactoryCachingDecoratorTest {

    @Mock
    private lateinit var specificationExecutorFactory: QueryDslSpecificationExecutorFactory

    @InjectMocks
    private lateinit var sefcd: QueryDslSpecificationExecutorFactoryCachingDecorator

    @Test
    fun getQueryDslSpecificationExecutor() {
        val entityClass = Any::class.java

        val specificationExecutor = mock<QueryDslSpecificationExecutor<Any>>()
        whenever(specificationExecutorFactory.getQueryDslSpecificationExecutor(entityClass)).thenReturn(specificationExecutor)

        val result1 = sefcd.getQueryDslSpecificationExecutor(entityClass)
        assertThat(result1).isSameAs(specificationExecutor)

        // The second call uses the cache
        val result2 = sefcd.getQueryDslSpecificationExecutor(entityClass)
        assertThat(result2).isSameAs(specificationExecutor)

        // Verify the method is called one time, The second call uses the cache
        verify(specificationExecutorFactory, times(1)).getQueryDslSpecificationExecutor(entityClass)
    }
}