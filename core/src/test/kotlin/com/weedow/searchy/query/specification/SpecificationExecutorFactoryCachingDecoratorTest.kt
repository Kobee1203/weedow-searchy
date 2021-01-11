package com.weedow.searchy.query.specification

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
internal class SpecificationExecutorFactoryCachingDecoratorTest {

    @Mock
    private lateinit var specificationExecutorFactory: SpecificationExecutorFactory

    @InjectMocks
    private lateinit var sefcd: SpecificationExecutorFactoryCachingDecorator

    @Test
    fun getSpecificationExecutor() {
        val entityClass = Any::class.java

        val specificationExecutor = mock<SpecificationExecutor<Any>>()
        whenever(specificationExecutorFactory.getSpecificationExecutor(entityClass)).thenReturn(specificationExecutor)

        val result1 = sefcd.getSpecificationExecutor(entityClass)
        assertThat(result1).isSameAs(specificationExecutor)

        // The second call uses the cache
        val result2 = sefcd.getSpecificationExecutor(entityClass)
        assertThat(result2).isSameAs(specificationExecutor)

        // Verify the method is called one time, The second call uses the cache
        verify(specificationExecutorFactory, times(1)).getSpecificationExecutor(entityClass)
    }
}