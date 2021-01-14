package com.weedow.searchy.autoconfigure

import com.weedow.searchy.alias.AliasResolutionService
import com.weedow.searchy.alias.AliasResolver
import com.weedow.searchy.alias.DefaultAliasResolutionService
import com.weedow.searchy.common.model.Person
import com.weedow.searchy.common.model.VehicleType
import com.weedow.searchy.descriptor.DefaultSearchyDescriptorService
import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.descriptor.SearchyDescriptorService
import com.weedow.searchy.dto.DtoMapper
import com.weedow.searchy.query.specification.SpecificationExecutor
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
 * Class to test Bean injection without SearchyConfigurer implementation.
 */
@SpringBootTest(properties = ["spring.main.web-application-type=servlet"])
internal class SearchyAutoConfigurationIntegrationTest {

    @Autowired
    private lateinit var searchyDescriptorService: SearchyDescriptorService

    @Autowired
    private lateinit var searchAliasResolutionService: AliasResolutionService

    @Autowired
    private lateinit var searchConversionService: ConversionService

    @Test
    fun add_custom_search_descriptor() {
        assertThat(searchyDescriptorService).isInstanceOf(DefaultSearchyDescriptorService::class.java)

        val searchyDescriptor = searchyDescriptorService.getSearchyDescriptor("my_descriptor_id")
        assertThat(searchyDescriptor).isNotNull
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
class MySearchyDescriptor : SearchyDescriptor<Person> {
    override val id: String = "my_descriptor_id"
    override val entityClass: Class<Person> = Person::class.java
    override val dtoMapper: DtoMapper<Person, *>? = null
    override val specificationExecutor: SpecificationExecutor<Person>? = null
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