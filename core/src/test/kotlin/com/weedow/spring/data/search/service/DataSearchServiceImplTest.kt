package com.weedow.spring.data.search.service

import com.nhaarman.mockitokotlin2.*
import com.weedow.spring.data.search.common.dto.PersonDto
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.descriptor.SearchDescriptorService
import com.weedow.spring.data.search.dto.DtoConverterService
import com.weedow.spring.data.search.example.PersonDtoMapper
import com.weedow.spring.data.search.exception.SearchDescriptorNotFound
import com.weedow.spring.data.search.exception.ValidationException
import com.weedow.spring.data.search.expression.ExpressionMapper
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.validation.DataSearchError
import com.weedow.spring.data.search.validation.DataSearchValidationService
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.util.LinkedMultiValueMap

@ExtendWith(MockitoExtension::class)
internal class DataSearchServiceImplTest {

    @Mock
    private lateinit var searchDescriptorService: SearchDescriptorService

    @Mock
    private lateinit var expressionMapper: ExpressionMapper

    @Mock
    private lateinit var dataSearchValidationService: DataSearchValidationService

    @Mock
    private lateinit var entitySearchService: EntitySearchService

    @Mock
    private lateinit var dtoConverterService: DtoConverterService<Person, *>

    @InjectMocks
    lateinit var dataSearchService: DataSearchServiceImpl<*, *>

    @Test
    fun findAll() {
        val searchDescriptorId = "person"
        val params = mutableMapOf(
            "firstName" to listOf("John")
        )

        val rootClass = Person::class.java

        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.entityClass }.doReturn(rootClass)
        }
        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(searchDescriptor)

        val rootExpression = mock<RootExpression<Person>>()
        whenever(expressionMapper.toExpression(params, rootClass)).thenReturn(rootExpression)

        val fieldExpressions = mock<Collection<FieldExpression>>()
        whenever(rootExpression.toFieldExpressions(false)).thenReturn(fieldExpressions)

        val person = Person("John", "Doe")
        val entities = listOf(person)
        whenever(entitySearchService.findAll(rootExpression, searchDescriptor)).thenReturn(entities)

        val dtos = listOf<Any>(mock())
        whenever(dtoConverterService.convert(entities, searchDescriptor)).thenReturn(dtos)

        val result = dataSearchService.search(searchDescriptorId, params)

        assertThat(result).isSameAs(dtos)

        verify(dataSearchValidationService).validate(fieldExpressions, searchDescriptor)
    }

    @Test
    fun throw_exception_when_SearchDescriptor_not_found() {
        val searchDescriptorId = "person"
        val params = mutableMapOf<String, List<String>>()

        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(null)

        Assertions.assertThatThrownBy { dataSearchService.search(searchDescriptorId, params) }
            .isInstanceOf(SearchDescriptorNotFound::class.java)
            .hasMessage("Could not found the Search Descriptor with Id $searchDescriptorId")

        verifyZeroInteractions(expressionMapper)
        verifyZeroInteractions(dataSearchValidationService)
        verifyZeroInteractions(entitySearchService)
    }

    @Test
    fun throw_exception_when_validation_errors() {
        val rootClass = Person::class.java
        val searchDescriptorId = "person"
        val params = LinkedMultiValueMap<String, String>()

        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.entityClass }.doReturn(rootClass)
        }
        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(searchDescriptor)

        val rootExpression = mock<RootExpression<Person>>()
        whenever(expressionMapper.toExpression(params, rootClass)).thenReturn(rootExpression)

        val fieldExpressions = mock<Collection<FieldExpression>>()
        whenever(rootExpression.toFieldExpressions(false)).thenReturn(fieldExpressions)

        val errorCode = "not-empty"
        val errorMessage = "The search must contain at least one query parameter."

        val error = DataSearchError(errorCode, errorMessage)
        whenever(dataSearchValidationService.validate(fieldExpressions, searchDescriptor)).thenThrow(ValidationException(listOf(error)))

        val status = HttpStatus.BAD_REQUEST
        Assertions.assertThatThrownBy { dataSearchService.search(searchDescriptorId, params) }
            .isInstanceOf(ValidationException::class.java)
            .hasMessage("${status.value()} ${status.name} \"Validation Errors: [$errorCode: $errorMessage]\"")
            .extracting("status", "reason").contains(status, "Validation Errors: [$errorCode: $errorMessage]")

        verifyZeroInteractions(entitySearchService)
        verifyNoMoreInteractions(searchDescriptor) // searchDescriptor.dtoMapper not called
    }

}