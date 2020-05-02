package com.weedow.spring.data.search.controller

import com.nhaarman.mockitokotlin2.*
import com.weedow.spring.data.search.descriptor.SearchDescriptorBuilder
import com.weedow.spring.data.search.descriptor.SearchDescriptorService
import com.weedow.spring.data.search.example.model.Person
import com.weedow.spring.data.search.field.FieldInfo
import com.weedow.spring.data.search.field.FieldMapper
import com.weedow.spring.data.search.service.DataSearchService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(DataSearchController::class)
class DataSearchControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var searchDescriptorService: SearchDescriptorService

    @MockBean
    lateinit var fieldMapper: FieldMapper

    @MockBean
    lateinit var dataSearchService: DataSearchService

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

        val fieldInfos = mutableListOf(
                FieldInfo(fieldPath, rootClass, rootClass.getDeclaredField("firstName"), String::class.java, mutableListOf(fieldValue))
        )
        whenever(fieldMapper.toFieldInfos(any(), eq(rootClass))).thenReturn(fieldInfos)

        val personInfos = createPerson(firstName, lastName)
        whenever(dataSearchService.findAll(fieldInfos, searchDescriptor)).thenReturn(listOf(personInfos.first))

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
        val rootClass = Person::class.java
        val firstName = "John"
        val lastName = "Doe"
        val searchDescriptorId = "person"

        val searchDescriptor = createSearchDescriptor()
        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(searchDescriptor)

        val fieldInfos = emptyList<FieldInfo>()
        whenever(fieldMapper.toFieldInfos(any(), eq(rootClass))).thenReturn(fieldInfos)

        val personInfos = createPerson(firstName, lastName)
        whenever(dataSearchService.findAll(fieldInfos, searchDescriptor)).thenReturn(listOf(personInfos.first))

        mockMvc.get("/search/$searchDescriptorId") {
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[${personInfos.second}]") }
        }
    }

    @Test
    fun search_with_empty_result() {
        val rootClass = Person::class.java
        val searchDescriptorId = "person"

        val searchDescriptor = createSearchDescriptor()
        whenever(searchDescriptorService.getSearchDescriptor(searchDescriptorId)).thenReturn(searchDescriptor)

        val fieldInfos = emptyList<FieldInfo>()
        whenever(fieldMapper.toFieldInfos(any(), eq(rootClass))).thenReturn(fieldInfos)

        whenever(dataSearchService.findAll(fieldInfos, searchDescriptor)).thenReturn(emptyList())

        mockMvc.get("/search/$searchDescriptorId") {
            param("firstName", "John")
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json("[]") }
        }
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

        verifyZeroInteractions(fieldMapper)
        verifyZeroInteractions(dataSearchService)
    }

    private fun createSearchDescriptor() = SearchDescriptorBuilder.builder<Person>().jpaSpecificationExecutor(mock()).build()

    private fun createPerson(firstName: String, lastName: String): Pair<Person, String> {
        val json = ""
                .plus("{")
                .plus("\"id\":null,")
                .plus("\"firstName\":\"$firstName\",")
                .plus("\"lastName\":\"$lastName\",")
                .plus("\"email\":null,")
                .plus("\"addressEntities\":null,")
                .plus("\"jobEntity\":null,")
                .plus("\"phoneNumbers\":null,")
                .plus("\"nickNames\":null,")
                .plus("\"new\":true")
                .plus("}")
        return Person(firstName, lastName) to json
    }
}

@SpringBootApplication
class MockMvcApplication