package com.weedow.spring.data.search.dto

import org.apache.commons.beanutils.PropertyUtils
import org.hibernate.Hibernate
import org.hibernate.internal.util.collections.IdentitySet


/**
 * Default [DtoMapper] implementation.
 *
 * There is no conversion to a specific DTO.
 *
 * But the entity is fully loaded to prevent LazyInitializationException when the Entity is serialized.
 */
class DefaultDtoMapper<T>(
        val entityInitializer: EntityInitializer = EntityInitializer()
) : DtoMapper<T, T> {

    /**
     * Returns the given Entity bean directly
     *
     * @param source Entity bean
     * @return The same Entity bean as [source]
     */
    override fun map(source: T): T {
        // Set of objects already initialized to prevent cycles
        val initializedObjects = IdentitySet()
        entityInitializer.initialize(source!!, initializedObjects)

        return source
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}

/**
 * Interface to initialize recursively an object if it is not initialized, and prevent cycles if an object is already initialized.
 */
interface Initializer<T> {

    /**
     * Method to be implemented.
     *
     * @param obj Object to be initialized
     * @param initializedObjects Set of objects already initialized
     */
    fun doInitialize(obj: T, initializedObjects: IdentitySet)

    /**
     * Method to call.
     * This method calls the [doInitialize] method when the given object must be initialized.
     *
     * @param obj Object to be initialized
     * @param initializedObjects Set of objects already initialized
     */
    fun initialize(obj: T, initializedObjects: IdentitySet) {
        // Prevent cycles
        if (!initializedObjects.contains(obj)) {
            initializedObjects.add(obj)

            // Check if the object is initialized
            if (!Hibernate.isInitialized(obj)) {
                Hibernate.initialize(obj);
            }

            // Initialize recursively
            doInitialize(obj, initializedObjects)
        }
    }

}

/**
 * Entity Initializer.
 */
class EntityInitializer() : Initializer<Any> {

    private var propertyInitializer: Initializer<Any> = PropertyInitializer(this)
    private var mapInitializer: Initializer<Map<*, *>> = MapInitializer(this)
    private var collectionInitializer: Initializer<Collection<*>> = CollectionInitializer(this)

    constructor(propertyInitializer: PropertyInitializer, mapInitializer: Initializer<Map<*, *>>, collectionInitializer: Initializer<Collection<*>>) : this() {
        this.propertyInitializer = propertyInitializer
        this.mapInitializer = mapInitializer
        this.collectionInitializer = collectionInitializer
    }

    override fun doInitialize(obj: Any, initializedObjects: IdentitySet) {
        val propertyDescriptors = PropertyUtils.getPropertyDescriptors(obj)
        propertyDescriptors.forEach { propertyDescriptor ->
            val propertyType = propertyDescriptor.propertyType
            val property = PropertyUtils.getProperty(obj, propertyDescriptor.name)

            if (!isSkippedProperty(property, propertyType)) {
                when {
                    Map::class.java.isAssignableFrom(propertyType) -> {
                        mapInitializer.initialize(property as Map<*, *>, initializedObjects)
                    }
                    Collection::class.java.isAssignableFrom(propertyType) -> {
                        collectionInitializer.initialize(property as Collection<*>, initializedObjects)
                    }
                    else -> {
                        propertyInitializer.initialize(property, initializedObjects)
                    }
                }
            }
        }
    }

    private fun isSkippedProperty(property: Any?, propertyType: Class<*>) =
            property == null
                    || propertyType.isPrimitive
                    || propertyType.isEnum
                    || propertyType.isArray
                    || propertyType.isAnonymousClass
                    || propertyType.kotlin.javaPrimitiveType != null
}

/**
 * Property Initializer
 */
class PropertyInitializer(
        private val entityInitializer: EntityInitializer
) : Initializer<Any> {

    override fun doInitialize(obj: Any, initializedObjects: IdentitySet) {
        entityInitializer.initialize(obj, initializedObjects)
    }
}

/**
 * Map Initializer
 */
class MapInitializer(
        private val entityInitializer: EntityInitializer
) : Initializer<Map<*, *>> {

    override fun doInitialize(map: Map<*, *>, initializedObjects: IdentitySet) {
        map.keys.forEach {
            entityInitializer.initialize(it!!, initializedObjects)
        }

        map.values.forEach {
            entityInitializer.initialize(it!!, initializedObjects)
        }
    }
}

/**
 * Collection Initializer
 */
class CollectionInitializer(
        private val entityInitializer: EntityInitializer
) : Initializer<Collection<*>> {

    override fun doInitialize(coll: Collection<*>, initializedObjects: IdentitySet) {
        coll.forEach { item ->
            entityInitializer.initialize(item!!, initializedObjects)
        }
    }
}