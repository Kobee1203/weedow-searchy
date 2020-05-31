package com.weedow.spring.data.search.sample

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringDataSearchSampleApplicationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Value("classpath:data/result.json")
    private lateinit var jsonResult: Resource

    @Test
    fun contextLoads() {
        val firstName = "John"
        val searchDescriptorId = "person"

        mockMvc.get("/search/$searchDescriptorId") {
            param("firstName", firstName)
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json(jsonResult.file.readText()) }
        }
    }

}
