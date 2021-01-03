package com.weedow.spring.data.search.controller.servlet

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.dto.PersonDto
import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.service.DataSearchService
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
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@ExtendWith(MockitoExtension::class)
internal class DataSearchControllerTest {

    @Mock
    private lateinit var dataSearchService: DataSearchService

    @Spy
    private val searchProperties: SearchProperties = SearchProperties()

    @Mock
    private lateinit var requestMappingHandlerMapping: RequestMappingHandlerMapping

    @InjectMocks
    lateinit var dataSearchController: DataSearchController

    @BeforeEach
    fun setUp() {
        whenever(searchProperties.basePath).thenReturn(SearchProperties.DEFAULT_BASE_PATH)

        val mapping = RequestMappingInfo
            .paths("/search/{searchDescriptorId}")
            .methods(RequestMethod.GET)
            .build()
        verify(requestMappingHandlerMapping).registerMapping(
            mapping,
            dataSearchController,
            DataSearchController::class.java.getMethod("search", String::class.java, MultiValueMap::class.java)
        )
    }

    @Test
    fun search_successfully() {
        val firstName = "John"
        val lastName = "Doe"
        val searchDescriptorId = "person"
        val fieldPath = "firstName"
        val fieldValue = firstName
        val params = LinkedMultiValueMap<String, String>()
        params.add(fieldPath, fieldValue)

        val person = PersonDto.Builder().firstName(firstName).lastName(lastName).build()
        whenever(dataSearchService.search(searchDescriptorId, params)).thenReturn(listOf(person))

        val responseEntity = dataSearchController.search(searchDescriptorId, params)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body)
            .isInstanceOf(List::class.java)
            .hasOnlyElementsOfType(PersonDto::class.java)
            .extracting("firstName", "lastName")
            .containsExactly(Tuple.tuple(firstName, lastName))
    }

}