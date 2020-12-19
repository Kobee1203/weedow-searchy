package com.weedow.spring.data.search.join

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.JoinType
import com.weedow.spring.data.search.common.model.Address
import com.weedow.spring.data.search.common.model.Feature
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.common.model.Vehicle
import com.weedow.spring.data.search.utils.MAP_KEY
import com.weedow.spring.data.search.utils.MAP_VALUE
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.query.criteria.internal.JoinImplementor
import org.hibernate.query.criteria.internal.MapJoinImplementor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import javax.persistence.criteria.Path
import javax.persistence.criteria.Root
import javax.persistence.metamodel.Attribute

@ExtendWith(MockitoExtension::class)
internal class JpaEntityJoinsImplTest {

    companion object {
        private const val ADDRESS_ENTITIES_FIELD = "addressEntities"
        private const val FIRST_NAME_FIELD = "firstName"
        private const val VEHICLES_FIELD = "vehicles"
        private const val FEATURES_FIELD = "features"
        private const val FEATURE_NAME_FIELD = "name"
        private const val COUNTRY_FIELD = "country"
        private const val NICK_NAMES_FIELD = "nickNames"
        private const val CHARACTERISTICS_FIELD = "characteristics"
        private const val COUNTRY_PATH = "addressEntities.country"
    }

    @Test
    fun get_path_for_simple_field() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val expectedPath = mock<Path<Any>>()

        val root = mock<Root<Person>>()
        whenever(root.javaType).thenReturn(rootClass)
        whenever(root.get<Any>(FIRST_NAME_FIELD)).thenReturn(expectedPath)

        val path = entityJoins.getPath(FIRST_NAME_FIELD, root)

        assertThat(path).isEqualTo(expectedPath)

        verifyNoMoreInteractions(root)
    }

    @Test
    fun get_path_for_inner_join_field() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val expectedPath = mock<Path<Any>>()

        val root = mock<Root<Person>>()
        whenever(root.javaType).thenReturn(rootClass)
        whenever(root.join<Any, Any>(ADDRESS_ENTITIES_FIELD, javax.persistence.criteria.JoinType.INNER)).thenReturn(mock<JoinImplementor<Any, Any>>())
        whenever(root.get<Any>(ADDRESS_ENTITIES_FIELD)).thenReturn(expectedPath)

        val entityJoin = EntityJoin(ADDRESS_ENTITIES_FIELD, ADDRESS_ENTITIES_FIELD, Address::class.java.canonicalName, JoinType.INNERJOIN)
        entityJoins.add(entityJoin)

        val path = entityJoins.getPath(ADDRESS_ENTITIES_FIELD, root)

        assertThat(path).isEqualTo(expectedPath)

        verify(root).joins
        verify(root).fetches
        verifyNoMoreInteractions(root)
    }

    @Test
    fun get_path_for_entity_as_map_key() {
        // TODO UPDATE ENTITIES TO TEST THE CASE
    }

    @Test
    fun get_path_for_entity_as_map_value() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val expectedPath = mock<Path<Any>>()

        val root = mock<Root<Person>>()
        whenever(root.javaType).thenReturn(rootClass)

        val vehicleJoin = mock<JoinImplementor<Any, Any>>()
        whenever(root.join<Any, Any>(VEHICLES_FIELD, javax.persistence.criteria.JoinType.LEFT)).thenReturn(vehicleJoin)
        whenever(vehicleJoin.javaType).thenReturn(Vehicle::class.java)

        val mapJoin = mock<MapJoinImplementor<Any, Any, Any>>()
        whenever(vehicleJoin.join<Any, Any>(FEATURES_FIELD, javax.persistence.criteria.JoinType.LEFT)).thenReturn(mapJoin)

        val mapValuePath = mock<Path<Any>>()
        whenever(mapJoin.value()).thenReturn(mapValuePath)
        whenever(mapValuePath.javaType).thenReturn(Feature::class.java)
        whenever(mapValuePath.get<Any>(FEATURE_NAME_FIELD)).thenReturn(expectedPath)

        val path = entityJoins.getPath("$VEHICLES_FIELD.$FEATURES_FIELD.value.$FEATURE_NAME_FIELD", root)

        assertThat(path).isEqualTo(expectedPath)

        verify(root).joins
        verify(root).fetches
        verifyNoMoreInteractions(root)
    }

    @Test
    fun get_path_for_map_key_field() {
        get_path_for_map_field(MAP_KEY) { mapJoin: MapJoinImplementor<Any, Any, Any>, expectedPath: Path<Any> ->
            whenever(mapJoin.key()).thenReturn(expectedPath)
        }
    }

    @Test
    fun get_path_for_map_value_field() {
        get_path_for_map_field(MAP_VALUE) { mapJoin: MapJoinImplementor<Any, Any, Any>, expectedPath: Path<Any> ->
            whenever(mapJoin.value()).thenReturn(expectedPath)
        }
    }

    private fun get_path_for_map_field(specialKey: String, stubbingMethod: (MapJoinImplementor<Any, Any, Any>, Path<Any>) -> Unit) {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val expectedPath = mock<Path<Any>>()

        val root = mock<Root<Person>>()
        whenever(root.javaType).thenReturn(rootClass)
        val mapJoin = mock<MapJoinImplementor<Any, Any, Any>>()
        whenever(root.join<Any, Any>(CHARACTERISTICS_FIELD, javax.persistence.criteria.JoinType.LEFT)).thenReturn(mapJoin)
        // Custom stubbing method
        stubbingMethod(mapJoin, expectedPath)

        val path = entityJoins.getPath("$CHARACTERISTICS_FIELD.$specialKey", root)

        assertThat(path).isEqualTo(expectedPath)

        verify(root).joins
        verify(root).fetches
        verifyNoMoreInteractions(root)
    }

    @Test
    fun throw_exception_when_field_path_invalid_for_map() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val root = mock<Root<Person>>()
        whenever(root.javaType).thenReturn(rootClass)
        val mapJoin = mock<MapJoinImplementor<Any, Any, Any>>()
        whenever(root.join<Any, Any>(CHARACTERISTICS_FIELD, javax.persistence.criteria.JoinType.LEFT)).thenReturn(mapJoin)

        val fieldPath = "$CHARACTERISTICS_FIELD.unknown"
        Assertions.assertThatThrownBy { entityJoins.getPath(fieldPath, root) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageStartingWith("The attribute name 'unknown' is not authorized for a parent Map Join")
            .hasNoCause()

        verify(root).joins
        verify(root).fetches
        verifyNoMoreInteractions(root)
    }

    @Test
    fun get_path_for_left_join_field() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val expectedPath = mock<Path<Any>>()

        val root = mock<Root<Person>>()
        whenever(root.javaType).thenReturn(rootClass)
        whenever(root.fetch<Any, Any>(ADDRESS_ENTITIES_FIELD, javax.persistence.criteria.JoinType.LEFT)).thenReturn(mock<JoinImplementor<Any, Any>>())
        whenever(root.get<Any>(ADDRESS_ENTITIES_FIELD)).thenReturn(expectedPath)

        val entityJoin = EntityJoin(ADDRESS_ENTITIES_FIELD, ADDRESS_ENTITIES_FIELD, Address::class.java.canonicalName, JoinType.LEFTJOIN, true)
        entityJoins.add(entityJoin)

        val path = entityJoins.getPath(ADDRESS_ENTITIES_FIELD, root)

        assertThat(path).isEqualTo(expectedPath)

        verify(root).joins
        verify(root).fetches
        verifyNoMoreInteractions(root)
    }

    @Test
    fun get_path_for_field_in_sub_parent() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val expectedPath = mock<Path<Any>>()

        val root = mock<Root<Person>>()
        whenever(root.javaType).thenReturn(rootClass)
        val join = mock<JoinImplementor<Any, Any>>()
        whenever(root.fetch<Any, Any>(ADDRESS_ENTITIES_FIELD, javax.persistence.criteria.JoinType.LEFT)).thenReturn(join)
        whenever(join.javaType).thenReturn(Address::class.java)
        whenever(join.get<Any>(COUNTRY_FIELD)).thenReturn(expectedPath)

        val entityJoin = EntityJoin(ADDRESS_ENTITIES_FIELD, ADDRESS_ENTITIES_FIELD, Address::class.java.canonicalName, JoinType.LEFTJOIN, true)
        entityJoins.add(entityJoin)

        val path = entityJoins.getPath(COUNTRY_PATH, root)

        assertThat(path).isEqualTo(expectedPath)

        verify(root).joins
        verify(root).fetches
        verifyNoMoreInteractions(root)
    }

    @Test
    fun get_path_for_field_in_sub_parent_with_default_EntityJoin() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val expectedPath = mock<Path<Any>>()

        val root = mock<Root<Person>>()
        whenever(root.javaType).thenReturn(rootClass)
        val join = mock<JoinImplementor<Any, Any>>()
        whenever(root.join<Any, Any>(ADDRESS_ENTITIES_FIELD, javax.persistence.criteria.JoinType.LEFT)).thenReturn(join)
        whenever(join.javaType).thenReturn(Address::class.java)
        whenever(join.get<Any>(COUNTRY_FIELD)).thenReturn(expectedPath)

        val path = entityJoins.getPath(COUNTRY_PATH, root)

        assertThat(path).isEqualTo(expectedPath)

        verify(root).joins
        verify(root).fetches
        verifyNoMoreInteractions(root)
    }

    @Test
    fun get_path_for_field_in_sub_parent_with_inner_join_already_present_in_parent_join() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val expectedPath = mock<Path<Any>>()

        val root = mock<Root<Person>>()
        whenever(root.javaType).thenReturn(rootClass)

        val join = mock<JoinImplementor<Person, Any>>()
        whenever(root.joins).thenReturn(setOf(join))

        whenever(join.javaType).thenReturn(Address::class.java)
        whenever(join.get<Any>(COUNTRY_FIELD)).thenReturn(expectedPath)

        val attribute = mock<Attribute<in Person, *>>()
        whenever(join.attribute).thenReturn(attribute)

        whenever(attribute.name).thenReturn(ADDRESS_ENTITIES_FIELD)
        whenever(join.joinType).thenReturn(javax.persistence.criteria.JoinType.INNER)

        val entityJoin = EntityJoin(ADDRESS_ENTITIES_FIELD, ADDRESS_ENTITIES_FIELD, Address::class.java.canonicalName, JoinType.INNERJOIN, true)
        entityJoins.add(entityJoin)

        val path = entityJoins.getPath(COUNTRY_PATH, root)

        assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun get_path_for_field_in_sub_parent_with_left_join_already_present_in_parent_join() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val expectedPath = mock<Path<Any>>()

        val root = mock<Root<Person>>()
        whenever(root.javaType).thenReturn(rootClass)

        val join = mock<JoinImplementor<Person, Any>>()
        whenever(root.fetches).thenReturn(setOf(join))

        whenever(join.javaType).thenReturn(Address::class.java)
        whenever(join.get<Any>(COUNTRY_FIELD)).thenReturn(expectedPath)

        val attribute = mock<Attribute<in Person, *>>()
        whenever(join.attribute).thenReturn(attribute)

        whenever(attribute.name).thenReturn(ADDRESS_ENTITIES_FIELD)
        whenever(join.joinType).thenReturn(javax.persistence.criteria.JoinType.LEFT)

        val entityJoin = EntityJoin(ADDRESS_ENTITIES_FIELD, ADDRESS_ENTITIES_FIELD, Address::class.java.canonicalName, JoinType.LEFTJOIN, true)
        entityJoins.add(entityJoin)

        val path = entityJoins.getPath(COUNTRY_PATH, root)

        assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun get_joins_when_no_join_is_added() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        assertThat(entityJoins.getJoins()).isEmpty()
    }

    @Test
    fun get_joins_when_any_joins_are_added() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val entityJoin1 = EntityJoin(ADDRESS_ENTITIES_FIELD, ADDRESS_ENTITIES_FIELD, Address::class.java.canonicalName, JoinType.INNERJOIN)
        entityJoins.add(entityJoin1)
        val entityJoin2 = EntityJoin(VEHICLES_FIELD, VEHICLES_FIELD, Vehicle::class.java.canonicalName, JoinType.LEFTJOIN, true)
        entityJoins.add(entityJoin2)
        val entityJoin3 =
            EntityJoin(NICK_NAMES_FIELD, NICK_NAMES_FIELD, Person::class.java.canonicalName + "." + NICK_NAMES_FIELD, JoinType.LEFTJOIN, true)
        entityJoins.add(entityJoin3)

        val joins = entityJoins.getJoins()
        assertThat(joins).containsExactlyInAnyOrderEntriesOf(
            mutableMapOf(
                Address::class.java.canonicalName to entityJoin1,
                Vehicle::class.java.canonicalName to entityJoin2,
                Person::class.java.canonicalName + "." + NICK_NAMES_FIELD to entityJoin3
            )
        )
    }

    @Test
    fun get_joins_by_using_filter() {
        val rootClass = Person::class.java
        val entityJoins = EntityJoinsImpl(rootClass)

        val entityJoin1 = EntityJoin(ADDRESS_ENTITIES_FIELD, ADDRESS_ENTITIES_FIELD, Address::class.java.canonicalName, JoinType.INNERJOIN)
        entityJoins.add(entityJoin1)
        val entityJoin2 = EntityJoin(VEHICLES_FIELD, VEHICLES_FIELD, Vehicle::class.java.canonicalName, JoinType.LEFTJOIN, true)
        entityJoins.add(entityJoin2)
        val entityJoin3 =
            EntityJoin(NICK_NAMES_FIELD, NICK_NAMES_FIELD, Person::class.java.canonicalName + "." + NICK_NAMES_FIELD, JoinType.LEFTJOIN, true)
        entityJoins.add(entityJoin3)

        assertThat(entityJoins.getJoins { it.fetched }).containsExactlyInAnyOrderEntriesOf(
            mutableMapOf(
                Vehicle::class.java.canonicalName to entityJoin2,
                Person::class.java.canonicalName + "." + NICK_NAMES_FIELD to entityJoin3
            )
        )

        assertThat(entityJoins.getJoins { it.joinType == JoinType.INNERJOIN }).containsExactlyInAnyOrderEntriesOf(
            mutableMapOf(
                Address::class.java.canonicalName to entityJoin1
            )
        )

        assertThat(entityJoins.getJoins { it.joinName.contains("address", true) }).containsExactlyInAnyOrderEntriesOf(
            mutableMapOf(
                Address::class.java.canonicalName to entityJoin1
            )
        )
    }
}