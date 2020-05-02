package com.weedow.spring.data.search.specification

import com.weedow.spring.data.search.field.FieldInfo
import com.weedow.spring.data.search.join.EntityJoinHandler
import org.springframework.data.jpa.domain.Specification

interface JpaSpecificationService {

    fun <T> createSpecification(fieldInfos: List<FieldInfo>, entityClass: Class<T>, entityJoinHandlers: List<EntityJoinHandler<T>>): Specification<T>

}