package com.weedow.spring.data.search.querydsl.specification

import com.nhaarman.mockitokotlin2.mock
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.querydsl.SafeEntityPathResolver
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
internal class AbstractQueryDslSpecificationExecutorFactoryTest {

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
    fun <T, ID> getQueryDslSpecificationExecutor(entityClass: Class<T>, @Suppress("UNUSED_PARAMETER") primaryKeyClass: Class<ID>) {
        val dataSearchContext = mock<DataSearchContext>()

        val entityInformation = mock<EntityInformation<T, ID>>()

        val repositoryFactorySupport = mock<RepositoryFactorySupport> {
            on { getEntityInformation<T, ID>(entityClass) }.thenReturn(entityInformation)
        }

        val queryDslSpecificationExecutor = TestQueryDslSpecificationExecutorFactory(
            dataSearchContext,
            repositoryFactorySupport,
            entityClass,
            primaryKeyClass
        ).getQueryDslSpecificationExecutor(entityClass)

        assertThat(queryDslSpecificationExecutor).isInstanceOf(TestQueryDslSpecificationExecutor::class.java)

        val specificationExecutor = queryDslSpecificationExecutor as TestQueryDslSpecificationExecutor<*, *>

        assertThat(specificationExecutor.dataSearchContext).isEqualTo(dataSearchContext)
        assertThat(specificationExecutor.entityInformation).isEqualTo(entityInformation)
        assertThat(specificationExecutor.entityPathResolver).isEqualTo(SafeEntityPathResolver.INSTANCE)
    }

    internal class TestQueryDslSpecificationExecutorFactory(
        dataSearchContext: DataSearchContext,
        private val repositoryFactorySupport: RepositoryFactorySupport,
        private val expectedEntityClass: Class<*>,
        private val expectedPrimaryKeyClass: Class<*>
    ) : AbstractQueryDslSpecificationExecutorFactory(dataSearchContext) {

        public override fun <T, ID> newQueryDslSpecificationExecutor(
            dataSearchContext: DataSearchContext,
            entityInformation: EntityInformation<T, ID>,
            entityPathResolver: EntityPathResolver
        ): QueryDslSpecificationExecutor<T> {
            return TestQueryDslSpecificationExecutor(dataSearchContext, entityInformation, entityPathResolver)
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
     * Test class to check the parameters passed into the [TestQueryDslSpecificationExecutorFactory.newQueryDslSpecificationExecutor] method.
     */
    internal class TestQueryDslSpecificationExecutor<T, ID>(
        val dataSearchContext: DataSearchContext,
        val entityInformation: EntityInformation<T, ID>,
        val entityPathResolver: EntityPathResolver
    ) : QueryDslSpecificationExecutor<T> {
        override fun findAll(specification: QueryDslSpecification<T>?): List<T> = emptyList()
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

