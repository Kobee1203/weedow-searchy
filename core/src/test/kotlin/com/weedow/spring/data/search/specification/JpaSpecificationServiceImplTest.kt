package com.weedow.spring.data.search.specification

import com.neovisionaries.i18n.CountryCode
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.example.model.Address
import com.weedow.spring.data.search.example.model.Person
import com.weedow.spring.data.search.field.FieldInfo
import com.weedow.spring.data.search.join.DefaultEntityJoinHandler
import com.weedow.spring.data.search.join.EntityJoinHandler
import com.weedow.spring.data.search.join.EntityJoinManager
import com.weedow.spring.data.search.utils.NullValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import javax.persistence.criteria.*

@ExtendWith(MockitoExtension::class)
internal class JpaSpecificationServiceImplTest {

    @Mock
    private lateinit var entityJoinManager: EntityJoinManager

    @InjectMocks
    private lateinit var jpaSpecificationService: JpaSpecificationServiceImpl

    private lateinit var entityJoinHandlers: List<EntityJoinHandler<Person>>

    private lateinit var root: Root<Person>
    private lateinit var query: CriteriaQuery<*>
    private lateinit var criteriaBuilder: CriteriaBuilder

    @BeforeEach
    fun setUp() {
        val entityJoinHandler = mock<EntityJoinHandler<Person>>()
        entityJoinHandlers = listOf(entityJoinHandler, DefaultEntityJoinHandler())

        root = mock()
        query = mock()
        criteriaBuilder = mock()
    }

    @Test
    fun create_specification_for_a_field_with_a_single_value() {
        // GIVEN

        val entityClass = Person::class.java
        val parentClass = Person::class.java
        val fieldPath = "firstName"
        val fieldName = "firstName"
        val fieldValue = "John"

        val fieldInfos = listOf(
                FieldInfo(fieldPath, parentClass, parentClass.getDeclaredField(fieldName), String::class.java, listOf(fieldValue))
        )

        val expressionFirstName = mock<Path<Any>>()
        whenever(root.get<Any>(fieldName)).thenReturn(expressionFirstName)

        val joinMap = emptyMap<String, From<*, *>>()
        whenever(entityJoinManager.computeJoinMap(root, entityClass, entityJoinHandlers)).thenReturn(joinMap)

        val predicateFirstName = mock<Predicate>()
        whenever(criteriaBuilder.equal(expressionFirstName, fieldValue)).thenReturn(predicateFirstName)

        val finalPredicate = mock<Predicate>()
        whenever(criteriaBuilder.and(predicateFirstName)).thenReturn(finalPredicate)

        // WHEN

        val specification = jpaSpecificationService.createSpecification(fieldInfos, entityClass, entityJoinHandlers)
        assertThat(specification).isNotNull()

        val predicate = specification.toPredicate(root, query, criteriaBuilder)
        assertThat(predicate).isNotNull()
        assertThat(predicate).isEqualTo(finalPredicate)

        // THEN

        verify(query).distinct(true)
    }

    @Test
    fun create_specification_for_a_ElementCollection_field_with_a_single_value() {
        // GIVEN

        val entityClass = Person::class.java
        val parentClass = Person::class.java
        val fieldPath = "nickNames"
        val fieldName = "nickNames"
        val fieldValue = "John"

        val fieldInfos = listOf(
                FieldInfo(fieldPath, parentClass, parentClass.getDeclaredField(fieldName), String::class.java, listOf(fieldValue))
        )

        val expressionNickNames = mock<Join<Collection<*>, Collection<*>>>()

        val joinMap = mapOf<String, From<*, *>>("nickNames" to expressionNickNames)
        whenever(entityJoinManager.computeJoinMap(root, entityClass, entityJoinHandlers)).thenReturn(joinMap)

        val predicateNickNames = mock<Predicate>()
        whenever(criteriaBuilder.isMember(fieldValue, expressionNickNames)).thenReturn(predicateNickNames)

        val finalPredicate = mock<Predicate>()
        whenever(criteriaBuilder.and(predicateNickNames)).thenReturn(finalPredicate)

        // WHEN

        val specification = jpaSpecificationService.createSpecification(fieldInfos, entityClass, entityJoinHandlers)
        assertThat(specification).isNotNull()

        val predicate = specification.toPredicate(root, query, criteriaBuilder)
        assertThat(predicate).isNotNull()
        assertThat(predicate).isEqualTo(finalPredicate)

        // THEN

        verify(query).distinct(true)
    }

    @Test
    fun create_specification_for_a_field_with_a_null_value() {
        // GIVEN

        val entityClass = Person::class.java
        val parentClass = Person::class.java
        val fieldPath = "firstName"
        val fieldName = "firstName"
        val fieldValue = NullValue.INSTANCE

        val fieldInfos = listOf(
                FieldInfo(fieldPath, parentClass, parentClass.getDeclaredField(fieldName), String::class.java, listOf(fieldValue))
        )

        val expressionFirstName = mock<Path<Any>>()
        whenever(root.get<Any>(fieldName)).thenReturn(expressionFirstName)

        val joinMap = emptyMap<String, From<*, *>>()
        whenever(entityJoinManager.computeJoinMap(root, entityClass, entityJoinHandlers)).thenReturn(joinMap)

        val predicateFirstName = mock<Predicate>()
        whenever(criteriaBuilder.isNull(expressionFirstName)).thenReturn(predicateFirstName)

        val finalPredicate = mock<Predicate>()
        whenever(criteriaBuilder.and(predicateFirstName)).thenReturn(finalPredicate)

        // WHEN

        val specification = jpaSpecificationService.createSpecification(fieldInfos, entityClass, entityJoinHandlers)
        assertThat(specification).isNotNull()

        val predicate = specification.toPredicate(root, query, criteriaBuilder)
        assertThat(predicate).isNotNull()
        assertThat(predicate).isEqualTo(finalPredicate)

        // THEN

        verify(query).distinct(true)
    }

    @Test
    fun create_specification_for_a_collection_field_with_a_null_value() {
        // GIVEN

        val entityClass = Person::class.java
        val parentClass = Person::class.java
        val fieldPath = "nickNames"
        val fieldName = "nickNames"
        val fieldValue = NullValue.INSTANCE

        val fieldInfos = listOf(
                FieldInfo(fieldPath, parentClass, parentClass.getDeclaredField(fieldName), String::class.java, listOf(fieldValue))
        )

        val expressionNickNames = mock<Path<Collection<*>>>()
        whenever(root.get<Collection<*>>(fieldName)).thenReturn(expressionNickNames)

        val joinMap = emptyMap<String, From<*, *>>()
        whenever(entityJoinManager.computeJoinMap(root, entityClass, entityJoinHandlers)).thenReturn(joinMap)

        val predicateNickNames = mock<Predicate>()
        whenever(criteriaBuilder.isEmpty(expressionNickNames)).thenReturn(predicateNickNames)

        val finalPredicate = mock<Predicate>()
        whenever(criteriaBuilder.and(predicateNickNames)).thenReturn(finalPredicate)

        // WHEN

        val specification = jpaSpecificationService.createSpecification(fieldInfos, entityClass, entityJoinHandlers)
        assertThat(specification).isNotNull()

        val predicate = specification.toPredicate(root, query, criteriaBuilder)
        assertThat(predicate).isNotNull()
        assertThat(predicate).isEqualTo(finalPredicate)

        // THEN

        verify(query).distinct(true)
    }

    @Test
    fun create_specification_for_a_field_with_a_multiple_values() {
        // GIVEN

        val entityClass = Person::class.java
        val parentClass = Person::class.java
        val fieldPath = "firstName"
        val fieldName = "firstName"
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"

        val fieldInfos = listOf(
                FieldInfo(fieldPath, parentClass, parentClass.getDeclaredField(fieldName), String::class.java, listOf(fieldValue1, fieldValue2))
        )

        val expressionFirstName = mock<Path<Any>>()
        whenever(root.get<Any>(fieldName)).thenReturn(expressionFirstName)

        val joinMap = emptyMap<String, From<*, *>>()
        whenever(entityJoinManager.computeJoinMap(root, entityClass, entityJoinHandlers)).thenReturn(joinMap)

        val inClause = mock<CriteriaBuilder.In<Any>>()
        whenever(criteriaBuilder.`in`(expressionFirstName)).thenReturn(inClause)

        val finalPredicate = mock<Predicate>()
        whenever(criteriaBuilder.and(inClause)).thenReturn(finalPredicate)

        // WHEN

        val specification = jpaSpecificationService.createSpecification(fieldInfos, entityClass, entityJoinHandlers)
        assertThat(specification).isNotNull()

        val predicate = specification.toPredicate(root, query, criteriaBuilder)
        assertThat(predicate).isNotNull()
        assertThat(predicate).isEqualTo(finalPredicate)

        // THEN

        verify(query).distinct(true)
        verify(inClause).value(fieldValue1)
        verify(inClause).value(fieldValue2)
    }

    @Test
    fun create_specification_for_a_field_in_related_entity_with_a_single_value() {
        // GIVEN

        val entityClass = Person::class.java
        val parentClass = Address::class.java
        val fieldPath = "addressEntities.country"
        val fieldName = "country"
        val fieldValue = CountryCode.FR

        val fieldInfos = listOf(
                FieldInfo(fieldPath, parentClass, parentClass.getDeclaredField(fieldName), String::class.java, listOf(fieldValue))
        )

        val addressFrom = mock<From<*, *>>()

        val expressionFirstName = mock<Path<Any>>()
        whenever(addressFrom.get<Any>(fieldName)).thenReturn(expressionFirstName)

        val joinMap = mapOf<String, From<*, *>>(Address::class.java.canonicalName to addressFrom)
        whenever(entityJoinManager.computeJoinMap(root, entityClass, entityJoinHandlers)).thenReturn(joinMap)

        val predicateFirstName = mock<Predicate>()
        whenever(criteriaBuilder.equal(expressionFirstName, fieldValue)).thenReturn(predicateFirstName)

        val finalPredicate = mock<Predicate>()
        whenever(criteriaBuilder.and(predicateFirstName)).thenReturn(finalPredicate)

        // WHEN

        val specification = jpaSpecificationService.createSpecification(fieldInfos, entityClass, entityJoinHandlers)
        assertThat(specification).isNotNull()

        val predicate = specification.toPredicate(root, query, criteriaBuilder)
        assertThat(predicate).isNotNull()
        assertThat(predicate).isEqualTo(finalPredicate)

        // THEN

        verify(query).distinct(true)
    }
}