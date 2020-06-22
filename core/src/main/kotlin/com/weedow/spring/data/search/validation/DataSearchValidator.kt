package com.weedow.spring.data.search.validation

import com.weedow.spring.data.search.expression.FieldExpression

interface DataSearchValidator {

    fun validate(fieldExpressions: Collection<FieldExpression>, errors: DataSearchErrors)

}