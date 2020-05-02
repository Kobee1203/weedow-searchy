package com.weedow.spring.data.search.config

import com.weedow.spring.data.search.utils.EntityUtils
import com.weedow.spring.data.search.utils.klogger
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import javax.persistence.EntityManager
import javax.persistence.Id

object JpaSpecificationExecutorFactory {

    private val log by klogger()

    private var entityManager: EntityManager? = null

    fun init(entityManager: EntityManager) {
        if (this.entityManager == null) {
            this.entityManager = entityManager
            log.info("Initialized JpaSpecificationExecutorFactory with the following entityManager: {}", this.entityManager)
        }
    }

    fun isInitialized(): Boolean {
        return this.entityManager != null && this.entityManager!!.isOpen
    }

    fun reset() {
        this.entityManager = null
    }

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