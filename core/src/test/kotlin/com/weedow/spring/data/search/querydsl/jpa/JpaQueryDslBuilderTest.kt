package com.weedow.spring.data.search.querydsl.jpa

import com.nhaarman.mockitokotlin2.*
import com.querydsl.core.JoinExpression
import com.querydsl.core.JoinType
import com.querydsl.core.QueryMetadata
import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.*
import com.querydsl.jpa.JPAQueryMixin
import com.querydsl.jpa.JPQLOps
import com.querydsl.jpa.impl.AbstractJPAQuery
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.querydsl.querytype.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
internal class JpaQueryDslBuilderTest {

    @Mock
    private lateinit var dataSearchContext: DataSearchContext

    @Mock
    private lateinit var query: AbstractJPAQuery<Any, *>

    @Mock
    private lateinit var qEntityRoot: QEntityRoot<Any>

    @InjectMocks
    private lateinit var queryDslBuilder: JpaQueryDslBuilder<Any>

    @Test
    fun getQEntityRoot() {
        assertThat(queryDslBuilder.qEntityRoot).isSameAs(qEntityRoot)
    }

    @Test
    fun distinct() {
        queryDslBuilder.distinct()

        verify(query).distinct()
        verifyNoMoreInteractions(query)
        verifyZeroInteractions(dataSearchContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @ParameterizedTest
    @EnumSource(value = ElementType::class, names = ["MAP_KEY", "MAP_VALUE"])
    fun join_map_entries(elementType: ElementType) {
        val entityClass = Any::class.java

        val propertyInfos = mock<PropertyInfos> {
            on { this.elementType }.thenReturn(elementType)
            on { this.type }.thenReturn(entityClass)
        }

        val metadata = mock<PathMetadata>()
        val path = mock<Path<*>> {
            on { this.metadata }.thenReturn(metadata)
        }
        val qPath = mock<QPath<*>> {
            on { this.propertyInfos }.thenReturn(propertyInfos)
            on { this.path }.thenReturn(path)
        }

        whenever(dataSearchContext.getAllPropertyInfos(entityClass)).thenReturn(emptyList())

        val join = queryDslBuilder.join(qPath, JoinType.DEFAULT, false)

        assertThat(join).isInstanceOf(QEntityJoinImpl::class.java)
        assertThat(join).extracting("propertyInfos").isSameAs(propertyInfos)
        assertThat(join).extracting("qEntity")
            .isInstanceOf(QEntityImpl::class.java)
            .extracting("dataSearchContext")
            .isSameAs(dataSearchContext)

        verifyNoMoreInteractions(dataSearchContext)
        verifyZeroInteractions(query)
        verifyZeroInteractions(qEntityRoot)
    }

    @ParameterizedTest
    @MethodSource("join_with_existing_join_expression")
    fun join_with_existing_join_expression(fetched: Boolean, addConditions: (JoinExpression) -> Unit) {
        val elementType = ElementType.SIMPLE
        val propertyInfos = mock<PropertyInfos> {
            on { this.elementType }.thenReturn(elementType)
        }

        val joinPath = "myjoin"

        val path = mock<Path<*>> {
            on { this.toString() }.thenReturn(joinPath)
        }
        val qPath = mock<QPath<*>> {
            on { this.propertyInfos }.thenReturn(propertyInfos)
            on { this.path }.thenReturn(path)
        }

        val qEntity = mock<QEntity<*>>()
        val expression = mock<Operation<*>> {
            on { this.operator }.thenReturn(Ops.ALIAS)
            on { this.getArg(0) }.thenReturn(Expressions.constant(joinPath))
            on { this.getArg(1) }.thenReturn(qEntity)
        }
        val joinExpression = mock<JoinExpression> {
            on { this.target }.thenReturn(expression)
        }
        val existingJoins = listOf(joinExpression)
        val metadata = mock<QueryMetadata> {
            on { this.joins }.thenReturn(existingJoins)
        }
        whenever(query.metadata).thenReturn(metadata)

        addConditions(joinExpression)

        val join = queryDslBuilder.join(qPath, JoinType.DEFAULT, fetched)

        assertThat(join)
            .isInstanceOf(QEntityJoinImpl::class.java)
            .extracting("qEntity", "propertyInfos")
            .contains(qEntity, propertyInfos)

        verifyZeroInteractions(dataSearchContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @ParameterizedTest
    @MethodSource("join_with_alias")
    fun join_with_alias(
        propertyInfos: PropertyInfos,
        fetched: Boolean,
        joinType: JoinType,
        path: Path<*>,
        doVerify: (query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any>) -> Unit
    ) {
        val entityClass = Any::class.java
        verifyJoinWithAlias(entityClass, propertyInfos, fetched, joinType, path, doVerify)
    }

    private fun <T> verifyJoinWithAlias(
        entityClass: Class<T>,
        propertyInfos: PropertyInfos,
        fetched: Boolean,
        joinType: JoinType,
        path: Path<*>,
        doVerify: (query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<T>) -> Unit
    ) {
        val qPath = mock<QPath<*>> {
            on { this.propertyInfos }.thenReturn(propertyInfos)
            on { this.path }.thenReturn(path)
        }

        val metadata = mock<QueryMetadata> {
            on { this.joins }.thenReturn(emptyList())
        }
        whenever(query.metadata).thenReturn(metadata)

        val qEntityAlias = mock<QEntity<T>>()
        whenever(dataSearchContext.get(eq(entityClass), any())).thenReturn(qEntityAlias)

        val join = queryDslBuilder.join(qPath, joinType, fetched)

        assertThat(join).isInstanceOf(QEntityJoinImpl::class.java)
        assertThat(join).extracting("propertyInfos").isSameAs(propertyInfos)
        // Special case: If CROSS JOIN and Entity -> query.from(path as QEntity<E>) without alias and return the alias
        if (joinType == JoinType.DEFAULT && propertyInfos.elementType == ElementType.ENTITY) {
            assertThat(join).extracting("qEntity").isSameAs(path)
        } else {
            assertThat(join).extracting("qEntity").isSameAs(qEntityAlias)
        }

        if (fetched) {
            verify(query).fetchJoin()
        }

        doVerify(query, path, qEntityAlias)

        verifyNoMoreInteractions(query)
        verifyNoMoreInteractions(dataSearchContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun throw_exception_when_create_join_with_bad_element_type() {
        val elementType = ElementType.SIMPLE
        val propertyInfos = mock<PropertyInfos> {
            on { this.elementType }.thenReturn(elementType)
        }

        val qPath = mock<QPath<*>> {
            on { this.propertyInfos }.thenReturn(propertyInfos)
        }

        val existingJoins = emptyList<JoinExpression>()
        val metadata = mock<QueryMetadata> {
            on { this.joins }.thenReturn(existingJoins)
        }
        whenever(query.metadata).thenReturn(metadata)

        assertThatThrownBy { queryDslBuilder.join(qPath, JoinType.DEFAULT, false) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Could not identify the alias type for the QPath of type '$elementType': $qPath")

        verifyZeroInteractions(dataSearchContext)
        verifyNoMoreInteractions(query)
    }

    @Test
    fun throw_exception_when_create_full_join() {
        val entityClass = Any::class.java

        val elementType = ElementType.ENTITY
        val propertyInfos = mock<PropertyInfos> {
            on { this.elementType }.thenReturn(elementType)
            on { this.type }.thenReturn(entityClass)
        }

        val path = mock<Path<out Any?>>()
        val qPath = mock<QPath<*>> {
            on { this.propertyInfos }.thenReturn(propertyInfos)
            on { this.path }.thenReturn(path)
        }

        val metadata = mock<QueryMetadata> {
            on { this.joins }.thenReturn(emptyList())
        }
        whenever(query.metadata).thenReturn(metadata)

        val qEntityAlias = mock<QEntity<Any>>()
        whenever(dataSearchContext.get(eq(entityClass), any())).thenReturn(qEntityAlias)

        assertThatThrownBy { queryDslBuilder.join(qPath, JoinType.FULLJOIN, false) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("full join in JPA is not allowed: element type=$elementType, path=$path, alias=$qEntityAlias")

        verifyZeroInteractions(dataSearchContext)
        verifyNoMoreInteractions(query)
    }

    @Test
    fun and_with_expressions() {
        val expr1 = mock<Expression<Boolean>>()
        val expr2 = mock<Expression<Boolean>>()
        val predicate = queryDslBuilder.and(expr1, expr2)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.AND)
        assertThat(booleanOperation.args).containsExactly(expr1, expr2)
    }

    @Test
    fun and_with_predicates() {
        val predicate1 = mock<Predicate>()
        val predicate2 = mock<Predicate>()
        val predicate = queryDslBuilder.and(predicate1, predicate2)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.AND)
        assertThat(booleanOperation.args).containsExactly(predicate1, predicate2)
    }

    @Test
    fun or_with_no_predicates() {
        val predicate = queryDslBuilder.or()

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.OR)
        assertThat(booleanOperation.args).isEmpty()
    }

    @Test
    fun or_with_expressions() {
        val expr1 = mock<Expression<Boolean>>()
        val expr2 = mock<Expression<Boolean>>()
        val predicate = queryDslBuilder.or(expr1, expr2)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.OR)
        assertThat(booleanOperation.args).containsExactly(expr1, expr2)
    }

    @Test
    fun or_with_predicates() {
        val predicate1 = mock<Predicate>()
        val predicate2 = mock<Predicate>()
        val predicate = queryDslBuilder.or(predicate1, predicate2)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.OR)
        assertThat(booleanOperation.args).containsExactly(predicate1, predicate2)
    }

    @Test
    fun and_with_no_predicates() {
        val predicate = queryDslBuilder.and()

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.AND)
        assertThat(booleanOperation.args).isEmpty()
    }

    @Test
    operator fun not() {
        val expr = mock<Expression<Boolean>>()
        val predicate = queryDslBuilder.not(expr)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.NOT)
        assertThat(booleanOperation.args).containsExactly(expr)
    }

    @Test
    fun equal() {
        val expr = mock<Expression<*>> {
            on { this.type }.thenReturn(Any::class.java)
        }
        val value = "myvalue"
        val predicate = queryDslBuilder.equal(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(value)
    }

    @Test
    fun equal_with_current_date_value() {
        val expr = mock<Expression<*>> {
            on { this.type }.thenReturn(Any::class.java)
        }
        val value = "CURRENT_DATE"
        val predicate = queryDslBuilder.equal(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).isInstanceOf(DateOperation::class.java)
        assertThat(booleanOperation.args[1]).extracting("operator").isEqualTo(Ops.DateTimeOps.CURRENT_DATE)
    }

    @Test
    fun equal_with_current_time_value() {
        val expr = mock<Expression<*>> {
            on { this.type }.thenReturn(Any::class.java)
        }
        val value = "CURRENT_TIME"
        val predicate = queryDslBuilder.equal(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).isInstanceOf(TimeOperation::class.java)
        assertThat(booleanOperation.args[1]).extracting("operator").isEqualTo(Ops.DateTimeOps.CURRENT_TIME)
    }

    @Test
    fun equal_with_current_date_time_value() {
        val expr = mock<Expression<*>> {
            on { this.type }.thenReturn(Any::class.java)
        }
        val value = "CURRENT_DATE_TIME"
        val predicate = queryDslBuilder.equal(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).isInstanceOf(DateTimeOperation::class.java)
        assertThat(booleanOperation.args[1]).extracting("operator").isEqualTo(Ops.DateTimeOps.CURRENT_TIMESTAMP)
    }

    @Test
    fun equal_with_collection() {
        val expr = mock<Expression<*>> {
            on { this.type }.thenReturn(Collection::class.java)
        }
        val values = listOf("myvalue1", "myvalue2")
        val predicate = queryDslBuilder.equal(expr, values)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(JPQLOps.MEMBER_OF)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).extracting("constant").isEqualTo(values)
        assertThat(booleanOperation.args[1]).isSameAs(expr)
    }

    @Test
    fun isNull() {
        val expr = mock<Expression<*>> {
            on { this.type }.thenReturn(Any::class.java)
        }

        val predicate = queryDslBuilder.isNull(expr)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.IS_NULL)
        assertThat(booleanOperation.args).hasSize(1)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
    }

    @Test
    fun isNull_with_collection() {
        val expr = mock<Expression<*>> {
            on { this.type }.thenReturn(Collection::class.java)
        }

        val predicate = queryDslBuilder.isNull(expr)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.COL_IS_EMPTY)
        assertThat(booleanOperation.args).hasSize(1)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
    }

    @Test
    fun isNull_with_map() {
        val expr = mock<Expression<*>> {
            on { this.type }.thenReturn(Map::class.java)
        }

        val predicate = queryDslBuilder.isNull(expr)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.MAP_IS_EMPTY)
        assertThat(booleanOperation.args).hasSize(1)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
    }

    @Test
    fun like() {
        val expr = mock<Expression<String>>()
        val value = "MYVALUE*"
        val predicate = queryDslBuilder.like(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.LIKE)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo("MYVALUE%")
    }

    @Test
    fun ilike() {
        val expr = mock<Expression<String>>()
        val value = "MYVALUE*"
        val predicate = queryDslBuilder.ilike(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.LIKE_IC)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo("myvalue%")
    }

    @Test
    fun lessThan() {
        val expr = mock<Expression<*>>()
        val value = 123
        val predicate = queryDslBuilder.lessThan(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.LT)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(value)
    }

    @Test
    fun lessThanOrEquals() {
        val expr = mock<Expression<*>>()
        val value = 123
        val predicate = queryDslBuilder.lessThanOrEquals(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.LOE)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(value)
    }

    @Test
    fun greaterThan() {
        val expr = mock<Expression<*>>()
        val value = 123
        val predicate = queryDslBuilder.greaterThan(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.GT)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(value)
    }

    @Test
    fun greaterThanOrEquals() {
        val expr = mock<Expression<*>>()
        val value = 123
        val predicate = queryDslBuilder.greaterThanOrEquals(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.GOE)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(value)
    }

    @Test
    fun in_with_single_value() {
        val expr = mock<Expression<*>> {
            on { this.type }.thenReturn(Any::class.java)
        }
        val v = "myvalue"
        val values = listOf(v)
        val predicate = queryDslBuilder.`in`(expr, values)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(v)
    }

    @Test
    fun in_with_multiple_values() {
        val expr = mock<Expression<*>>()
        val values = listOf("myvalue1", "myvalue2")
        val predicate = queryDslBuilder.`in`(expr, values)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.IN)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(values)
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun join_with_existing_join_expression(): Stream<Arguments> {
            val doNothing = { _: JoinExpression -> }
            return Stream.of(
                Arguments.of(false, doNothing),
                Arguments.of(true, { joinExpression: JoinExpression ->
                    whenever(joinExpression.hasFlag(JPAQueryMixin.FETCH)).thenReturn(false)
                    whenever(joinExpression.flags).thenReturn(mutableSetOf())
                }),
                Arguments.of(true, { joinExpression: JoinExpression ->
                    whenever(joinExpression.hasFlag(JPAQueryMixin.FETCH)).thenReturn(true)
                })
            )
        }

        @JvmStatic
        @Suppress("unused", "UNCHECKED_CAST")
        private fun join_with_alias(): Stream<Arguments> {
            return Stream.of(
                // Arguments to test that 'query.fetchJoin()' is not called
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.COLLECTION)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    false,
                    JoinType.JOIN,
                    mock<CollectionPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).join(eq(path as CollectionPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                /****************************
                 * COLLECTION
                 ****************************/

                // COLLECTION / JOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.COLLECTION)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.JOIN,
                    mock<CollectionPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).join(eq(path as CollectionPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // COLLECTION / INNERJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.COLLECTION)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.INNERJOIN,
                    mock<CollectionPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).innerJoin(eq(path as CollectionPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // COLLECTION / LEFTJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.COLLECTION)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.LEFTJOIN,
                    mock<CollectionPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).leftJoin(eq(path as CollectionPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // COLLECTION / RIGHTJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.COLLECTION)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.RIGHTJOIN,
                    mock<CollectionPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).rightJoin(eq(path as CollectionPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // COLLECTION / CROSSJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.COLLECTION)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.DEFAULT,
                    mock<CollectionPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).from(eq(path as CollectionPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                /****************************
                 * LIST
                 ****************************/

                // LIST / JOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.LIST)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.JOIN,
                    mock<ListPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).join(eq(path as ListPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // LIST / INNERJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.LIST)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.INNERJOIN,
                    mock<ListPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).innerJoin(eq(path as ListPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // LIST / LEFTJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.LIST)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.LEFTJOIN,
                    mock<ListPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).leftJoin(eq(path as ListPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // LIST / RIGHTJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.LIST)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.RIGHTJOIN,
                    mock<ListPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).rightJoin(eq(path as ListPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // LIST / CROSSJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.LIST)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.DEFAULT,
                    mock<ListPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).from(eq(path as ListPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                /****************************
                 * SET
                 ****************************/

                // SET / JOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.SET)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.JOIN,
                    mock<SetPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).join(eq(path as SetPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // SET / INNERJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.SET)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.INNERJOIN,
                    mock<SetPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).innerJoin(eq(path as SetPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // SET / LEFTJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.SET)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.LEFTJOIN,
                    mock<SetPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).leftJoin(eq(path as SetPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // SET / RIGHTJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.SET)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.RIGHTJOIN,
                    mock<SetPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).rightJoin(eq(path as SetPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // SET / CROSSJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.SET)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java))
                    },
                    true,
                    JoinType.DEFAULT,
                    mock<SetPath<Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).from(eq(path as SetPath<Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                /****************************
                 * MAP
                 ****************************/

                // MAP / JOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.MAP)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java, Any::class.java))
                    },
                    true,
                    JoinType.JOIN,
                    mock<MapPath<Any, Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).join(eq(path as MapPath<Any, Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // MAP / INNERJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.MAP)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java, Any::class.java))
                    },
                    true,
                    JoinType.INNERJOIN,
                    mock<MapPath<Any, Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).innerJoin(eq(path as MapPath<Any, Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // MAP / LEFTJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.MAP)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java, Any::class.java))
                    },
                    true,
                    JoinType.LEFTJOIN,
                    mock<MapPath<Any, Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).leftJoin(eq(path as MapPath<Any, Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // MAP / RIGHTJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.MAP)
                        on { parameterizedTypes }.thenReturn(listOf(Any::class.java, Any::class.java))
                    },
                    true,
                    JoinType.RIGHTJOIN,
                    mock<MapPath<Any, Any, *>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).rightJoin(eq(path as MapPath<Any, Any, *>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                /****************************
                 * ENTITY
                 ****************************/

                // ENTITY / JOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.ENTITY)
                        on { type }.thenReturn(Any::class.java)
                    },
                    true,
                    JoinType.JOIN,
                    mock<QEntity<Any>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).join(eq(path as QEntity<Any>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // ENTITY / INNERJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.ENTITY)
                        on { type }.thenReturn(Any::class.java)
                    },
                    true,
                    JoinType.INNERJOIN,
                    mock<QEntity<Any>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).innerJoin(eq(path as QEntity<Any>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // ENTITY / LEFTJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.ENTITY)
                        on { type }.thenReturn(Any::class.java)
                    },
                    true,
                    JoinType.LEFTJOIN,
                    mock<QEntity<Any>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).leftJoin(eq(path as QEntity<Any>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // ENTITY / RIGHTJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.ENTITY)
                        on { type }.thenReturn(Any::class.java)
                    },
                    true,
                    JoinType.RIGHTJOIN,
                    mock<QEntity<Any>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        argumentCaptor<Path<Any>> {
                            verify(query).rightJoin(eq(path as QEntity<Any>), capture())

                            assertThat(allValues).containsExactly(alias)
                        }
                    }
                ),

                // ENTITY / CROSSJOIN
                Arguments.of(
                    mock<PropertyInfos> {
                        on { elementType }.thenReturn(ElementType.ENTITY)
                        on { type }.thenReturn(Any::class.java)
                    },
                    true,
                    JoinType.DEFAULT,
                    mock<QEntity<Any>>(),
                    { query: AbstractJPAQuery<Any, *>, path: Path<*>, alias: QEntity<Any> ->
                        verify(query).from(path as QEntity<Any>)
                    }
                )
            )
        }
    }

}