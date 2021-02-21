package com.weedow.searchy.sample

import net.javacrumbs.jsonunit.spring.jsonContent
import org.junit.jupiter.api.DisplayName
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
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["weedow.searchy.base-path=/api"]
)
class SampleApplicationWithCustomBasePathTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("Search with custom base path: GET /api/person?firstName=John")
    fun search_with_custom_base_path(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = "/api"

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("firstName", "John")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result.file.readText()) }
        }
    }

}
