package com.weedow.spring.data.search.join

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.example.model.Address
import com.weedow.spring.data.search.example.model.Job
import com.weedow.spring.data.search.example.model.Person
import com.weedow.spring.data.search.example.model.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.hibernate.query.criteria.internal.JoinImplementor
import org.junit.jupiter.api.Test
import javax.persistence.criteria.Join
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Root

internal class EntityJoinManagerImplTest {

    @Test
    fun computeJoinMap_with_inner_joins() {
        val entityJoinManager = EntityJoinManagerImpl()

        val root = mock<Root<Person>>()
        val entityJoinHandlers = listOf<EntityJoinHandler<Person>>(DefaultEntityJoinHandler())

        val mockNickNamesJoin = mock<Join<Any, Any>>()
        val mockPhoneNumberJoin = mock<Join<Any, Any>>()
        val mockAddressEntitiesJoin = mock<Join<Any, Any>>()
        val mockJobEntityJoin = mock<Join<Any, Any>>()
        val mockVehiclesJoin = mock<Join<Any, Any>>()
        whenever(root.join<Any, Any>("nickNames", JoinType.INNER)).thenReturn(mockNickNamesJoin)
        whenever(root.join<Any, Any>("phoneNumbers", JoinType.INNER)).thenReturn(mockPhoneNumberJoin)
        whenever(root.join<Any, Any>("addressEntities", JoinType.INNER)).thenReturn(mockAddressEntitiesJoin)
        whenever(root.join<Any, Any>("jobEntity", JoinType.INNER)).thenReturn(mockJobEntityJoin)
        whenever(root.join<Any, Any>("vehicles", JoinType.INNER)).thenReturn(mockVehiclesJoin)

        val joinMap = entityJoinManager.computeJoinMap(root, Person::class.java, entityJoinHandlers)

        assertThat(joinMap).containsOnly(
                entry("nickNames", mockNickNamesJoin),
                entry("phoneNumbers", mockPhoneNumberJoin),
                entry(Address::class.java.canonicalName, mockAddressEntitiesJoin),
                entry(Job::class.java.canonicalName, mockJobEntityJoin),
                entry(Vehicle::class.java.canonicalName, mockVehiclesJoin)
        )
    }

    @Test
    fun computeJoinMap_with_left_joins() {
        val entityJoinManager = EntityJoinManagerImpl()

        val root = mock<Root<Vehicle>>()
        val entityJoinHandlers = listOf<EntityJoinHandler<Vehicle>>(FetchingEagerEntityJoinHandler())

        val mockPersonJoin = mock<JoinImplementor<Any, Any>>()
        val mockJobJoin = mock<JoinImplementor<Any, Any>>()
        whenever(root.fetch<Any, Any>("person", JoinType.LEFT)).thenReturn(mockPersonJoin)
        whenever(mockPersonJoin.fetch<Any, Any>("jobEntity", JoinType.LEFT)).thenReturn(mockJobJoin)

        val joinMap = entityJoinManager.computeJoinMap(root, Vehicle::class.java, entityJoinHandlers)

        assertThat(joinMap).containsOnly(
                entry(Person::class.java.canonicalName, mockPersonJoin),
                entry(Job::class.java.canonicalName, mockJobJoin)
        )
    }

    @Test
    fun computeJoinMap_when_no_EntityJoinHandler() {
        val entityJoinManager = EntityJoinManagerImpl()

        val entityJoinHandlers = listOf<EntityJoinHandler<Person>>()
        val joinMap = entityJoinManager.computeJoinMap(mock(), Person::class.java, entityJoinHandlers)

        assertThat(joinMap).isEmpty()
    }
}