package com.weedow.spring.data.search.dto

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.common.model.Vehicle
import com.weedow.spring.data.search.common.model.VehicleType
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.collection.internal.PersistentMap
import org.hibernate.collection.internal.PersistentSet
import org.hibernate.collection.spi.PersistentCollection
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.loader.CollectionAliases
import org.hibernate.persister.collection.CollectionPersister
import org.hibernate.type.MapType
import org.hibernate.type.SetType
import org.junit.jupiter.api.Test
import java.sql.ResultSet

internal class DefaultDtoMapperTest {

    @Test
    fun map_entity_to_dto() {
        val dtoMapper = DefaultDtoMapper<Person>()

        val source = Person("John", "Doe")
        val result = dtoMapper.map(source)

        assertThat(result).isSameAs(source)
    }

    @Test
    fun map_entity_to_dto_with_lazy_loading_of_set() {
        val dtoMapper = DefaultDtoMapper<Person>()

        val session = createSession()
        val persistentVehicles = PersistentSet(session)

        @Suppress("UNCHECKED_CAST")
        val source = Person("John", "Doe", vehicles = persistentVehicles as Set<Vehicle>)

        val vehicle1 = Vehicle(VehicleType.CAR, "Renault", "Clio E-Tech", source)
        setId(vehicle1, 1L)
        val vehicle2 = Vehicle(VehicleType.MOTORBIKE, "Harley-Davidson", "Livewire", source)
        setId(vehicle2, 2L)

        simulateLazyInitialization(persistentVehicles, source, session, vehicle1, vehicle2)

        val result = dtoMapper.map(source)

        assertThat(result).isSameAs(source)
        assertThat(result.vehicles).containsExactlyInAnyOrder(vehicle1, vehicle2)
    }

    @Test
    fun map_entity_to_dto_with_lazy_loading_of_map() {
        val dtoMapper = DefaultDtoMapper<Person>()

        val session = createSession()
        val persistentCharacteristics = PersistentMap(session)

        @Suppress("UNCHECKED_CAST")
        val source = Person("John", "Doe", characteristics = persistentCharacteristics as Map<String, String>)

        val characteristic1 = arrayOf("eyes", "blue")
        val characteristic2 = arrayOf("hair", "brown")

        simulateLazyInitialization(persistentCharacteristics, source, session, characteristic1, characteristic2)

        val result = dtoMapper.map(source)

        assertThat(result).isSameAs(source)
        assertThat(result.characteristics).containsExactlyInAnyOrderEntriesOf(
            mapOf(
                characteristic1[0] to characteristic1[1],
                characteristic2[0] to characteristic2[1]
            )
        )
    }

    @Test
    fun map_entity_to_dto_with_skipped_property() {
        val propertyInitializer = mock<PropertyInitializer>()
        val mapInitializer = mock<Initializer<Map<*, *>>>()
        val collectionInitializer = mock<Initializer<Collection<*>>>()
        val dtoMapper = DefaultDtoMapper<MyEntityWithSkippedProperty>(EntityInitializer(propertyInitializer, mapInitializer, collectionInitializer))

        val entity = MyEntityWithSkippedProperty()

        val result = dtoMapper.map(entity)

        assertThat(result).isSameAs(entity)

        verifyZeroInteractions(propertyInitializer)
        verifyZeroInteractions(mapInitializer)
        verifyZeroInteractions(collectionInitializer)
    }

    private fun setId(vehicle: Vehicle, id: Long) {
        val idField = vehicle.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(vehicle, id)
        idField.isAccessible = false
    }

    private fun createSession(): SharedSessionContractImplementor {
        return mock {
            on { isOpenOrWaitingForAutoClose }.thenReturn(true)
            on { isConnected }.thenReturn(true)
        }
    }

    private fun simulateLazyInitialization(
        persistentCollection: PersistentCollection,
        source: Any, session: SharedSessionContractImplementor,
        vararg elements: Any
    ) {
        val isPersistentSet = persistentCollection is PersistentSet
        val isPersistentMap = persistentCollection is PersistentMap

        persistentCollection.owner = source

        whenever(session.initializeCollection(persistentCollection, false)).then {
            val columnAliases = arrayOf<String>()
            val descriptor = mock<CollectionAliases> {
                on { this.suffixedElementAliases }.thenReturn(columnAliases)
            }

            if (isPersistentMap) {
                whenever(descriptor.suffixedIndexAliases).thenReturn(arrayOf<String>())
            }

            val persister = mock<CollectionPersister>()

            if (isPersistentSet) {
                whenever(persister.collectionType).thenReturn(SetType("", ""))
            }
            if (isPersistentMap) {
                whenever(persister.collectionType).thenReturn(MapType("", ""))
            }

            val resultSets = elements.map {
                val rs = mock<ResultSet>()

                if (isPersistentMap) {
                    @Suppress("UNCHECKED_CAST")
                    val item = it as Array<String>

                    whenever(persister.readIndex(rs, columnAliases, session)).thenReturn(item[0])
                    whenever(persister.readElement(rs, persistentCollection.owner, columnAliases, session)).thenReturn(item[1])
                } else { // PersistentSet
                    whenever(persister.readElement(rs, persistentCollection.owner, columnAliases, session)).thenReturn(it)
                }

                rs
            }

            val anticipatedSize = -1

            // Initialize the inner persistentCollection's Set/Map: calls persister.collectionType, then collectionType.instantiate(...)
            persistentCollection.beforeInitialize(persister, anticipatedSize)

            // Initialize the temporary list
            persistentCollection.beginRead()

            // Add the items in the temporary list
            resultSets.forEach {
                persistentCollection.readFrom(it, persister, descriptor, source)
            }

            // Copy the temporary list in the inner persistentCollection's Set/Map, and declare persistedVehicles as 'initialized'
            persistentCollection.endRead()
        }
    }

    data class MyEntityWithSkippedProperty(
        private val primitive: Int = 0,
        private val enumeration: MyEnum = MyEnum.ONE,
        private val array: Array<String> = arrayOf("one", "two"),
        private val anonymousClass: MyInterface = object : MyInterface {
            override fun compute() = 5
        }
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MyEntityWithSkippedProperty

            if (primitive != other.primitive) return false
            if (enumeration != other.enumeration) return false
            if (!array.contentEquals(other.array)) return false
            if (anonymousClass != other.anonymousClass) return false

            return true
        }

        override fun hashCode(): Int {
            var result = primitive
            result = 31 * result + enumeration.hashCode()
            result = 31 * result + array.contentHashCode()
            result = 31 * result + anonymousClass.hashCode()
            return result
        }
    }

    enum class MyEnum {
        ONE, TWO
    }

    interface MyInterface {
        fun compute(): Int
    }

}