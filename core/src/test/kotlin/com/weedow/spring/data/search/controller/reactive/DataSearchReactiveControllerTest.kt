package com.weedow.spring.data.search.controller.reactive

import com.nhaarman.mockitokotlin2.*
import com.weedow.spring.data.search.common.dto.PersonDto
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.descriptor.SearchDescriptorService
import com.weedow.spring.data.search.exception.SearchDescriptorNotFound
import com.weedow.spring.data.search.exception.ValidationException
import com.weedow.spring.data.search.expression.ExpressionMapper
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.service.DataSearchService
import com.weedow.spring.data.search.validation.DataSearchError
import com.weedow.spring.data.search.validation.DataSearchValidationService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.result.method.RequestMappingInfo
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping

@ExtendWith(MockitoExtension::class)
internal class DataSearchReactiveControllerTest {

    @Mock
    lateinit var searchDescriptorService: SearchDescriptorService

    @Mock
    lateinit var expressionMapper: ExpressionMapper

    @Mock
    lateinit var dataSearchService: DataSearchService

    @Mock
    lateinit var dataSearchValidationService: DataSearchValidationService

    @Spy
    private val searchProperties: SearchProperties = SearchProperties()

    @Mock
    lateinit var requestMappingHandlerMapping: RequestMappingHandlerMapping

    @InjectMocks
    lateinit var dataSearchController: DataSearchReactiveController

    @BeforeEach
    fun setUp() {
        whenever(searchProperties.basePath).thenReturn(SearchProperties.DEFAULT_BASE_PATH)

        val mapping = RequestMappingInfo
                .paths("/search/{searchDescriptorId}")
                .methods(RequestMethod.GET)
                .build()
        verify(requestMappingHandlerMapping).registerMapping(mapping, dataSearchController, DataSearchReactiveController::class.java.getMethod("search", String::class.java, MultiValueMap::class.java))
    }

    @Test
    fun search_successfully() {
        val rootClass = Person::class.java
        val firstName = "John"
        val lastName = "Doe"
        val searchDescriptorId = "person"
        val fieldPath = "firstName"
        val fieldValue = firstName
        val params = LinkedMultiValueMap<String, String>()
        params.add(fieldPath, fieldValue)

        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.entityClass }.doReturn(rootClass)
        }
        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(searchDescriptor)

        val rootExpression = mock<RootExpression<Person>>()
        whenever(expressionMapper.toExpression(params, rootClass)).thenReturn(rootExpression)

        val fieldExpressions = mock<Collection<FieldExpression>>()
        whenever(rootExpression.toFieldExpressions(false)).thenReturn(fieldExpressions)

        val person = PersonDto.Builder().firstName(firstName).lastName(lastName).build()
        whenever(dataSearchService.findAll(rootExpression, searchDescriptor)).thenReturn(listOf(person))

        val responseEntity = dataSearchController.search(searchDescriptorId, params)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body)
                .isInstanceOf(List::class.java)
                .hasOnlyElementsOfType(PersonDto::class.java)
                .extracting("firstName", "lastName")
                .containsExactly(Tuple.tuple(firstName, lastName))

        verify(dataSearchValidationService).validate(fieldExpressions, searchDescriptor)
    }

    @Test
    fun throw_exception_when_SearchDescriptor_not_found() {
        val searchDescriptorId = "person"
        val params = LinkedMultiValueMap<String, String>()

        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(null)

        assertThatThrownBy { dataSearchController.search(searchDescriptorId, params) }
                .isInstanceOf(SearchDescriptorNotFound::class.java)
                .hasMessage("Could not found the Search Descriptor with Id $searchDescriptorId")

        verifyZeroInteractions(expressionMapper)
        verifyZeroInteractions(dataSearchValidationService)
        verifyZeroInteractions(dataSearchService)
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
        assertThatThrownBy { dataSearchController.search(searchDescriptorId, params) }
                .isInstanceOf(ValidationException::class.java)
                .hasMessage("${status.value()} ${status.name} \"Validation Errors: [$errorCode: $errorMessage]\"")
                .extracting("status", "reason").contains(status, "Validation Errors: [$errorCode: $errorMessage]")

        verifyZeroInteractions(dataSearchService)
    }
}