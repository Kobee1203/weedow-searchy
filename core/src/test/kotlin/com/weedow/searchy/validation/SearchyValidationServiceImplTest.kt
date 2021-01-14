package com.weedow.searchy.validation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.searchy.common.model.Person
import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.exception.ValidationException
import com.weedow.searchy.expression.FieldExpression
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
internal class SearchyValidationServiceImplTest {

    @Mock
    private lateinit var searchyErrorsFactory: SearchyErrorsFactory

    @InjectMocks
    private lateinit var searchyValidationService: SearchyValidationServiceImpl

    @Test
    fun validate_without_validator() {
        val fieldExpression = mock<FieldExpression>()
        val fieldExpressions = listOf(fieldExpression)
        val searchyDescriptor = mock<SearchyDescriptor<Any>>()

        searchyValidationService.validate(fieldExpressions, searchyDescriptor)

        verifyZeroInteractions(searchyErrorsFactory)
    }

    @Test
    fun validate_successfully_with_validators() {
        val fieldExpression = mock<FieldExpression>()
        val fieldExpressions = listOf(fieldExpression)

        val validator = mock<SearchyValidator>()
        val searchyDescriptor = mock<SearchyDescriptor<Person>> {
            on { this.entityClass }.thenReturn(Person::class.java)
            on { this.validators }.thenReturn(listOf(validator))
        }

        val searchyErrors = mock<SearchyErrors>()
        whenever(searchyErrorsFactory.getSearchyErrors()).thenReturn(searchyErrors)

        searchyValidationService.validate(fieldExpressions, searchyDescriptor)

        verify(validator).validate(fieldExpressions, searchyErrors)
    }

    @Test
    fun validate_without_success_with_validators() {
        val fieldExpression = mock<FieldExpression>()
        val fieldExpressions = listOf(fieldExpression)

        val validator = mock<SearchyValidator>()
        val searchyDescriptor = mock<SearchyDescriptor<Person>> {
            on { this.entityClass }.thenReturn(Person::class.java)
            on { this.validators }.thenReturn(listOf(validator))
        }

        val errorCode = "1001"
        val errorMessage = "Invalid value"
        val searchyErrors = mock<SearchyErrors> {
            on { this.hasErrors() }.thenReturn(true)
            on { this.getAllErrors() }.thenReturn(listOf(SearchyError(errorCode, errorMessage)))
        }
        whenever(searchyErrorsFactory.getSearchyErrors()).thenReturn(searchyErrors)

        val status = HttpStatus.BAD_REQUEST
        assertThatThrownBy { searchyValidationService.validate(fieldExpressions, searchyDescriptor) }
            .isInstanceOf(ValidationException::class.java)
            .hasMessage("${status.value()} ${status.name} \"Validation Errors: [$errorCode: $errorMessage]\"")
            .extracting("status", "reason").contains(status, "Validation Errors: [$errorCode: $errorMessage]")

        verify(validator).validate(fieldExpressions, searchyErrors)
    }
}