package com.weedow.spring.data.search.controller

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.descriptor.SearchDescriptorService
import com.weedow.spring.data.search.common.dto.PersonDto
import com.weedow.spring.data.search.common.PersonDtoMapper
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.exception.SearchDescriptorNotFound
import com.weedow.spring.data.search.expression.ExpressionMapper
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.service.DataSearchService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.util.LinkedMultiValueMap

@ExtendWith(MockitoExtension::class)
internal class DataSearchControllerTest {

    @Mock
    lateinit var searchDescriptorService: SearchDescriptorService

    @Mock
    lateinit var expressionMapper: ExpressionMapper

    @Mock
    lateinit var dataSearchService: DataSearchService

    @InjectMocks
    lateinit var dataSearchController: DataSearchController

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
            on { this.dtoMapper }.doReturn(PersonDtoMapper())
        }
        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(searchDescriptor)

        val rootExpression = mock<RootExpression<Person>>()
        whenever(expressionMapper.toExpression(params, rootClass)).thenReturn(rootExpression)

        val person = Person(firstName, lastName)
        whenever(dataSearchService.findAll(rootExpression, searchDescriptor)).thenReturn(listOf(person))

        val responseEntity = dataSearchController.search(searchDescriptorId, params)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body)
                .isInstanceOf(List::class.java)
                .hasOnlyElementsOfType(PersonDto::class.java)
                .extracting("firstName", "lastName")
                .containsExactly(Tuple.tuple(firstName, lastName))
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
        verifyZeroInteractions(dataSearchService)
    }
}