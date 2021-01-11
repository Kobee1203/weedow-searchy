package com.weedow.searchy.join

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.querydsl.core.JoinType
import com.querydsl.core.types.Path
import com.weedow.searchy.common.model.Address
import com.weedow.searchy.common.model.Person
import com.weedow.searchy.common.model.Vehicle
import com.weedow.searchy.query.QueryBuilder
import com.weedow.searchy.query.querytype.*
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

        val entityJoin = EntityJoin("addressEntities", Address::class.java.canonicalName)
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

        val qPath = mock<QPath<*>> {
            val propertyInfos = mock<PropertyInfos> {
                on { this.qName }.thenReturn("firstName")
            }
            on { this.propertyInfos }.thenReturn(propertyInfos)
        }

        val qEntityRoot = mock<QEntityRoot<Person>> {
            on { this.get("firstName") }.thenReturn(qPath)
        }

        val queryBuilder = mock<QueryBuilder<Person>>()

        val result = entityJoins.getQPath("firstName", qEntityRoot, queryBuilder)

        assertThat(result).isSameAs(qPath)
        verifyNoMoreInteractions(queryBuilder)
    }

    @Test
    fun get_qpath_when_fieldpath_is_composed_with_parent_and_field() {
        val entityJoins = EntityJoinsImpl(Person::class.java)

        val qPath = mock<QPath<*>> {
            val propertyInfos = mock<PropertyInfos> {
                on { this.qName }.thenReturn("addressEntities.city")
            }
            on { this.propertyInfos }.thenReturn(propertyInfos)
        }

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
        val queryBuilder = mock<QueryBuilder<Person>> {
            val join = mock<QEntityJoin<*>> {
                on { get("city") }.thenReturn(qPath)
            }
            on { this.join(qPathParent, JoinType.LEFTJOIN, false) }.thenReturn(join)
        }

        val result = entityJoins.getQPath("addressEntities.city", qEntityRoot, queryBuilder)

        assertThat(result).isSameAs(qPath)
        verifyNoMoreInteractions(queryBuilder)
    }

    @Test
    fun get_qpath_when_fieldpath_is_composed_with_parent_and_field_and_entity_join_already_present() {
        val entityJoins = EntityJoinsImpl(Person::class.java)

        val qName1 = Address::class.java.canonicalName
        val joinType1 = JoinType.INNERJOIN
        val fetched1 = true
        val qName2 = Address::class.java.canonicalName
        val joinType2 = JoinType.INNERJOIN
        val fetched2 = true

        val entityJoin1 = EntityJoin("addressEntities", qName1, joinType1, fetched1)
        entityJoins.add(entityJoin1)
        val entityJoin2 = EntityJoin("addressEntities.city", qName2, joinType2, fetched2)
        entityJoins.add(entityJoin2)

        val qPath = mock<QPath<*>> {
            val propertyInfos = mock<PropertyInfos> {
                on { this.qName }.thenReturn(qName2)
            }
            on { this.propertyInfos }.thenReturn(propertyInfos)
        }

        val qPathParent = mock<QPath<*>> {
            val parentPropertyInfos = mock<PropertyInfos> {
                on { this.qName }.thenReturn(qName1)
            }
            on { this.propertyInfos }.thenReturn(parentPropertyInfos)
        }

        val qEntityRoot = mock<QEntityRoot<Person>> {
            on { get("addressEntities") }.thenReturn(qPathParent)
        }
        val queryBuilder = mock<QueryBuilder<Person>> {
            val join = mock<QEntityJoin<*>> {
                on { get("city") }.thenReturn(qPath)
            }
            on { this.join(qPathParent, joinType1, fetched1) }.thenReturn(join)
        }

        val result = entityJoins.getQPath("addressEntities.city", qEntityRoot, queryBuilder)

        assertThat(result).isSameAs(qPath)
        verify(queryBuilder).join(qPath, joinType2, fetched2)
        verifyNoMoreInteractions(queryBuilder)
        verifyNoMoreInteractions(qPathParent)
    }
}