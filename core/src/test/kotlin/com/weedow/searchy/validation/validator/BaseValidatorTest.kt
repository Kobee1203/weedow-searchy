package com.weedow.searchy.validation.validator

import com.nhaarman.mockitokotlin2.mock
import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.expression.FieldInfo
import com.weedow.searchy.expression.Operator
import com.weedow.searchy.validation.SearchyErrors

abstract class BaseValidatorTest {

    internal fun mockFieldExpression(fieldPath: String, fieldName: String, operator: Operator, value: Any): FieldExpression {
        val fieldInfo = FieldInfo(fieldPath, fieldName, Object::class.java)
        return mock {
            on { this.fieldInfo }.thenReturn(fieldInfo)
            on { this.operator }.thenReturn(operator)
            on { this.value }.thenReturn(value)
        }
    }

    internal fun mockSearchyErrors() = mock<SearchyErrors>()

}