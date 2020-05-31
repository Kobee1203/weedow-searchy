package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.exception.UnsupportedOperatorException
import com.weedow.spring.data.search.field.FieldInfo
import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.utils.EntityUtils
import com.weedow.spring.data.search.utils.NullValue
import com.weedow.spring.data.search.utils.klogger
import org.springframework.data.jpa.domain.Specification
import java.lang.reflect.Field
import javax.persistence.criteria.*

internal data class SimpleExpression(
        private val operator: Operator,
        private val fieldInfo: FieldInfo,
        private val value: Any
) : Expression {

    companion object {
        private val log by klogger()
    }

    override fun <T> toSpecification(entityJoins: EntityJoins): Specification<T> {
        return Specification { root: Root<T>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
            val path: Path<*> = entityJoins.getPath(fieldInfo.fieldPath, root)

            when (operator) {
                Operator.EQUALS -> equals(criteriaBuilder, path, value, fieldInfo.field)
                Operator.CONTAINS -> @Suppress("UNCHECKED_CAST") like(criteriaBuilder, path as Path<String>, value as String)
                Operator.ICONTAINS -> @Suppress("UNCHECKED_CAST") ilike(criteriaBuilder, path as Path<String>, value as String)
                Operator.LESS_THAN -> @Suppress("UNCHECKED_CAST") lessThan(criteriaBuilder, path as Path<Comparable<Any>>, value as Comparable<Any>)
                Operator.LESS_THAN_OR_EQUALS -> @Suppress("UNCHECKED_CAST") lessThanOrEquals(criteriaBuilder, path as Path<Comparable<Any>>, value as Comparable<Any>)
                Operator.GREATER_THAN -> @Suppress("UNCHECKED_CAST") greaterThan(criteriaBuilder, path as Path<Comparable<Any>>, value as Comparable<Any>)
                Operator.GREATER_THAN_OR_EQUALS -> @Suppress("UNCHECKED_CAST") greaterThanOrEquals(criteriaBuilder, path as Path<Comparable<Any>>, value as Comparable<Any>)
                Operator.IN -> `in`(criteriaBuilder, path, value as List<*>)
                else -> throw UnsupportedOperatorException(operator)
            }
        }
    }

    private fun equals(criteriaBuilder: CriteriaBuilder, path: Path<*>, value: Any, field: Field): Predicate {
        return if (value === NullValue) {
            if (Collection::class.java.isAssignableFrom(field.type)) {
                @Suppress("UNCHECKED_CAST")
                criteriaBuilder.isEmpty(path as javax.persistence.criteria.Expression<Collection<*>>)
            } else {
                criteriaBuilder.isNull(path)
            }
        } else if (EntityUtils.isElementCollection(field)) {
            @Suppress("UNCHECKED_CAST")
            criteriaBuilder.isMember(value, path as javax.persistence.criteria.Expression<Collection<*>>)
        } else {
            criteriaBuilder.equal(path, value)
        }
    }

    private fun like(criteriaBuilder: CriteriaBuilder, path: Path<String>, value: String): Predicate {
        return criteriaBuilder.like(
                path,
                criteriaBuilder.literal("%$value%")
        )
    }

    private fun ilike(criteriaBuilder: CriteriaBuilder, path: Path<String>, value: String): Predicate {
        return criteriaBuilder.like(
                criteriaBuilder.lower(path),
                criteriaBuilder.lower(criteriaBuilder.literal("%$value%"))
        )
    }

    private fun <Y : Comparable<Y>> lessThan(criteriaBuilder: CriteriaBuilder, path: Path<Y>, value: Y): Predicate {
        return criteriaBuilder.lessThan(path, value)
    }

    private fun <Y : Comparable<Y>> lessThanOrEquals(criteriaBuilder: CriteriaBuilder, path: Path<Y>, value: Y): Predicate {
        return criteriaBuilder.lessThanOrEqualTo(path, value)
    }

    private fun <Y : Comparable<Y>> greaterThan(criteriaBuilder: CriteriaBuilder, path: Path<Y>, value: Y): Predicate {
        return criteriaBuilder.greaterThan(path, value)
    }

    private fun <Y : Comparable<Y>> greaterThanOrEquals(criteriaBuilder: CriteriaBuilder, path: Path<Y>, value: Y): Predicate {
        return criteriaBuilder.greaterThanOrEqualTo(path, value)
    }

    private fun `in`(criteriaBuilder: CriteriaBuilder, path: Path<*>, values: List<*>): Predicate {
        val inClause = criteriaBuilder.`in`(path)
        values.forEach { inClause.value(it) }
        return inClause
    }
}