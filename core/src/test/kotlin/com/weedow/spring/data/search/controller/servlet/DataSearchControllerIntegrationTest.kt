package com.weedow.spring.data.search.controller.servlet

import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.dto.PersonDto
import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.exception.SearchDescriptorNotFound
import com.weedow.spring.data.search.exception.ValidationException
import com.weedow.spring.data.search.service.DataSearchService
import com.weedow.spring.data.search.validation.DataSearchError
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
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@ExtendWith(MockitoExtension::class)
internal class DataSearchControllerIntegrationTest {

    @Mock
    lateinit var dataSearchService: DataSearchService

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
        val firstName = "John"
        val lastName = "Doe"
        val searchDescriptorId = "person"
        val fieldPath = "firstName"
        val fieldValue = firstName
        val params = LinkedMultiValueMap<String, String>()
        params.add(fieldPath, fieldValue)

        val personInfos = createPerson(firstName, lastName)
        whenever(dataSearchService.search(searchDescriptorId, params)).thenReturn(listOf(personInfos.first))

        mockMvc.get("/search/$searchDescriptorId") {
            param(fieldPath, firstName)
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[${personInfos.second}]") }
        }
    }

    @Test
    fun search_without_params() {
        val firstName = "John"
        val lastName = "Doe"
        val searchDescriptorId = "person"
        val params = LinkedMultiValueMap<String, String>()

        val personInfos = createPerson(firstName, lastName)
        whenever(dataSearchService.search(searchDescriptorId, params)).thenReturn(listOf(personInfos.first))

        mockMvc.get("/search/$searchDescriptorId") {
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[${personInfos.second}]") }
        }
    }

    @Test
    fun search_with_empty_result() {
        val firstName = "John"
        val searchDescriptorId = "person"
        val fieldPath = "firstName"
        val fieldValue = firstName
        val params = LinkedMultiValueMap<String, String>()
        params.add(fieldPath, fieldValue)

        whenever(dataSearchService.search(searchDescriptorId, params)).thenReturn(emptyList<Any?>())

        mockMvc.get("/search/$searchDescriptorId") {
            param(fieldPath, firstName)
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[]") }
        }
    }

    @Test
    fun search_with_bad_descriptor_id() {
        val searchDescriptorId = "unknown"
        val params = LinkedMultiValueMap<String, String>()

        whenever(dataSearchService.search(searchDescriptorId, params)).thenThrow(SearchDescriptorNotFound(searchDescriptorId))

        mockMvc.get("/search/$searchDescriptorId") {
        }.andExpect {
            status { isNotFound }
            status { reason("Not Found") }
            content { string("") }
        }
    }

    @Test
    fun search_with_validation_errors() {
        val searchDescriptorId = "person"
        val params = LinkedMultiValueMap<String, String>()

        val errorCode = "not-empty"
        val errorMessage = "The search must contain at least one query parameter."

        val error = DataSearchError(errorCode, errorMessage)
        whenever(dataSearchService.search(searchDescriptorId, params)).thenThrow(ValidationException(listOf(error)))

        mockMvc.get("/search/$searchDescriptorId") {
        }.andExpect {
            status { isBadRequest }
            status { reason("Validation Errors: [$errorCode: $errorMessage]") }
            content { string("") }
        }

        verifyZeroInteractions(dataSearchService)
    }

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