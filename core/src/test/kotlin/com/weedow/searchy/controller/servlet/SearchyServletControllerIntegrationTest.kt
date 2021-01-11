package com.weedow.searchy.controller.servlet

import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.searchy.common.dto.PersonDto
import com.weedow.searchy.config.SearchyProperties
import com.weedow.searchy.exception.SearchyDescriptorNotFound
import com.weedow.searchy.exception.ValidationException
import com.weedow.searchy.service.SearchyService
import com.weedow.searchy.validation.SearchyError
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
internal class SearchyServletControllerIntegrationTest {

    @Mock
    private lateinit var searchyService: SearchyService

    @Spy
    private val searchyProperties: SearchyProperties = SearchyProperties()

    @Spy
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping = RequestMappingHandlerMapping()

    @InjectMocks
    lateinit var searchyServletController: SearchyServletController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        whenever(searchyProperties.basePath).thenReturn(SearchyProperties.DEFAULT_BASE_PATH)

        this.mockMvc = MockMvcBuilders.standaloneSetup(searchyServletController)
            .setCustomHandlerMapping { requestMappingHandlerMapping }
            .build()
    }

    @Test
    fun search_with_params() {
        val firstName = "John"
        val lastName = "Doe"
        val searchyDescriptorId = "person"
        val fieldPath = "firstName"
        val fieldValue = firstName
        val params = LinkedMultiValueMap<String, String>()
        params.add(fieldPath, fieldValue)

        val personInfos = createPerson(firstName, lastName)
        whenever(searchyService.search(searchyDescriptorId, params)).thenReturn(listOf(personInfos.first))

        mockMvc.get("/search/$searchyDescriptorId") {
            param(fieldPath, firstName)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[${personInfos.second}]") }
        }
    }

    @Test
    fun search_without_params() {
        val firstName = "John"
        val lastName = "Doe"
        val searchyDescriptorId = "person"
        val params = LinkedMultiValueMap<String, String>()

        val personInfos = createPerson(firstName, lastName)
        whenever(searchyService.search(searchyDescriptorId, params)).thenReturn(listOf(personInfos.first))

        mockMvc.get("/search/$searchyDescriptorId") {
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[${personInfos.second}]") }
        }
    }

    @Test
    fun search_with_empty_result() {
        val firstName = "John"
        val searchyDescriptorId = "person"
        val fieldPath = "firstName"
        val fieldValue = firstName
        val params = LinkedMultiValueMap<String, String>()
        params.add(fieldPath, fieldValue)

        whenever(searchyService.search(searchyDescriptorId, params)).thenReturn(emptyList<Any?>())

        mockMvc.get("/search/$searchyDescriptorId") {
            param(fieldPath, firstName)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[]") }
        }
    }

    @Test
    fun search_with_bad_descriptor_id() {
        val searchyDescriptorId = "unknown"
        val params = LinkedMultiValueMap<String, String>()

        whenever(searchyService.search(searchyDescriptorId, params)).thenThrow(SearchyDescriptorNotFound(searchyDescriptorId))

        mockMvc.get("/search/$searchyDescriptorId") {
        }.andExpect {
            status { isNotFound() }
            status { reason("Not Found") }
            content { string("") }
        }
    }

    @Test
    fun search_with_validation_errors() {
        val searchyDescriptorId = "person"
        val params = LinkedMultiValueMap<String, String>()

        val errorCode = "not-empty"
        val errorMessage = "The search must contain at least one query parameter."

        val error = SearchyError(errorCode, errorMessage)
        whenever(searchyService.search(searchyDescriptorId, params)).thenThrow(ValidationException(listOf(error)))

        mockMvc.get("/search/$searchyDescriptorId") {
        }.andExpect {
            status { isBadRequest() }
            status { reason("Validation Errors: [$errorCode: $errorMessage]") }
            content { string("") }
        }

        verifyZeroInteractions(searchyService)
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