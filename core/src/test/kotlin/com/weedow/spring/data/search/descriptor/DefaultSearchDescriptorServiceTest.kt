package com.weedow.spring.data.search.descriptor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.example.model.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultSearchDescriptorServiceTest {

    @Test
    fun add_and_find_SearchDescriptor_successfully() {
        val searchDescriptorId = "person"
        val searchDescriptor: SearchDescriptor<Person> = mock()
        whenever(searchDescriptor.id).thenReturn(searchDescriptorId)

        val searchDescriptorService = DefaultSearchDescriptorService()

        searchDescriptorService.addSearchDescriptor(searchDescriptor)

        val resultSearchDescriptor = searchDescriptorService.getSearchDescriptor(searchDescriptorId)

        assertThat(resultSearchDescriptor).isEqualTo(searchDescriptor)
    }

    @Test
    fun add_and_find_SearchDescriptor_unsuccessfully() {
        val searchDescriptorId = "person"
        val searchDescriptor: SearchDescriptor<Person> = mock()
        whenever(searchDescriptor.id).thenReturn(searchDescriptorId)

        val searchDescriptorService = DefaultSearchDescriptorService()

        searchDescriptorService.addSearchDescriptor(searchDescriptor)

        val resultSearchDescriptor = searchDescriptorService.getSearchDescriptor("unknown")

        assertThat(resultSearchDescriptor).isNull()
    }
}