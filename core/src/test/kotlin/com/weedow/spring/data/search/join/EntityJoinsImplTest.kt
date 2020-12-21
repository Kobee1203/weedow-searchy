package com.weedow.spring.data.search.join

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.querydsl.core.JoinType
import com.querydsl.core.types.Path
import com.weedow.spring.data.search.common.model.Address
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.common.model.Vehicle
import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import com.weedow.spring.data.search.querydsl.querytype.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class EntityJoinsImplTest {

    @Test
    fun check_already_processed_when_property_infos_matches_root_entity_class() {
        val entityJoins = EntityJoinsImpl(Person::class.java)

        // Root class
        val propertyInfos: PropertyInfos = mock {
            on { this.elementType }.thenReturn(ElementType.SET)
            on { this.parameterizedTypes }.thenReturn(listOf(Person::class.java))
        }
        assertThat(entityJoins.alreadyProcessed(propertyInfos)).isTrue
    }

    @Test
    fun check_already_processed_when_property_infos_matches_entity_join() {
        val entityJoins = EntityJoinsImpl(Person::class.java)

        val entityJoin = EntityJoin("addressEntities", "addressEntities", Address::class.java.canonicalName)
        entityJoins.add(entityJoin)

        // Join already added
        val propertyInfos = mock<PropertyInfos> {
            on { qName }.thenReturn(Address::class.java.canonicalName)
            on { elementType }.thenReturn(ElementType.SET)
            on { parameterizedTypes }.thenReturn(listOf<Class<*>>(Address::class.java))
        }
        assertThat(entityJoins.alreadyProcessed(propertyInfos)).isTrue
    }

    @Test
    fun check_not_already_processed_when_property_infos_does_not_match_entity_join() {
        val entityJoins = EntityJoinsImpl(Person::class.java)

        // Join not already processed
        val propertyInfos = mock<PropertyInfos> {
            on { qName }.thenReturn(Vehicle::class.java.canonicalName)
            on { elementType }.thenReturn(ElementType.SET)
            on { parameterizedTypes }.thenReturn(listOf<Class<*>>(Vehicle::class.java))
        }
        assertThat(entityJoins.alreadyProcessed(propertyInfos)).isFalse
    }

    @Test
    fun get_qpath_when_fieldpath_is_simple_field() {
        val entityJoins = EntityJoinsImpl(Person::class.java)

        val qPath = mock<QPath<*>>()

        val qEntityRoot = mock<QEntityRoot<Person>> {
            on { this.get("firstName") }.thenReturn(qPath)
        }

        val queryDslBuilder = mock<QueryDslBuilder<Person>>()

        val result = entityJoins.getQPath("firstName", qEntityRoot, queryDslBuilder)

        assertThat(result).isSameAs(qPath)
        verifyNoMoreInteractions(queryDslBuilder)
    }

    @Test
    fun get_qpath_when_fieldpath_is_composed_with_parent_and_field() {
        val entityJoins = EntityJoinsImpl(Person::class.java)

        val qPath = mock<QPath<*>>()

        val qPathParent = mock<QPath<*>> {
            val path = mock<Path<*>> {
                on { this.toString() }.thenReturn("addressEntities")
            }
            val propertyInfos = mock<PropertyInfos> {
                on { this.qName }.thenReturn(Address::class.java.canonicalName)
            }
            on { this.path }.thenReturn(path)
            on { this.propertyInfos }.thenReturn(propertyInfos)
        }

        val qEntityRoot = mock<QEntityRoot<Person>> {
            on { get("addressEntities") }.thenReturn(qPathParent)
        }
        val queryDslBuilder = mock<QueryDslBuilder<Person>> {
            val join = mock<QEntityJoin<*>> {
                on { get("city") }.thenReturn(qPath)
            }
            on { this.join(qPathParent, JoinType.LEFTJOIN, false) }.thenReturn(join)
        }

        val result = entityJoins.getQPath("addressEntities.city", qEntityRoot, queryDslBuilder)

        assertThat(result).isSameAs(qPath)
        verifyNoMoreInteractions(queryDslBuilder)
    }

    @Test
    fun get_qpath_when_fieldpath_is_composed_with_parent_and_field_and_entity_join_already_present() {
        val entityJoins = EntityJoinsImpl(Person::class.java)

        val qName = Address::class.java.canonicalName
        val joinType = JoinType.INNERJOIN
        val fetched = true

        val entityJoin = EntityJoin("addressEntities", "addressEntities", qName, joinType, fetched)
        entityJoins.add(entityJoin)

        val qPath = mock<QPath<*>>()

        val qPathParent = mock<QPath<*>> {
            val propertyInfos = mock<PropertyInfos> {
                on { this.qName }.thenReturn(qName)
            }
            on { this.propertyInfos }.thenReturn(propertyInfos)
        }

        val qEntityRoot = mock<QEntityRoot<Person>> {
            on { get("addressEntities") }.thenReturn(qPathParent)
        }
        val queryDslBuilder = mock<QueryDslBuilder<Person>> {
            val join = mock<QEntityJoin<*>> {
                on { get("city") }.thenReturn(qPath)
            }
            on { this.join(qPathParent, joinType, fetched) }.thenReturn(join)
        }

        val result = entityJoins.getQPath("addressEntities.city", qEntityRoot, queryDslBuilder)

        assertThat(result).isSameAs(qPath)
        verifyNoMoreInteractions(queryDslBuilder)
        verifyNoMoreInteractions(qPathParent)
    }
}