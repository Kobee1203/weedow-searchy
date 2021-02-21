package com.weedow.searchy.sample.mongodb

import com.weedow.searchy.config.SearchyProperties
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SampleAppMongoAdvancedQueryTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("GET /search/person?query=firstName='John' AND lastName='Doe'")
    fun search_and_condition(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "firstName='John' AND lastName='Doe' AND birthday = '1981-03-12T10:36:00'")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName(
        "GET /search/person?query=firstName='John' OR firstName='Jane'," +
                "GET /search/person?query=birthday='1981-03-12T10:36:00' OR birthday='1981-11-26T12:30:00'"
    )
    fun search_or_condition(@Value("classpath:data/result_john_doe_jane_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "firstName='John' OR firstName='Jane'")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "birthday='1981-03-12T10:36:00' OR birthday='1981-11-26T12:30:00'")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName(
        "GET /search/person?query=NOT(firstName='Bob')," +
                "GET /search/person?query=NOT(firstName='John' OR firstName='Jane')," +
                "GET /search/person?query=birthday IS NOT NULL"
    )
    fun search_not_condition(
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource,
        @Value("classpath:data/result_bob_nullos.json") result_bob_nullos: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "NOT(firstName='Bob')")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "NOT(firstName='John' OR firstName='Jane')")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_bob_nullos.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "birthday IS NOT NULL")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }
    }

    @Test
    @DisplayName(
        "GET /search/person?query=firstName MATCHES 'Jo*'," +
                "GET /search/person?query=firstName MATCHES '*hn'," +
                "GET /search/person?query=email MATCHES '*acme*'"
    )
    fun search_matches_condition(
        @Value("classpath:data/result_john_doe.json") result_john_doe: Resource,
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "firstName MATCHES 'Jo*'")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "firstName MATCHES '*hn'")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "email MATCHES '*acme*'")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }
    }

    @Test
    @DisplayName(
        "GET /search/person?query=firstName IMATCHES 'JO*'," +
                "GET /search/person?query=firstName IMATCHES '*HN'," +
                "GET /search/person?query=email IMATCHES '*AcMe*'"
    )
    fun search_imatches_condition(
        @Value("classpath:data/result_john_doe.json") result_john_doe: Resource,
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "firstName IMATCHES 'JO*'")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "firstName IMATCHES '*HN'")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "email IMATCHES '*AcMe*'")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }
    }

    @Test
    @DisplayName(
        "GET /search/person?query=birthday < CURRENT_DATE," +
                "GET /search/person?query=birthday < '1981-03-12T11:00:00'," +
                "GET /search/person?query=height < 174," +
                "GET /search/person?query=height <= 174"
    )
    fun search_less_than_condition(
        @Value("classpath:data/result_john_doe.json") result_john_doe: Resource,
        @Value("classpath:data/result_jane_doe.json") result_jane_doe: Resource,
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "birthday < CURRENT_DATE")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "birthday < '1981-03-12T11:00:00'")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "height < 174")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_jane_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "height <= 174")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }
    }

    @Test
    @DisplayName(
        "GET /search/person?query=birthday > CURRENT_DATE," +
                "GET /search/person?query=birthday > '1981-03-12T10:00:00'," +
                "GET /search/person?query=height > 165," +
                "GET /search/person?query=height >= 165"
    )
    fun search_greater_than_condition(
        @Value("classpath:data/result_john_doe.json") result_john_doe: Resource,
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "birthday > CURRENT_DATE")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo("[]") }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "birthday > '1981-03-12T10:00:00'")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "height > 165")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "height >= 165")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }
    }

    @Test
    @DisplayName(
        "GET /search/person?query=" +
                "(mainAddress.street IMATCHES 'RUE*' OR otherAddresses.street IMATCHES '*MARKT*') " +
                // "AND (vehicles.brand='Renault' OR vehicles.brand='Porsche') " +
                // "AND job.active = true AND (job.salary >=50000 AND job.salary<610000) " +
                "AND characteristics.key = 'hair'," +

                "GET /search/person?query=" +
                "(mainAddress.street IMATCHES 'RUE*' OR otherAddresses.street IMATCHES '*MARKT*') " +
                // "AND (vehicles.brand='Renault' OR vehicles.brand='Porsche') " +
                // "AND job.active = true AND (job.salary >=50000 AND job.salary<610000) " +
                "AND characteristics.key = 'hair'" +
                // "AND vehicles.features.value.description IMATCHES '*Navigation Services*'"
                "AND mainAddress.roomDescription.value.type='OFFICE'"
    )
    fun search_complex_query(
        @Value("classpath:data/result_john_doe.json") result_john_doe: Resource,
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param(
                "query",
                "(mainAddress.street IMATCHES 'RUE*' OR otherAddresses.street IMATCHES '*MARKT*') " +
                        // "AND (vehicles.brand='Renault' OR vehicles.brand='Porsche') " +
                        // "AND job.active = true AND (job.salary >=50000 AND job.salary<610000) " +
                        "AND characteristics.key = 'hair'"
            )
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param(
                "query",
                "(mainAddress.street IMATCHES 'RUE*' OR otherAddresses.street IMATCHES '*MARKT*') " +
                        // "AND (vehicles.brand='Renault' OR vehicles.brand='Porsche') " +
                        // "AND job.active = true AND (job.salary >=50000 AND job.salary<610000) " +
                        "AND characteristics.key = 'hair' " +
                        // "AND vehicles.features.value.description IMATCHES '*Navigation Services*'"
                        "AND mainAddress.roomDescription.value.type='OFFICE'"
            )
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe.file.readText()) }
        }
    }

    @Test
    @DisplayName(
        "GET /search/person?query=firstName IN ('John', 'Jane' , 'Bob')," +
                "GET /search/person?query=birthday IN ('1981-03-12T10:36:00', '1981-11-26T12:30:00')" +
                "GET /search/person?query=height IN (174, 165)"
    )
    fun search_in_condition(
        @Value("classpath:data/result_all.json") result_all: Resource,
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "firstName IN ('John', 'Jane' , 'Bob')")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_all.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "birthday IN ('1981-03-12T10:36:00', '1981-11-26T12:30:00')")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("query", "height IN (174, 165)")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }
    }
}