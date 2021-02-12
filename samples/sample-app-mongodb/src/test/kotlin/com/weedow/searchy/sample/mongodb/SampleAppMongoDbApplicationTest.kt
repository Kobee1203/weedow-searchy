package com.weedow.searchy.sample.mongodb

import com.weedow.searchy.config.SearchyProperties
import net.javacrumbs.jsonunit.core.Option
import net.javacrumbs.jsonunit.spring.jsonContent
import org.junit.jupiter.api.Disabled
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
class SampleAppMongoDbApplicationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("GET /search/person")
    fun search_all(@Value("classpath:data/result_all.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
            }
    }

    @Test
    @DisplayName("GET /search/person?firstName=John")
    fun search_from_one_field_as_query_param(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("firstName", "John")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName("GET /search/person?firstName=John&lastName=Doe")
    fun search_from_multiple_fields_as_query_param(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("firstName", "John")
            this.param("lastName", "Doe")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName("GET /search/person?firstName=John&firstName=Jane")
    fun search_from_one_field_as_query_param_with_multiple_values(@Value("classpath:data/result_john_doe_jane_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("firstName", "John")
            this.param("firstName", "Jane")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName("GET /search/person?birthday=1981-03-12T10:36:00")
    fun search_from_date_field_as_query_param(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("birthday", "1981-03-12T10:36:00")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName("GET /search/person?mainAddress.zipCode=78370")
    fun search_from_nested_string_field_as_query_param(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("mainAddress.zipCode", "78370")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName("GET /search/person?nickNames=Johnny")
    fun search_from_nested_string_collection_field_as_query_param(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("nickNames", "Johnny")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName("GET /search/person?mainAddress.country=FR")
    fun search_from_nested_enum_field_as_query_param(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("mainAddress.country", "FR")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName("GET /search/person?mainAddress.city.name=Plaisir")
    fun search_from_nested_field_as_query_param(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("mainAddress.city.name", "Plaisir")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName("GET /search/person?otherAddresses.street=Rue des Petits Pois")
    fun search_from_nested_object_collection_field_as_query_param(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("otherAddresses.street", "Rue des Petits Pois")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName("GET /search/person?otherAddresses.city.name=Le-Bois-Plage-En-Ré")
    fun search_from_nested_field_of_object_collection_field_as_query_param(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("otherAddresses.city.name", "Le-Bois-Plage-En-Ré")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName("GET /search/person?otherAddresses.city.name=Le-Bois-Plage-En-Ré&otherAddresses.city.name=Amsterdam")
    fun search_from_nested_field_of_object_collection_field_as_query_param_with_multiple_values(@Value("classpath:data/result_john_doe_jane_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("otherAddresses.city.name", "Le-Bois-Plage-En-Ré")
            this.param("otherAddresses.city.name", "Amsterdam")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @DisplayName(
        "GET /search/person?characteristics.key=eyes," +
                "GET /search/person?characteristics.value=blue," +
                "GET /search/person?characteristics.key=eyes&characteristics.value=blue"
    )
    fun search_from_map_field_as_query_param(
        @Value("classpath:data/result_john_doe.json") result_john_doe: Resource,
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("characteristics.key", "eyes")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("characteristics.value", "blue")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("characteristics.key", "eyes")
            this.param("characteristics.value", "blue")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe.file.readText()) }
        }
    }

    @Test
    @DisplayName(
        "GET /search/person?mainAddress.roomDescription.key=kitchen," +
                "GET /search/person?mainAddress.roomDescription.value.floor=ground-floor," +
                "GET /search/person?otherAddresses.roomDescription.value.type=KITCHEN" +
                "GET /search/person?mainAddress.roomDescription.value.area=15&otherAddresses.roomDescription.value.windows=1"
    )
    fun search_from_nested_map_field_as_query_param(
        @Value("classpath:data/result_john_doe.json") result_john_doe: Resource,
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("mainAddress.roomDescription.key", "kitchen")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("mainAddress.roomDescription.value.floor", "ground-floor")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }

        // nested Map field from collection
        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("otherAddresses.roomDescription.value.type", "KITCHEN")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("mainAddress.roomDescription.value.area", "15")
            this.param("otherAddresses.roomDescription.value.windows", "1")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe.file.readText()) }
        }
    }

    @Test
    @Disabled
    @DisplayName(
        "GET /search/person?job.company=Acme," +
                "GET /search/person?job.active=true"
    )
    fun search_from_entity_field_as_query_param(
        @Value("classpath:data/result_john_doe.json") result_john_doe: Resource,
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("job.company", "Acme")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("job.active", "true")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }
    }

    @Test
    @Disabled
    @DisplayName(
        "GET /search/person?vehicles.brand=Renault," +
                "GET /search/person?vehicles.vehicleType=CAR"
    )
    fun search_from_entities_collection_field_as_query_param(
        @Value("classpath:data/result_john_doe.json") result_john_doe: Resource,
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("vehicles.brand", "Renault")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("vehicles.vehicleType", "CAR")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }
    }

    @Test
    @Disabled
    @DisplayName("GET /search/person?vehicles.features.value.name=gps")
    fun search_from_map_field_as_query_param_that_contains_entity_as_value(@Value("classpath:data/result_john_doe.json") result: Resource) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("vehicles.features.value.name", "gps")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result.file.readText()) }
        }
    }

    @Test
    @Disabled
    @DisplayName(
        "GET /search/person?tasks.key.name=Clean up the garage," +
                "GET /search/person?tasks.key.name=Clean up the garage&tasks.key.name=Go to the doctor's appointment"
    )
    fun search_from_map_field_as_query_param_that_contains_entity_as_key(
        @Value("classpath:data/result_john_doe.json") result_john_doe: Resource,
        @Value("classpath:data/result_john_doe_jane_doe.json") result_john_doe_jane_doe: Resource
    ) {
        val basePath = SearchyProperties.DEFAULT_BASE_PATH

        val searchyDescriptorId = "person"

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("tasks.key.name", "Clean up the garage")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe.file.readText()) }
        }

        mockMvc.get("$basePath/$searchyDescriptorId") {
            this.param("tasks.key.name", "Clean up the garage")
            this.param("tasks.key.name", "Go shopping") // John and Jane have got this task
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonContent { `when`(Option.IGNORING_ARRAY_ORDER).isEqualTo(result_john_doe_jane_doe.file.readText()) }
        }
    }
}
