package com.weedow.spring.data.search.validation.validator

import com.nhaarman.mockitokotlin2.mock
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.expression.FieldInfo
import com.weedow.spring.data.search.expression.Operator
import com.weedow.spring.data.search.validation.DataSearchErrors

abstract class BaseValidatorTest {

    internal fun mockFieldExpression(fieldPath: String, fieldName: String, operator: Operator, value: Any): FieldExpression {
        val fieldInfo = FieldInfo(fieldPath, fieldName, Object::class.java)
        return mock {
            on { this.fieldInfo }.thenReturn(fieldInfo)
            on { this.operator }.thenReturn(operator)
            on { this.value }.thenReturn(value)
        }
    }

    internal fun mockDataSearchErrors() = mock<DataSearchErrors>()

}