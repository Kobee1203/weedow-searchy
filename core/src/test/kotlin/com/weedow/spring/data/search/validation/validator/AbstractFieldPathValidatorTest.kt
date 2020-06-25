package com.weedow.spring.data.search.validation.validator

import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.weedow.spring.data.search.expression.Operator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class AbstractFieldPathValidatorTest : BaseValidatorTest() {

    @Test
    fun supports() {
        val fieldExpression = mockFieldExpression("myfield", "myfield", Operator.EQUALS, "my value")

        Assertions.assertThat(MyValidator("myfield").supports(fieldExpression)).isTrue()
    }

    @Test
    fun don_t_supports() {
        val fieldExpression = mockFieldExpression("myfield", "myfield", Operator.EQUALS, "my value")

        Assertions.assertThat(MyValidator().supports(fieldExpression)).isFalse()
        Assertions.assertThat(MyValidator("otherField").supports(fieldExpression)).isFalse()
    }

    @Test
    fun validate_without_field_expression() {
        val dataSearchErrors = mockDataSearchErrors()

        val validator = MyValidator("myfield")
        validator.validate(listOf(), dataSearchErrors)

        verifyZeroInteractions(dataSearchErrors)
    }

    @Test
    fun validate_without_field_path() {
        val fieldExpression = mockFieldExpression("myfield", "myfield", Operator.EQUALS, "my value")
        val dataSearchErrors = mockDataSearchErrors()

        val validator = MyValidator()
        validator.validate(listOf(fieldExpression), dataSearchErrors)

        verifyZeroInteractions(dataSearchErrors)
    }

    @Test
    fun validate_without_matching_field_path() {
        val fieldExpression = mockFieldExpression("myfield", "myfield", Operator.EQUALS, "my value")
        val dataSearchErrors = mockDataSearchErrors()

        val validator = MyValidator("otherField")
        validator.validate(listOf(fieldExpression), dataSearchErrors)

        verifyZeroInteractions(dataSearchErrors)
    }

    internal class MyValidator(vararg fieldPaths: String) : AbstractFieldPathValidator(*fieldPaths)

}