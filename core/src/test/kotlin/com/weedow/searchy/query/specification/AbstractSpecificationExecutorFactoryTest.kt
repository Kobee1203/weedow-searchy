package com.weedow.searchy.query.specification

import com.nhaarman.mockitokotlin2.mock
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.SafeEntityPathResolver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.querydsl.EntityPathResolver
import org.springframework.data.repository.core.EntityInformation
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
internal class AbstractSpecificationExecutorFactoryTest {

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun entity_class_with_primary_key_class(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(EntityWithSpringId::class.java, String::class.java),
                Arguments.of(EntityWithIdField::class.java, Double::class.java),
                Arguments.of(EntityWithUnknownId::class.java, Long::class.java)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("entity_class_with_primary_key_class")
    fun <T, ID> getSpecificationExecutor(entityClass: Class<T>, @Suppress("UNUSED_PARAMETER") primaryKeyClass: Class<ID>) {
        val searchyContext = mock<SearchyContext>()

        val entityInformation = mock<EntityInformation<T, ID>>()

        val repositoryFactorySupport = mock<RepositoryFactorySupport> {
            on { getEntityInformation<T, ID>(entityClass) }.thenReturn(entityInformation)
        }

        val specificationExecutor = TestSpecificationExecutorFactory(
            searchyContext,
            repositoryFactorySupport,
            entityClass,
            primaryKeyClass
        ).getSpecificationExecutor(entityClass)

        assertThat(specificationExecutor).isInstanceOf(TestSpecificationExecutor::class.java)

        val testSpecificationExecutor = specificationExecutor as TestSpecificationExecutor<*, *>

        assertThat(testSpecificationExecutor.searchyContext).isEqualTo(searchyContext)
        assertThat(testSpecificationExecutor.entityInformation).isEqualTo(entityInformation)
        assertThat(testSpecificationExecutor.entityPathResolver).isEqualTo(SafeEntityPathResolver.INSTANCE)
    }

    internal class TestSpecificationExecutorFactory(
        searchyContext: SearchyContext,
        private val repositoryFactorySupport: RepositoryFactorySupport,
        private val expectedEntityClass: Class<*>,
        private val expectedPrimaryKeyClass: Class<*>
    ) : AbstractSpecificationExecutorFactory(searchyContext) {

        public override fun <T, ID> newSpecificationExecutor(
            searchyContext: SearchyContext,
            entityInformation: EntityInformation<T, ID>,
            entityPathResolver: EntityPathResolver
        ): SpecificationExecutor<T> {
            return TestSpecificationExecutor(searchyContext, entityInformation, entityPathResolver)
        }

        override fun <T> findPrimaryKeyClass(entityClass: Class<T>, default: () -> Class<*>): Class<*> {
            val primaryKeyClass = super.findPrimaryKeyClass(entityClass, default)

            assertThat(entityClass).isEqualTo(expectedEntityClass)
            assertThat(primaryKeyClass).isEqualTo(expectedPrimaryKeyClass)

            return primaryKeyClass
        }

        override fun <T, ID> getEntityInformation(entityClass: Class<T>, primaryKeyClass: Class<ID>): EntityInformation<T, ID> {
            assertThat(entityClass).isEqualTo(expectedEntityClass)
            assertThat(primaryKeyClass).isEqualTo(expectedPrimaryKeyClass)

            return super.getEntityInformation(entityClass, primaryKeyClass)
        }

        override fun getRepositoryFactory(): RepositoryFactorySupport = repositoryFactorySupport

    }

    /**
     * Test class to check the parameters passed into the [TestSpecificationExecutorFactory.newSpecificationExecutor] method.
     */
    internal class TestSpecificationExecutor<T, ID>(
        val searchyContext: SearchyContext,
        val entityInformation: EntityInformation<T, ID>,
        val entityPathResolver: EntityPathResolver
    ) : SpecificationExecutor<T> {
        override fun findAll(specification: Specification<T>?): List<T> = emptyList()
    }

    class EntityWithSpringId(
        @org.springframework.data.annotation.Id
        private val myId: String
    )

    class EntityWithIdField(
        private val id: Double
    )

    class EntityWithUnknownId(
        private val unknownId: Long
    )
}

