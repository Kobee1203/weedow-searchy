package com.weedow.searchy.sample

import com.weedow.searchy.config.SearchyProperties
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@DirtiesContext
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SampleApplicationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Value("classpath:data/result.json")
    private lateinit var jsonResult: Resource

    @Test
    fun search() {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val firstName = "John"
        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            param("firstName", firstName)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json(jsonResult.file.readText()) }
        }
    }

}
