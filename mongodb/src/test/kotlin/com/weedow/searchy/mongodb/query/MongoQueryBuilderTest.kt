package com.weedow.searchy.mongodb.query

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.querydsl.core.JoinType
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Ops
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanOperation
import com.querydsl.core.types.dsl.DateOperation
import com.querydsl.core.types.dsl.DateTimeOperation
import com.querydsl.core.types.dsl.TimeOperation
import com.querydsl.mongodb.MongodbOps
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.querytype.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery

@ExtendWith(MockitoExtension::class)
internal class MongoQueryBuilderTest {

    @Mock
    private lateinit var searchyContext: SearchyContext

    @Mock
    private lateinit var query: SpringDataMongodbQuery<Any>

    @Mock
    private lateinit var qEntityRoot: QEntityRoot<Any>

    @InjectMocks
    private lateinit var mongoQueryBuilder: MongoQueryBuilder<Any>

    @Test
    fun getQEntityRoot() {
        assertThat(mongoQueryBuilder.qEntityRoot).isSameAs(qEntityRoot)
    }

    @Test
    fun distinct() {
        mongoQueryBuilder.distinct()

        verify(query).distinct()
        verifyNoMoreInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun join_without_join_annotation() {
        val propertyInfos = mock<PropertyInfos> {
            on { this.elementType }.thenReturn(ElementType.SIMPLE)
            on { this.type }.thenReturn(Any::class.java)
        }

        val path = mock<QEntity<Any>>()
        val qPath = mock<QPath<*>> {
            on { this.propertyInfos }.thenReturn(propertyInfos)
            on { this.path }.thenReturn(path)
        }
        val joinType = mock<JoinType>()
        val fetched = false

        val join = mongoQueryBuilder.join(qPath, joinType, fetched)

        assertThat(join).isInstanceOf(QEntityJoinImpl::class.java)
        assertThat(join).extracting("propertyInfos").isSameAs(propertyInfos)
        assertThat(join).extracting("qEntity").isSameAs(path)

        verifyNoMoreInteractions(query)
        verifyNoMoreInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun and_with_expressions() {
        val expr1 = mock<Expression<Boolean>>()
        val expr2 = mock<Expression<Boolean>>()
        val predicate = mongoQueryBuilder.and(expr1, expr2)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.AND)
        assertThat(booleanOperation.args).containsExactly(expr1, expr2)
    }

    @Test
    fun and_with_predicates() {
        val predicate1 = mock<Predicate>()
        val predicate2 = mock<Predicate>()
        val predicate = mongoQueryBuilder.and(predicate1, predicate2)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.AND)
        assertThat(booleanOperation.args).containsExactly(predicate1, predicate2)
    }

    @Test
    fun and_with_no_predicates() {
        val predicate = mongoQueryBuilder.and()

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.AND)
        assertThat(booleanOperation.args).isEmpty()
    }

    @Test
    fun or_with_expressions() {
        val expr1 = mock<Expression<Boolean>>()
        val expr2 = mock<Expression<Boolean>>()
        val predicate = mongoQueryBuilder.or(expr1, expr2)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.OR)
        assertThat(booleanOperation.args).containsExactly(expr1, expr2)
    }

    @Test
    fun or_with_predicates() {
        val predicate1 = mock<Predicate>()
        val predicate2 = mock<Predicate>()
        val predicate = mongoQueryBuilder.or(predicate1, predicate2)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.OR)
        assertThat(booleanOperation.args).containsExactly(predicate1, predicate2)
    }

    @Test
    fun or_with_no_predicates() {
        val predicate = mongoQueryBuilder.or()

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.OR)
        assertThat(booleanOperation.args).isEmpty()
    }

    @Test
    operator fun not() {
        val expr = mock<Expression<Boolean>>()
        val predicate = mongoQueryBuilder.not(expr)

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
        val predicate = mongoQueryBuilder.equal(expr, value)

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
        val predicate = mongoQueryBuilder.equal(expr, value)

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
        val predicate = mongoQueryBuilder.equal(expr, value)

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
        val predicate = mongoQueryBuilder.equal(expr, value)

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
        val predicate = mongoQueryBuilder.equal(expr, values)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(MongodbOps.ELEM_MATCH)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).extracting("constant").isEqualTo(values)
        assertThat(booleanOperation.args[1]).isSameAs(expr)
    }

    @Test
    fun isNull() {
        val expr = mock<Expression<*>> {
            on { this.type }.thenReturn(Any::class.java)
        }

        val predicate = mongoQueryBuilder.isNull(expr)

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

        val predicate = mongoQueryBuilder.isNull(expr)

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

        val predicate = mongoQueryBuilder.isNull(expr)

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
        val predicate = mongoQueryBuilder.like(expr, value)

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
        val predicate = mongoQueryBuilder.ilike(expr, value)

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
        val predicate = mongoQueryBuilder.lessThan(expr, value)

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
        val predicate = mongoQueryBuilder.lessThanOrEquals(expr, value)

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
        val predicate = mongoQueryBuilder.greaterThan(expr, value)

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
        val predicate = mongoQueryBuilder.greaterThanOrEquals(expr, value)

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
        val predicate = mongoQueryBuilder.`in`(expr, values)

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
        val predicate = mongoQueryBuilder.`in`(expr, values)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.IN)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(values)
    }

}