package com.weedow.searchy.validation

import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.exception.ValidationException
import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.utils.klogger

/**
 * Default implementation of [SearchyValidationService].
 *
 * @param searchyErrorsFactory [SearchyErrorsFactory]
 */
class SearchyValidationServiceImpl(
    private val searchyErrorsFactory: SearchyErrorsFactory
) : SearchyValidationService {

    companion object {
        private val log by klogger()
    }

    @Throws(ValidationException::class)
    override fun <T> validate(fieldExpressions: Collection<FieldExpression>, searchyDescriptor: SearchyDescriptor<T>) {
        val validators = searchyDescriptor.validators
        if (validators.isNotEmpty()) {
            val entityClass = searchyDescriptor.entityClass

            if (log.isTraceEnabled) log.trace("Process following validators for ${entityClass.canonicalName}: $validators")

            val errors = searchyErrorsFactory.getSearchyErrors()

            validators.forEach { validator ->
                validator.validate(fieldExpressions, errors)
            }

            if (errors.hasErrors()) {
                val allErrors = errors.getAllErrors()
                if (log.isDebugEnabled) log.debug("Validators found ${allErrors.size} errors")
                throw ValidationException(allErrors)
            } else {
                if (log.isDebugEnabled) log.debug("Validators found no errors")
            }
        }
    }
}