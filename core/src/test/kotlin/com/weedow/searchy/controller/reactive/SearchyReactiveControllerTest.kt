package com.weedow.searchy.controller.reactive

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.searchy.common.dto.PersonDto
import com.weedow.searchy.config.SearchyProperties
import com.weedow.searchy.service.SearchyService
import org.assertj.core.api.Assertions.assertThat
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
internal class SearchyReactiveControllerTest {

    @Mock
    private lateinit var searchyService: SearchyService

    @Spy
    private val searchyProperties: SearchyProperties = SearchyProperties()

    @Mock
    private lateinit var requestMappingHandlerMapping: RequestMappingHandlerMapping

    @InjectMocks
    lateinit var searchyController: SearchyReactiveController

    @BeforeEach
    fun setUp() {
        whenever(searchyProperties.basePath).thenReturn(SearchyProperties.DEFAULT_BASE_PATH)

        val mapping = RequestMappingInfo
            .paths("/search/{searchyDescriptorId}")
            .methods(RequestMethod.GET)
            .build()
        verify(requestMappingHandlerMapping).registerMapping(
            mapping,
            searchyController,
            SearchyReactiveController::class.java.getMethod("search", String::class.java, MultiValueMap::class.java)
        )
    }

    @Test
    fun search_successfully() {
        val firstName = "John"
        val lastName = "Doe"
        val searchyDescriptorId = "person"
        val fieldPath = "firstName"
        val fieldValue = firstName
        val params = LinkedMultiValueMap<String, String>()
        params.add(fieldPath, fieldValue)

        val person = PersonDto.Builder().firstName(firstName).lastName(lastName).build()
        whenever(searchyService.search(searchyDescriptorId, params)).thenReturn(listOf(person))

        val responseEntity = searchyController.search(searchyDescriptorId, params)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body)
            .isInstanceOf(List::class.java)
            .hasOnlyElementsOfType(PersonDto::class.java)
            .extracting("firstName", "lastName")
            .containsExactly(Tuple.tuple(firstName, lastName))
    }

}