package com.weedow.searchy.validation.validator

import com.nhaarman.mockitokotlin2.*
import com.weedow.searchy.expression.Operator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class EmailValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val fieldExpression1 = mockFieldExpression("email", "email", Operator.EQUALS, "john.doe@example.com")
        val fieldExpression2 = mockFieldExpression("otherEmails", "otherEmails", Operator.IN, listOf("john.doe@spring.com", "john.doe@mail.com"))
        val fieldExpression3 = mockFieldExpression("job.email", "email", Operator.EQUALS, "john.doe@acme.com")
        val searchyErrors = mockSearchyErrors()

        val validator = EmailValidator("email", "otherEmails", "job.email")
        validator.validate(listOf(fieldExpression1, fieldExpression2, fieldExpression3), searchyErrors)

        verifyZeroInteractions(searchyErrors)
    }

    @Test
    fun validate_without_success() {
        val fieldExpression1 = mockFieldExpression("email", "email", Operator.EQUALS, "invalid-email-1")
        val fieldExpression2 = mockFieldExpression("otherEmails", "otherEmails", Operator.IN, listOf("john.doe@example.com", "invalid-email-2"))
        val fieldExpression3 = mockFieldExpression("job.email", "email", Operator.EQUALS, "invalid-email-3")
        val searchyErrors = mockSearchyErrors()

        val validator = EmailValidator("email", "otherEmails", "job.email")
        validator.validate(listOf(fieldExpression1, fieldExpression2, fieldExpression3), searchyErrors)

        argumentCaptor<String> {
            verify(searchyErrors, times(3)).reject(eq("email"), eq("Invalid email value for expression ''{0}''."), capture())

            assertThat(allValues).containsExactly(
                "email EQUALS invalid-email-1",
                "otherEmails IN [john.doe@example.com, invalid-email-2]",
                "job.email EQUALS invalid-email-3"
            )
        }
    }

}