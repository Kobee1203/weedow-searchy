package com.weedow.spring.data.search.validation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.exception.ValidationException
import com.weedow.spring.data.search.expression.FieldExpression
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
internal class DataSearchValidationServiceImplTest {

    @Mock
    private lateinit var dataSearchErrorsFactory: DataSearchErrorsFactory

    @InjectMocks
    private lateinit var dataSearchValidationService: DataSearchValidationServiceImpl

    @Test
    fun validate_without_validator() {
        val fieldExpression = mock<FieldExpression>()
        val fieldExpressions = listOf(fieldExpression)
        val searchDescriptor = mock<SearchDescriptor<Any>>()

        dataSearchValidationService.validate(fieldExpressions, searchDescriptor)

        verifyZeroInteractions(dataSearchErrorsFactory)
    }

    @Test
    fun validate_successfully_with_validators() {
        val fieldExpression = mock<FieldExpression>()
        val fieldExpressions = listOf(fieldExpression)

        val validator = mock<DataSearchValidator>()
        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.entityClass }.thenReturn(Person::class.java)
            on { this.validators }.thenReturn(listOf(validator))
        }

        val dataSearchErrors = mock<DataSearchErrors>()
        whenever(dataSearchErrorsFactory.getDataSearchErrors()).thenReturn(dataSearchErrors)

        dataSearchValidationService.validate(fieldExpressions, searchDescriptor)

        verify(validator).validate(fieldExpressions, dataSearchErrors)
    }

    @Test
    fun validate_without_success_with_validators() {
        val fieldExpression = mock<FieldExpression>()
        val fieldExpressions = listOf(fieldExpression)

        val validator = mock<DataSearchValidator>()
        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.entityClass }.thenReturn(Person::class.java)
            on { this.validators }.thenReturn(listOf(validator))
        }

        val errorCode = "1001"
        val errorMessage = "Invalid value"
        val dataSearchErrors = mock<DataSearchErrors> {
            on { this.hasErrors() }.thenReturn(true)
            on { this.getAllErrors() }.thenReturn(listOf(DataSearchError(errorCode, errorMessage)))
        }
        whenever(dataSearchErrorsFactory.getDataSearchErrors()).thenReturn(dataSearchErrors)

        val status = HttpStatus.BAD_REQUEST
        assertThatThrownBy { dataSearchValidationService.validate(fieldExpressions, searchDescriptor) }
                .isInstanceOf(ValidationException::class.java)
                .hasMessage("${status.value()} ${status.name} \"Validation Errors: [$errorCode: $errorMessage]\"")
                .extracting("status", "reason").contains(status, "Validation Errors: [$errorCode: $errorMessage]")

        verify(validator).validate(fieldExpressions, dataSearchErrors)
    }
}