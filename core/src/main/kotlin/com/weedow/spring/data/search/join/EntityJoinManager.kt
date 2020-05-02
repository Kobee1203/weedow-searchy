package com.weedow.spring.data.search.join

import javax.persistence.criteria.From
import javax.persistence.criteria.Root

interface EntityJoinManager {

    fun <T> computeJoinMap(root: Root<T>, entityClass: Class<T>, entityJoinHandlers: List<EntityJoinHandler<T>>): Map<String, From<*, *>>

}