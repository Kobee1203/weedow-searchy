package com.weedow.spring.data.search.field

import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.example.model.Person
import com.weedow.spring.data.search.utils.NullValue
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.convert.ConversionService
import org.springframework.util.LinkedMultiValueMap

@ExtendWith(MockitoExtension::class)
internal class FieldMapperImplTest {

    @Mock
    lateinit var fieldPathResolver: FieldPathResolver

    @Mock
    lateinit var conversionService: ConversionService

    @InjectMocks
    lateinit var fieldMapper: FieldMapperImpl

    @Test
    fun toFieldInfos_with_valid_value() {
        val rootClass = Person::class.java
        val fieldPath = "firstName"
        val fieldValue = "John"
        val fieldType = String::class.java
        val field = rootClass.getDeclaredField("firstName")
        val params = LinkedMultiValueMap<String, String>()
        params.add(fieldPath, fieldValue)

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, rootClass, field, fieldType))

        val convertedValue = "John"
        whenever(conversionService.convert(fieldValue, fieldType))
                .thenReturn(convertedValue)

        val fieldInfos = fieldMapper.toFieldInfos(params, rootClass)

        assertThat(fieldInfos)
                .isInstanceOf(List::class.java)
                .hasOnlyElementsOfType(FieldInfo::class.java)
                .extracting("fieldPath", "parentClass", "field", "fieldClass", "fieldValues")
                .containsExactly(
                        Tuple.tuple(fieldPath, rootClass, field, fieldType, arrayListOf(convertedValue))
                )
    }

    @Test
    fun toFieldInfos_with_null_value() {
        val rootClass = Person::class.java
        val fieldPath = "firstName"
        val fieldValue = NullValue.NULL_VALUE
        val fieldType = String::class.java
        val field = rootClass.getDeclaredField("firstName")
        val params = LinkedMultiValueMap<String, String>()
        params.add(fieldPath, fieldValue)

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, rootClass, field, fieldType))

        val fieldInfos = fieldMapper.toFieldInfos(params, rootClass)

        assertThat(fieldInfos)
                .isInstanceOf(List::class.java)
                .hasOnlyElementsOfType(FieldInfo::class.java)
                .extracting("fieldPath", "parentClass", "field", "fieldClass", "fieldValues")
                .containsExactly(
                        Tuple.tuple(fieldPath, rootClass, field, fieldType, arrayListOf(NullValue.INSTANCE))
                )

        verifyZeroInteractions(conversionService)
    }

    @Test
    fun toFieldInfos_with_empty_params() {
        val rootClass = Person::class.java
        val params = LinkedMultiValueMap<String, String>()

        val fieldInfos = fieldMapper.toFieldInfos(params, rootClass)

        assertThat(fieldInfos).isEmpty()

        verifyZeroInteractions(fieldPathResolver)
        verifyZeroInteractions(conversionService)
    }
}