package com.weedow.spring.data.search.specification

import com.weedow.spring.data.search.field.FieldInfo
import com.weedow.spring.data.search.join.EntityJoinHandler
import com.weedow.spring.data.search.join.EntityJoinManager
import com.weedow.spring.data.search.utils.EntityUtils
import com.weedow.spring.data.search.utils.NullValue
import com.weedow.spring.data.search.utils.klogger
import org.springframework.data.jpa.domain.Specification
import java.lang.reflect.Field
import javax.persistence.criteria.*

class JpaSpecificationServiceImpl(
        private val entityJoinManager: EntityJoinManager
) : JpaSpecificationService {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized JpaSpecificationService: {}", this)
    }

    override fun <T> createSpecification(fieldInfos: List<FieldInfo>, entityClass: Class<T>, entityJoinHandlers: List<EntityJoinHandler<T>>): Specification<T> {
        return Specification.where(createSpecifications(fieldInfos, entityClass, entityJoinHandlers))!!
    }

    private fun <T> createSpecifications(fieldInfos: List<FieldInfo>, entityClass: Class<T>, entityJoinHandlers: List<EntityJoinHandler<T>>): Specification<T> {
        return Specification { root: Root<T>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
            query.distinct(true)

            val joinMap = entityJoinManager.computeJoinMap(root, entityClass, entityJoinHandlers)

            val predicates = mutableListOf<Predicate>()

            fieldInfos.forEach { (_, parentClass, field, _, fieldValues) ->
                val fieldName = field.name
                val from = joinMap.getOrElse(parentClass.canonicalName) { root }
                val expression: Path<out Any> = joinMap.getOrElse(fieldName) { from.get(fieldName) }

                val predicate = createPredicate(field, fieldValues, expression, criteriaBuilder)

                predicates.add(predicate)
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun createPredicate(field: Field, fieldValues: List<*>, expression: Path<*>, criteriaBuilder: CriteriaBuilder): Predicate {
        return if (fieldValues.size == 1) {
            val fieldValue = fieldValues[0]!!
            doCreatePredicate(fieldValue, field, expression, criteriaBuilder)
        } else {
            doCreatePredicate(fieldValues, expression, criteriaBuilder)
        }
    }

    private fun doCreatePredicate(fieldValue: Any, field: Field, expression: Path<*>, criteriaBuilder: CriteriaBuilder): Predicate {
        return if (fieldValue === NullValue.INSTANCE) {
            if (Collection::class.java.isAssignableFrom(field.type)) {
                @Suppress("UNCHECKED_CAST")
                criteriaBuilder.isEmpty(expression as Expression<Collection<*>>)
            } else {
                criteriaBuilder.isNull(expression)
            }
        } else if (EntityUtils.isElementCollection(field)) {
            @Suppress("UNCHECKED_CAST")
            criteriaBuilder.isMember(fieldValue, expression as Expression<Collection<*>>)
        } else {
            criteriaBuilder.equal(expression, fieldValue)
        }
    }

    private fun doCreatePredicate(fieldValues: List<*>, expression: Path<*>, criteriaBuilder: CriteriaBuilder): Predicate {
        val inClause = criteriaBuilder.`in`(expression)
        fieldValues.forEach { inClause.value(it) }
        return inClause
    }

}