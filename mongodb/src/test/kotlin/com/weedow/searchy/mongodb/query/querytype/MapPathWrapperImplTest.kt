package com.weedow.searchy.mongodb.query.querytype

import com.nhaarman.mockitokotlin2.mock
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.dsl.MapPath
import com.querydsl.core.types.dsl.NumberPath
import com.weedow.searchy.query.querytype.ElementType
import org.apache.commons.lang3.reflect.FieldUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class MapPathWrapperImplTest {

    @Test
    fun test() {
        val keyType = String::class.java
        val valueType = Double::class.java
        val metadata = mock<PathMetadata>()
        val elementType = ElementType.MAP_KEY
        val queryType = NumberPath::class.java
        val mapPath = mock<MapPath<String, Double, NumberPath<Double>>> {
            on { this.keyType }.thenReturn(keyType)
            on { this.valueType }.thenReturn(valueType)
            on { this.metadata }.thenReturn(metadata)
        }
        FieldUtils.writeField(mapPath, "queryType", queryType, true)
        val mapPathWrapper = MapPathWrapperImpl(mapPath, elementType)

        assertThat(mapPathWrapper.keyType).isEqualTo(keyType)
        assertThat(mapPathWrapper.valueType).isEqualTo(valueType)
        assertThat(mapPathWrapper.metadata).isEqualTo(metadata)
        assertThat(mapPathWrapper.elementType).isEqualTo(elementType)
        assertThat(FieldUtils.readField(mapPathWrapper, "queryType", true)).isEqualTo(queryType)
    }
}