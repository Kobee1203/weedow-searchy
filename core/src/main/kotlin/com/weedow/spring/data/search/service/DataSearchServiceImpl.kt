package com.weedow.spring.data.search.service

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.field.FieldInfo
import com.weedow.spring.data.search.join.DefaultEntityJoinHandler
import com.weedow.spring.data.search.specification.JpaSpecificationService
import com.weedow.spring.data.search.utils.klogger
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
class DataSearchServiceImpl(private val jpaSpecificationService: JpaSpecificationService) : DataSearchService {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized DataSearchService: {}", this)
    }

    override fun <T> findAll(fieldInfos: List<FieldInfo>, searchDescriptor: SearchDescriptor<T>): List<T> {
        val entityJoinHandlers = mutableListOf(*searchDescriptor.entityJoinHandlers.toTypedArray())
        entityJoinHandlers.add(DefaultEntityJoinHandler())
        val specification = jpaSpecificationService.createSpecification(fieldInfos, searchDescriptor.entityClass, entityJoinHandlers)
        return searchDescriptor.jpaSpecificationExecutor.findAll(specification)
    }

}