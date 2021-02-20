package com.weedow.searchy.mongodb.query

import com.nhaarman.mockitokotlin2.*
import com.querydsl.core.JoinType
import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.*
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.mongodb.query.querytype.PathWrapper
import com.weedow.searchy.mongodb.query.querytype.QEntityJoinImpl
import com.weedow.searchy.query.querytype.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.bson.BsonJavaScript
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery
import java.nio.charset.StandardCharsets
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Stream
import kotlin.reflect.full.createInstance

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

    @ParameterizedTest
    @EnumSource(value = ElementType::class)
    fun join_without_join_annotation(elementType: ElementType) {
        val expectedType = Any::class.java

        val propertyInfos = mock<PropertyInfos> {
            on { this.elementType }.thenReturn(elementType)
        }
        when (elementType) {
            ElementType.SET,
            ElementType.LIST,
            ElementType.COLLECTION,
            ElementType.ARRAY
            -> whenever(propertyInfos.parameterizedTypes).thenReturn(listOf(expectedType))
            ElementType.MAP -> whenever(propertyInfos.parameterizedTypes).thenReturn(listOf(String::class.java, expectedType))
            else -> whenever(propertyInfos.type).thenReturn(expectedType)
        }

        val qEntity = mock<QEntity<Any>>()
        whenever(searchyContext.get(eq(expectedType), any())).doReturn(qEntity)

        val qPath = mock<QPath<*>> {
            on { this.propertyInfos }.thenReturn(propertyInfos)
        }
        val joinType = mock<JoinType>()
        val fetched = false

        val join = mongoQueryBuilder.join(qPath, joinType, fetched)

        assertThat(join).isInstanceOf(QEntityJoinImpl::class.java)
        assertThat(join).extracting("propertyInfos").isSameAs(propertyInfos)
        assertThat(join).extracting("qEntity").isSameAs(qEntity)

        verifyZeroInteractions(query)
        verifyNoMoreInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @ParameterizedTest
    @EnumSource(value = ElementType::class, mode = EnumSource.Mode.EXCLUDE, names = ["SET", "LIST", "COLLECTION", "ARRAY", "MAP", "ENTITY"])
    fun throw_exception_when_element_type_is_invalid_for_joined_field(elementType: ElementType) {
        val joinAnnotationKlass = DBRef::class

        whenever(searchyContext.isJoinAnnotation(joinAnnotationKlass.java)).thenReturn(true)

        val propertyInfos = mock<PropertyInfos> {
            on { this.elementType }.thenReturn(elementType)
            on { this.annotations }.thenReturn(listOf(joinAnnotationKlass.createInstance()))
        }
        val qPath = mock<QPath<*>> {
            on { this.propertyInfos }.thenReturn(propertyInfos)
        }

        val joinType = mock<JoinType>()
        val fetched = false

        assertThatThrownBy { mongoQueryBuilder.join(qPath, joinType, fetched) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Could not identify the alias type for the QPath of type '$elementType': $qPath")
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun and_with_no_predicates() {
        val predicate = mongoQueryBuilder.and()

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.AND)
        assertThat(booleanOperation.args).isEmpty()

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun or_with_no_predicates() {
        val predicate = mongoQueryBuilder.or()

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.OR)
        assertThat(booleanOperation.args).isEmpty()

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun not() {
        val expr = mock<Expression<Boolean>>()
        val predicate = mongoQueryBuilder.not(expr)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.NOT)
        assertThat(booleanOperation.args).containsExactly(expr)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun equal() {
        val expr = mock<Expression<*>>()
        val value = "myvalue"
        val predicate = mongoQueryBuilder.equal(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(value)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun equal_with_current_date_value() {
        val startOfDay = Date.from(LocalDate.now().atTime(LocalTime.MIN).atZone(MongoQueryBuilder.DEFAULT_TIME_ZONE.toZoneId()).toInstant())
        val endOfDay = Date.from(LocalDate.now().atTime(LocalTime.MAX).atZone(MongoQueryBuilder.DEFAULT_TIME_ZONE.toZoneId()).toInstant())

        val expr = mock<Expression<*>>()
        val value = "CURRENT_DATE"
        val predicate = mongoQueryBuilder.equal(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.BETWEEN)
        assertThat(booleanOperation.args).hasSize(3)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).isInstanceOf(DateExpression::class.java)
        assertThat(booleanOperation.args[1].type).isEqualTo(Date::class.java)
        assertThat(booleanOperation.args[1]).extracting("mixin").extracting("constant").isEqualTo(startOfDay)
        assertThat(booleanOperation.args[2]).isInstanceOf(DateExpression::class.java)
        assertThat(booleanOperation.args[2].type).isEqualTo(Date::class.java)
        assertThat(booleanOperation.args[2]).extracting("mixin").extracting("constant").isEqualTo(endOfDay)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun equal_with_current_time_value() {
        val expr = mock<Expression<*>>()
        val value = "CURRENT_TIME"
        val predicate = mongoQueryBuilder.equal(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).isInstanceOf(TimeOperation::class.java)
        assertThat(booleanOperation.args[1]).extracting("operator").isEqualTo(Ops.DateTimeOps.CURRENT_TIME)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun equal_with_current_date_time_value() {
        val now = ZonedDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))

        val expr = mock<Expression<*>>()
        val value = "CURRENT_DATE_TIME"
        val predicate = mongoQueryBuilder.equal(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).isInstanceOf(DateTimeExpression::class.java)
        assertThat(booleanOperation.args[1].type).isEqualTo(LocalDateTime::class.java)
        assertThat(booleanOperation.args[1]).extracting("mixin").extracting("constant").isInstanceOf(LocalDateTime::class.java)
        assertThat(booleanOperation.args[1].toString()).startsWith(now)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun equal_with_collection() {
        val expr = mock<Expression<*>>()
        val values = listOf("myvalue1", "myvalue2")
        val predicate = mongoQueryBuilder.equal(expr, values)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(values)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun equal_with_pathwrapper_map_key() {
        val expr = mock<PathWrapper<*>> {
            on { this.elementType }.thenReturn(ElementType.MAP_KEY)
        }
        val value = "myvalue"
        val predicate = mongoQueryBuilder.equal(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.CONTAINS_KEY)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(value)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @ParameterizedTest
    @MethodSource("get_parent_path")
    fun equal_with_pathwrapper_map_value(parentPath: Path<*>?) {
        val path = "expr_name"
        val expr = mock<PathWrapper<*>> {
            on { this.elementType }.thenReturn(ElementType.MAP_VALUE)
            val metadata = mock<PathMetadata> {
                on { this.name }.thenReturn(path)
                on { this.parent }.thenReturn(parentPath)
            }
            on { this.metadata }.thenReturn(metadata)
        }
        val value = "myvalue"
        val predicate = mongoQueryBuilder.equal(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isInstanceOf(StringPath::class.java).isEqualTo(Expressions.stringPath("\$where"))
        val jsFunction = String(javaClass.getResourceAsStream("/map_contains_value_result_1.js").readAllBytes(), StandardCharsets.UTF_8)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(BsonJavaScript(jsFunction))

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun equal_with_pathwrapper_map_value_with_parent() {
        val path = "expr_name"
        val parentPath = "parent_expr"
        val expr = mock<PathWrapper<*>> {
            on { this.elementType }.thenReturn(ElementType.MAP_VALUE)
            val metadata = mock<PathMetadata> {
                on { this.name }.thenReturn(path)
                val parent = mock<Path<*>> {
                    val parentMetadata = mock<PathMetadata> {
                        on { this.isRoot }.thenReturn(false)
                        on { this.name }.thenReturn(parentPath)
                    }
                    on { this.metadata }.thenReturn(parentMetadata)
                }
                on { this.parent }.thenReturn(parent)
            }
            on { this.metadata }.thenReturn(metadata)
        }
        val value = "myvalue"
        val predicate = mongoQueryBuilder.equal(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isInstanceOf(StringPath::class.java).isEqualTo(Expressions.stringPath("\$where"))
        val jsFunction = String(javaClass.getResourceAsStream("/map_contains_value_result_2.js").readAllBytes(), StandardCharsets.UTF_8)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(BsonJavaScript(jsFunction))

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @ParameterizedTest
    @EnumSource(value = ElementType::class, mode = EnumSource.Mode.EXCLUDE, names = ["MAP_KEY", "MAP_VALUE"])
    fun throw_exception_when_equal_with_pathwrapper_and_invalid_element_type(elementType: ElementType) {
        val expr = mock<PathWrapper<*>> {
            on { this.elementType }.thenReturn(elementType)
        }
        val value = "myvalue"

        assertThatThrownBy { mongoQueryBuilder.equal(expr, value) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("ElementType '${elementType}' from the PathWrapper is not supported")

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun lessThan_with_current_date() {
        val startOfDay = Date.from(LocalDate.now().atTime(LocalTime.MIN).atZone(MongoQueryBuilder.DEFAULT_TIME_ZONE.toZoneId()).toInstant())

        val expr = mock<Expression<*>>()
        val value = "CURRENT_DATE"
        val predicate = mongoQueryBuilder.lessThan(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.LT)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).isInstanceOf(DateExpression::class.java)
        assertThat(booleanOperation.args[1].type).isEqualTo(Date::class.java)
        assertThat(booleanOperation.args[1]).extracting("mixin").extracting("constant").isEqualTo(startOfDay)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun lessThanOrEquals_with_current_date() {
        val startOfDay = Date.from(LocalDate.now().atTime(LocalTime.MIN).atZone(MongoQueryBuilder.DEFAULT_TIME_ZONE.toZoneId()).toInstant())

        val expr = mock<Expression<*>>()
        val value = "CURRENT_DATE"
        val predicate = mongoQueryBuilder.lessThanOrEquals(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.LOE)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).isInstanceOf(DateExpression::class.java)
        assertThat(booleanOperation.args[1].type).isEqualTo(Date::class.java)
        assertThat(booleanOperation.args[1]).extracting("mixin").extracting("constant").isEqualTo(startOfDay)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun greaterThan_with_current_date() {
        val endOfDay = Date.from(LocalDate.now().atTime(LocalTime.MAX).atZone(MongoQueryBuilder.DEFAULT_TIME_ZONE.toZoneId()).toInstant())

        val expr = mock<Expression<*>>()
        val value = "CURRENT_DATE"
        val predicate = mongoQueryBuilder.greaterThan(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.GT)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).isInstanceOf(DateExpression::class.java)
        assertThat(booleanOperation.args[1].type).isEqualTo(Date::class.java)
        assertThat(booleanOperation.args[1]).extracting("mixin").extracting("constant").isEqualTo(endOfDay)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun greaterThanOrEquals_with_current_date() {
        val endOfDay = Date.from(LocalDate.now().atTime(LocalTime.MAX).atZone(MongoQueryBuilder.DEFAULT_TIME_ZONE.toZoneId()).toInstant())

        val expr = mock<Expression<*>>()
        val value = "CURRENT_DATE"
        val predicate = mongoQueryBuilder.greaterThanOrEquals(expr, value)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.GOE)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).isInstanceOf(DateExpression::class.java)
        assertThat(booleanOperation.args[1].type).isEqualTo(Date::class.java)
        assertThat(booleanOperation.args[1]).extracting("mixin").extracting("constant").isEqualTo(endOfDay)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    @Test
    fun in_with_single_value() {
        val expr = mock<Expression<*>>()
        val v = "myvalue"
        val values = listOf(v)
        val predicate = mongoQueryBuilder.`in`(expr, values)

        assertThat(predicate).isInstanceOf(BooleanOperation::class.java)

        val booleanOperation = predicate as BooleanOperation
        assertThat(booleanOperation.operator).isEqualTo(Ops.EQ)
        assertThat(booleanOperation.args).hasSize(2)
        assertThat(booleanOperation.args[0]).isSameAs(expr)
        assertThat(booleanOperation.args[1]).extracting("constant").isEqualTo(v)

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
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

        verifyZeroInteractions(query)
        verifyZeroInteractions(searchyContext)
        verifyZeroInteractions(qEntityRoot)
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun get_parent_path(): Stream<Arguments> {
            return Stream.of(
                // No parent
                Arguments.of(null),
                // Parent that is root
                Arguments.of(mock<Path<*>> {
                    val pathMetadata = mock<PathMetadata> {
                        on { this.isRoot }.thenReturn(true)
                    }
                    on { this.metadata }.thenReturn(pathMetadata)
                })
            )
        }
    }
}