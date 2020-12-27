package com.weedow.spring.data.search.fieldpath

import com.neovisionaries.i18n.CountryCode
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.alias.AliasResolutionService
import com.weedow.spring.data.search.common.model.Address
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.utils.MAP_KEY
import com.weedow.spring.data.search.utils.MAP_VALUE
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class FieldPathResolverImplTest {

    @Mock
    lateinit var aliasResolutionService: AliasResolutionService

    @InjectMocks
    lateinit var fieldPathResolver: FieldPathResolverImpl

    @Test
    fun resolveFieldPath_for_direct_field() {
        val rootClass = Person::class.java
        val fieldPath = "firstName"

        whenever(aliasResolutionService.resolve(rootClass, "firstName")).thenReturn("firstName")

        val fieldPathInfo = fieldPathResolver.resolveFieldPath(rootClass, fieldPath)

        val resolvedFieldPath = fieldPath
        assertThat(fieldPathInfo).isEqualTo(FieldPathInfo(resolvedFieldPath, "firstName", String::class.java, rootClass))
    }

    @Test
    fun resolveFieldPath_for_subfield() {
        val rootClass = Person::class.java
        val fieldPath = "address.country"
        val parentClass = Address::class.java

        whenever(aliasResolutionService.resolve(rootClass, "address")).thenReturn("addressEntities")
        whenever(aliasResolutionService.resolve(parentClass, "country")).thenReturn("country")

        val fieldPathInfo = fieldPathResolver.resolveFieldPath(rootClass, fieldPath)

        val resolvedFieldPath = "addressEntities.country"
        assertThat(fieldPathInfo).isEqualTo(FieldPathInfo(resolvedFieldPath, "country", CountryCode::class.java, parentClass))
    }

    @Test
    fun resolveFieldPath_for_map_key() {
        val rootClass = Person::class.java
        val fieldPath = "characteristics.$MAP_KEY"
        val parentClass = Map::class.java

        whenever(aliasResolutionService.resolve(rootClass, "characteristics")).thenReturn("characteristics")
        whenever(aliasResolutionService.resolve(parentClass, MAP_KEY)).thenReturn(MAP_KEY)

        val fieldPathInfo = fieldPathResolver.resolveFieldPath(rootClass, fieldPath)

        val resolvedFieldPath = "characteristics.$MAP_KEY"
        assertThat(fieldPathInfo).isEqualTo(FieldPathInfo(resolvedFieldPath, MAP_KEY, String::class.java, parentClass))
    }

    @Test
    fun resolveFieldPath_for_map_value() {
        val rootClass = Person::class.java
        val fieldPath = "characteristics.$MAP_VALUE"
        val parentClass = Map::class.java

        whenever(aliasResolutionService.resolve(rootClass, "characteristics")).thenReturn("characteristics")
        whenever(aliasResolutionService.resolve(parentClass, MAP_VALUE)).thenReturn(MAP_VALUE)

        val fieldPathInfo = fieldPathResolver.resolveFieldPath(rootClass, fieldPath)

        val resolvedFieldPath = "characteristics.$MAP_VALUE"
        assertThat(fieldPathInfo).isEqualTo(FieldPathInfo(resolvedFieldPath, MAP_VALUE, String::class.java, parentClass))
    }

    @Test
    fun throw_exception_when_field_path_unresolved_for_map() {
        val rootClass = Person::class.java
        val fieldPath = "characteristics.unknown"
        val parentClass = Map::class.java

        whenever(aliasResolutionService.resolve(rootClass, "characteristics")).thenReturn("characteristics")
        whenever(aliasResolutionService.resolve(parentClass, "unknown")).thenReturn("unknown")

        assertThatThrownBy { fieldPathResolver.resolveFieldPath(rootClass, fieldPath) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageStartingWith("Invalid field path: $fieldPath. The part 'unknown' is not authorized for a parent field of type Map")
            .hasNoCause()
    }

    @Test
    fun throw_exception_when_field_path_unresolved() {
        val rootClass = Person::class.java
        val fieldPath = "unknown"

        whenever(aliasResolutionService.resolve(rootClass, "unknown")).thenReturn("unknown")

        assertThatThrownBy { fieldPathResolver.resolveFieldPath(rootClass, fieldPath) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageStartingWith("Could not resolve the field path [$fieldPath] from [$rootClass]")
            .hasCauseInstanceOf(NoSuchFieldException::class.java)
    }
}