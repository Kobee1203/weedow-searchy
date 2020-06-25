package com.weedow.spring.data.search.validation.validator

import com.weedow.spring.data.search.expression.ExpressionUtils
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.validation.DataSearchErrors
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Base class to group common methods for comparing [Number].
 */
abstract class AbstractNumberValidator(
        vararg fieldPaths: String
) : AbstractFieldPathValidator(*fieldPaths) {

    abstract fun doValidate(value: Number, fieldExpression: FieldExpression, errors: DataSearchErrors)

    final override fun validateSingle(value: Any, fieldExpression: FieldExpression, errors: DataSearchErrors) {
        if (value !is Number) {
            errors.reject("not-a-number", "Invalid value for expression ''{0}''. ''{1}'' is not a Number instance.", ExpressionUtils.format(fieldExpression), value)
            return
        }

        doValidate(value, fieldExpression, errors)
    }

    internal fun compare(x: Number, y: Number): Int {
        return if (isSpecial(x) || isSpecial(y)) x.toDouble().compareTo(y.toDouble()) else toBigDecimal(x).compareTo(toBigDecimal(y))
    }

    private fun isSpecial(x: Number): Boolean {
        val specialDouble = (x is Double && (x.isNaN() || x.isInfinite()))
        val specialFloat = (x is Float && (x.isNaN() || x.isInfinite()))
        return specialDouble || specialFloat
    }

    private fun toBigDecimal(number: Number): BigDecimal {
        return when (number) {
            is BigDecimal -> number
            is BigInteger -> BigDecimal(number)
            is Byte, is Short, is Int, is Long -> BigDecimal(number.toLong())
            is Float, is Double -> BigDecimal(number.toDouble())
            else -> {
                try {
                    BigDecimal(number.toString())
                } catch (e: NumberFormatException) {
                    throw RuntimeException("The given number (\"" + number + "\" of class " + number.javaClass.name + ") does not have a parsable string representation", e)
                }
            }
        }
    }

}