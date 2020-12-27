package com.weedow.spring.data.search.autoconfigure

import com.weedow.spring.data.search.alias.AliasResolutionService
import com.weedow.spring.data.search.alias.AliasResolver
import com.weedow.spring.data.search.alias.DefaultAliasResolutionService
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.common.model.VehicleType
import com.weedow.spring.data.search.descriptor.DefaultSearchDescriptorService
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.descriptor.SearchDescriptorService
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutor
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.convert.ConversionException
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.data.convert.ReadingConverter
import org.springframework.stereotype.Component
import java.lang.reflect.Field

/**
 * Class to test Bean injection without SearchConfigurer implementation.
 */
@SpringBootTest(properties = ["spring.main.web-application-type=servlet"])
internal class DataSearchAutoConfigurationIntegrationTest {

    @Autowired
    private lateinit var searchDescriptorService: SearchDescriptorService

    @Autowired
    private lateinit var searchAliasResolutionService: AliasResolutionService

    @Autowired
    private lateinit var searchConversionService: ConversionService

    @Test
    fun add_custom_search_descriptor() {
        assertThat(searchDescriptorService).isInstanceOf(DefaultSearchDescriptorService::class.java)

        val searchDescriptor = searchDescriptorService.getSearchDescriptor("my_descriptor_id")
        assertThat(searchDescriptor).isNotNull
            .extracting("id", "entityClass")
            .contains("my_descriptor_id", Person::class.java)
    }

    @Test
    fun add_custom_alias_resolver() {
        assertThat(searchAliasResolutionService).isInstanceOf(DefaultAliasResolutionService::class.java)

        val fieldName = searchAliasResolutionService.resolve(Person::class.java, "first_name")
        assertThat(fieldName).isEqualTo("firstName")
    }

    @Test
    fun add_custom_converter() {
        assertThat(searchConversionService).isInstanceOf(DefaultConversionService::class.java)

        var vehicleType: VehicleType?

        vehicleType = searchConversionService.convert("car", VehicleType::class.java)
        assertThat(vehicleType).isNotNull.isEqualTo(VehicleType.CAR)
        vehicleType = searchConversionService.convert("motorbike", VehicleType::class.java)
        assertThat(vehicleType).isNotNull.isEqualTo(VehicleType.MOTORBIKE)
        vehicleType = searchConversionService.convert("scooter", VehicleType::class.java)
        assertThat(vehicleType).isNotNull.isEqualTo(VehicleType.SCOOTER)
        vehicleType = searchConversionService.convert("van", VehicleType::class.java)
        assertThat(vehicleType).isNotNull.isEqualTo(VehicleType.VAN)
        vehicleType = searchConversionService.convert("truck", VehicleType::class.java)
        assertThat(vehicleType).isNotNull.isEqualTo(VehicleType.TRUCK)

        assertThatThrownBy { searchConversionService.convert("unknown", VehicleType::class.java) }
            .isInstanceOf(ConversionException::class.java)
            .hasMessageContaining("java.lang.IllegalArgumentException: Vehicle type not found: unknown")
            .hasCauseExactlyInstanceOf(IllegalArgumentException::class.java)
            .cause.hasMessage("Vehicle type not found: unknown")
    }

}

@Component
class MySearchDescriptor : SearchDescriptor<Person> {
    override val id: String
        get() = "my_descriptor_id"
    override val entityClass: Class<Person>
        get() = Person::class.java
    override val queryDslSpecificationExecutor: QueryDslSpecificationExecutor<Person>?
        get() = null
}

@Component
class MyAliasResolver : AliasResolver {
    override fun supports(entityClass: Class<*>, field: Field): Boolean = field.name == "firstName"

    override fun resolve(entityClass: Class<*>, field: Field): List<String> = listOf("first_name")
}

@Component
@ReadingConverter
class MyConverter : Converter<String, VehicleType> {
    override fun convert(source: String): VehicleType {
        return when (source) {
            "car" -> VehicleType.CAR
            "motorbike" -> VehicleType.MOTORBIKE
            "scooter" -> VehicleType.SCOOTER
            "van" -> VehicleType.VAN
            "truck" -> VehicleType.TRUCK
            else -> throw IllegalArgumentException("Vehicle type not found: $source")
        }
    }
}