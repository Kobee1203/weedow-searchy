package com.weedow.searchy.service

import com.nhaarman.mockitokotlin2.*
import com.weedow.searchy.common.model.Person
import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.descriptor.SearchyDescriptorService
import com.weedow.searchy.dto.DtoConverterService
import com.weedow.searchy.exception.SearchyDescriptorNotFound
import com.weedow.searchy.exception.ValidationException
import com.weedow.searchy.expression.ExpressionMapper
import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.expression.RootExpression
import com.weedow.searchy.validation.SearchyError
import com.weedow.searchy.validation.SearchyValidationService
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
internal class SearchyServiceImplTest {

    @Mock
    private lateinit var searchyDescriptorService: SearchyDescriptorService

    @Mock
    private lateinit var expressionMapper: ExpressionMapper

    @Mock
    private lateinit var searchyValidationService: SearchyValidationService

    @Mock
    private lateinit var entitySearchService: EntitySearchService

    @Mock
    private lateinit var dtoConverterService: DtoConverterService<Person, *>

    @InjectMocks
    lateinit var searchyService: SearchyServiceImpl<*, *>

    @Test
    fun findAll() {
        val searchyDescriptorId = "person"
        val params = mutableMapOf(
            "firstName" to listOf("John")
        )

        val rootClass = Person::class.java

        val searchyDescriptor = mock<SearchyDescriptor<Person>> {
            on { this.entityClass }.doReturn(rootClass)
        }
        whenever(searchyDescriptorService.getSearchyDescriptor(searchyDescriptorId)).thenReturn(searchyDescriptor)

        val rootExpression = mock<RootExpression<Person>>()
        whenever(expressionMapper.toExpression(params, rootClass)).thenReturn(rootExpression)

        val fieldExpressions = mock<Collection<FieldExpression>>()
        whenever(rootExpression.toFieldExpressions(false)).thenReturn(fieldExpressions)

        val person = Person("John", "Doe")
        val entities = listOf(person)
        whenever(entitySearchService.findAll(rootExpression, searchyDescriptor)).thenReturn(entities)

        val dtos = listOf<Any>(mock())
        whenever(dtoConverterService.convert(entities, searchyDescriptor)).thenReturn(dtos)

        val result = searchyService.search(searchyDescriptorId, params)

        assertThat(result).isSameAs(dtos)

        verify(searchyValidationService).validate(fieldExpressions, searchyDescriptor)
    }

    @Test
    fun throw_exception_when_SearchyDescriptor_not_found() {
        val searchyDescriptorId = "person"
        val params = mutableMapOf<String, List<String>>()

        whenever(searchyDescriptorService.getSearchyDescriptor(searchyDescriptorId)).thenReturn(null)

        Assertions.assertThatThrownBy { searchyService.search(searchyDescriptorId, params) }
            .isInstanceOf(SearchyDescriptorNotFound::class.java)
            .hasMessage("Could not found the Search Descriptor with Id $searchyDescriptorId")

        verifyZeroInteractions(expressionMapper)
        verifyZeroInteractions(searchyValidationService)
        verifyZeroInteractions(entitySearchService)
    }

    @Test
    fun throw_exception_when_validation_errors() {
        val rootClass = Person::class.java
        val searchyDescriptorId = "person"
        val params = LinkedMultiValueMap<String, String>()

        val searchyDescriptor = mock<SearchyDescriptor<Person>> {
            on { this.entityClass }.doReturn(rootClass)
        }
        whenever(searchyDescriptorService.getSearchyDescriptor(searchyDescriptorId)).thenReturn(searchyDescriptor)

        val rootExpression = mock<RootExpression<Person>>()
        whenever(expressionMapper.toExpression(params, rootClass)).thenReturn(rootExpression)

        val fieldExpressions = mock<Collection<FieldExpression>>()
        whenever(rootExpression.toFieldExpressions(false)).thenReturn(fieldExpressions)

        val errorCode = "not-empty"
        val errorMessage = "The search must contain at least one query parameter."

        val error = SearchyError(errorCode, errorMessage)
        whenever(searchyValidationService.validate(fieldExpressions, searchyDescriptor)).thenThrow(ValidationException(listOf(error)))

        val status = HttpStatus.BAD_REQUEST
        Assertions.assertThatThrownBy { searchyService.search(searchyDescriptorId, params) }
            .isInstanceOf(ValidationException::class.java)
            .hasMessage("${status.value()} ${status.name} \"Validation Errors: [$errorCode: $errorMessage]\"")
            .extracting("status", "reason").contains(status, "Validation Errors: [$errorCode: $errorMessage]")

        verifyZeroInteractions(entitySearchService)
        verifyNoMoreInteractions(searchyDescriptor) // searchyDescriptor.dtoMapper not called
    }

}