package com.weedow.spring.data.search.sample

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringDataSearchSampleApplicationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun contextLoads() {
        val firstName = "John"
        val searchDescriptorId = "person"

        val result = "[{" +
                "\"firstName\":\"John\"," +
                "\"lastName\":\"Doe\"," +
                "\"email\":\"john.doe@acme.com\"," +
                "\"addresses\":[{" +
                "\"street\":\"Rue des Peupliers\"," +
                "\"city\":\"Plaisir\"," +
                "\"zipCode\":\"78370\"," +
                "\"country\":\"FR\"," +
                "\"id\":1" +
                "}]," +
                "\"phoneNumbers\":[\"+33612345678\"]," +
                "\"nickNames\":[\"Johnny\"]," +
                "\"id\":1" +
                "}]"

        mockMvc.get("/search/$searchDescriptorId") {
            param("firstName", firstName)
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json(result) }
        }
    }

}
