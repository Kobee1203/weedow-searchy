package com.weedow.spring.data.search.config

import com.weedow.spring.data.search.utils.EntityUtils
import com.weedow.spring.data.search.utils.klogger
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import javax.persistence.EntityManager
import javax.persistence.Id

/**
 * Factory class to create new [JpaSpecificationExecutor] from [EntityManager] and the given Entity Class.
 *
 */
object JpaSpecificationExecutorFactory {

    private val log by klogger()

    private var entityManager: EntityManager? = null

    /**
     * Initialize the Factory with the given EntityManager.
     *
     * @param entityManager EntityManager sed to initialize the Factory.
     */
    fun init(entityManager: EntityManager) {
        if (this.entityManager == null) {
            this.entityManager = entityManager
            log.info("Initialized JpaSpecificationExecutorFactory with the following entityManager: {}", this.entityManager)
        }
    }

    /**
     * Check if the Factory is initialized (Check if the inner EntityManager is not null and open).
     */
    fun isInitialized(): Boolean {
        return this.entityManager != null && this.entityManager!!.isOpen
    }

    /**
     * Reset the Factory.
     *
     * If this method is called, [isInitialized] method will return `false`.
     * The [init] method must then be called to re-initialize the Factory.
     */
    fun reset() {
        this.entityManager = null
    }

    /**
     * Returns a new [JpaSpecificationExecutor] instance for the given Entity Class.
     *
     * @param entityClass Entity Class used to initialize the [JpaSpecificationExecutor]
     */
    fun <T> getJpaSpecificationExecutor(entityClass: Class<T>): JpaSpecificationExecutor<T> {
        val primaryKeyFields = EntityUtils.getFieldsWithAnnotation(entityClass, Id::class.java)
        val primaryKeyClass = if (primaryKeyFields.isNotEmpty()) EntityUtils.getFieldClass(primaryKeyFields[0]) else Long::class.java

        return getJpaSpecificationExecutor(entityClass, primaryKeyClass)
    }

    private fun <T, ID> getJpaSpecificationExecutor(entityClass: Class<T>, @Suppress("UNUSED_PARAMETER") id: Class<ID>): JpaSpecificationExecutor<T> {
        requireNotNull(entityManager) {
            "EntityManager is not initialized. Use 'init' method before."
        }
        return SimpleJpaRepository<T, ID>(entityClass, entityManager!!)
    }
}