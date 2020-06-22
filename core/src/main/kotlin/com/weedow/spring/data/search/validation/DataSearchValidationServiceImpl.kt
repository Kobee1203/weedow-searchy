package com.weedow.spring.data.search.validation

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.exception.ValidationException
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.utils.klogger

/**
 * Default implementation of [DataSearchValidationService].
 */
class DataSearchValidationServiceImpl(
        private val dataSearchErrorsFactory: DataSearchErrorsFactory
) : DataSearchValidationService {

    companion object {
        private val log by klogger()
    }

    @Throws(ValidationException::class)
    override fun <T> validate(fieldExpressions: Collection<FieldExpression>, searchDescriptor: SearchDescriptor<T>) {
        val validators = searchDescriptor.validators
        if (validators.isNotEmpty()) {
            val entityClass = searchDescriptor.entityClass

            if (log.isTraceEnabled) log.trace("Process following validators for ${entityClass.canonicalName}: $validators")

            val errors = dataSearchErrorsFactory.getDataSearchErrors()
            validators.forEach {
                it.validate(fieldExpressions, errors)
            }

            if (errors.hasErrors()) {
                if (log.isDebugEnabled) log.debug("Validators found ${errors.getAllErrors().size} errors")
                throw ValidationException(errors.getAllErrors())
            } else {
                if (log.isDebugEnabled) log.debug("Validators found no errors")
            }
        }
    }
}