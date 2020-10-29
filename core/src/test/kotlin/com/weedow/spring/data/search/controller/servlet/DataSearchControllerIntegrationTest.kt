package com.weedow.spring.data.search.controller.servlet

import com.nhaarman.mockitokotlin2.*
import com.weedow.spring.data.search.common.dto.PersonDto
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.descriptor.SearchDescriptorBuilder
import com.weedow.spring.data.search.descriptor.SearchDescriptorService
import com.weedow.spring.data.search.exception.ValidationException
import com.weedow.spring.data.search.expression.ExpressionMapper
import com.weedow.spring.data.search.expression.ExpressionUtils
import com.weedow.spring.data.search.expression.FieldInfo
import com.weedow.spring.data.search.expression.RootExpressionImpl
import com.weedow.spring.data.search.service.DataSearchService
import com.weedow.spring.data.search.validation.DataSearchError
import com.weedow.spring.data.search.validation.DataSearchValidationService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@ExtendWith(MockitoExtension::class)
internal class DataSearchControllerIntegrationTest {

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

    @Spy
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping = RequestMappingHandlerMapping()

    @InjectMocks
    lateinit var dataSearchController: DataSearchController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        whenever(searchProperties.basePath).thenReturn(SearchProperties.DEFAULT_BASE_PATH)

        this.mockMvc = MockMvcBuilders.standaloneSetup(dataSearchController)
                .setCustomHandlerMapping { requestMappingHandlerMapping }
                .build()
    }

    @Test
    fun search_with_params() {
        val rootClass = Person::class.java
        val firstName = "John"
        val lastName = "Doe"
        val searchDescriptorId = "person"
        val fieldPath = "firstName"
        val fieldValue = firstName

        val searchDescriptor = createSearchDescriptor()
        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(searchDescriptor)

        val fieldInfo = FieldInfo(fieldPath, "firstName", rootClass)
        val rootExpression = RootExpressionImpl<Person>(ExpressionUtils.equals(fieldInfo, fieldValue))
        whenever(expressionMapper.toExpression(any(), eq(rootClass))).thenReturn(rootExpression)

        val personInfos = createPerson(firstName, lastName)
        whenever(dataSearchService.findAll(rootExpression, searchDescriptor)).thenReturn(listOf(personInfos.first))

        mockMvc.get("/search/$searchDescriptorId") {
            param(fieldPath, firstName)
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[${personInfos.second}]") }
        }

        verify(dataSearchValidationService).validate(rootExpression.toFieldExpressions(false), searchDescriptor)
    }

    @Test
    fun search_without_params() {
        val rootClass = Person::class.java
        val firstName = "John"
        val lastName = "Doe"
        val searchDescriptorId = "person"

        val searchDescriptor = createSearchDescriptor()
        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(searchDescriptor)

        val rootExpression = RootExpressionImpl<Person>()
        whenever(expressionMapper.toExpression(any(), eq(rootClass))).thenReturn(rootExpression)

        val personInfos = createPerson(firstName, lastName)
        whenever(dataSearchService.findAll(rootExpression, searchDescriptor)).thenReturn(listOf(personInfos.first))

        mockMvc.get("/search/$searchDescriptorId") {
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[${personInfos.second}]") }
        }

        verify(dataSearchValidationService).validate(rootExpression.toFieldExpressions(false), searchDescriptor)
    }

    @Test
    fun search_with_empty_result() {
        val rootClass = Person::class.java
        val firstName = "John"
        val searchDescriptorId = "person"
        val fieldPath = "firstName"
        val fieldValue = firstName

        val searchDescriptor = createSearchDescriptor()
        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(searchDescriptor)

        val fieldInfo = FieldInfo(fieldPath, "firstName", rootClass)
        val rootExpression = RootExpressionImpl<Person>(ExpressionUtils.equals(fieldInfo, fieldValue))
        whenever(expressionMapper.toExpression(any(), eq(rootClass))).thenReturn(rootExpression)

        whenever(dataSearchService.findAll(rootExpression, searchDescriptor)).thenReturn(emptyList<Any?>())

        mockMvc.get("/search/$searchDescriptorId") {
            param(fieldPath, firstName)
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[]") }
        }

        verify(dataSearchValidationService).validate(rootExpression.toFieldExpressions(false), searchDescriptor)
    }

    @Test
    fun search_with_bad_descriptor_id() {
        val searchDescriptorId = "unknown"

        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(null)

        mockMvc.get("/search/$searchDescriptorId") {
        }.andExpect {
            status { isNotFound }
            status { reason("Not Found") }
            content { string("") }
        }

        verifyZeroInteractions(expressionMapper)
        verifyZeroInteractions(dataSearchValidationService)
        verifyZeroInteractions(dataSearchService)
    }

    @Test
    fun search_with_validation_errors() {
        val rootClass = Person::class.java
        val searchDescriptorId = "person"

        val searchDescriptor = createSearchDescriptor()
        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(searchDescriptor)

        val rootExpression = RootExpressionImpl<Person>()
        whenever(expressionMapper.toExpression(any(), eq(rootClass))).thenReturn(rootExpression)

        val errorCode = "not-empty"
        val errorMessage = "The search must contain at least one query parameter."

        val error = DataSearchError(errorCode, errorMessage)
        whenever(dataSearchValidationService.validate(rootExpression.toFieldExpressions(false), searchDescriptor)).thenThrow(ValidationException(listOf(error)))

        mockMvc.get("/search/$searchDescriptorId") {
        }.andExpect {
            status { isBadRequest }
            status { reason("Validation Errors: [$errorCode: $errorMessage]") }
            content { string("") }
        }

        verifyZeroInteractions(dataSearchService)
    }

    @Test
    fun search_with_alias() {
        val rootClass = Person::class.java
        val firstName = "John"
        val lastName = "Doe"
        val searchDescriptorId = "person"
        val fieldPath = "first_name"
        val fieldValue = firstName

        val searchDescriptor = createSearchDescriptor()
        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(searchDescriptor)

        val fieldInfo = FieldInfo(fieldPath, "firstName", rootClass)
        val rootExpression = RootExpressionImpl<Person>(ExpressionUtils.equals(fieldInfo, fieldValue))
        whenever(expressionMapper.toExpression(any(), eq(rootClass))).thenReturn(rootExpression)

        val personInfos = createPerson(firstName, lastName)
        whenever(dataSearchService.findAll(rootExpression, searchDescriptor)).thenReturn(listOf(personInfos.first))

        mockMvc.get("/search/$searchDescriptorId") {
            param(fieldPath, firstName)
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[${personInfos.second}]") }
        }

        verify(dataSearchValidationService).validate(rootExpression.toFieldExpressions(false), searchDescriptor)
    }

    private fun createSearchDescriptor() = SearchDescriptorBuilder.builder<Person>().jpaSpecificationExecutor(mock()).build()

    private fun createPerson(firstName: String, lastName: String): Pair<PersonDto, String> {
        val json = ""
                .plus("{")
                .plus("\"firstName\":\"$firstName\",")
                .plus("\"lastName\":\"$lastName\",")
                .plus("\"email\":null,")
                .plus("\"phoneNumbers\":null,")
                .plus("\"nickNames\":null,")
                .plus("\"addresses\":null,")
                .plus("\"vehicles\":null")
                .plus("}")
        return PersonDto.Builder().firstName(firstName).lastName(lastName).build() to json
    }
}