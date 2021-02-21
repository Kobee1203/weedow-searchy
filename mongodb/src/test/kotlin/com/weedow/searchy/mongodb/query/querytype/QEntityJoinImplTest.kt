package com.weedow.searchy.mongodb.query.querytype

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.types.*
import com.weedow.searchy.query.querytype.*
import com.weedow.searchy.utils.MAP_KEY
import com.weedow.searchy.utils.MAP_VALUE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.lang.reflect.AnnotatedElement
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
internal class QEntityJoinImplTest {

    @Mock
    private lateinit var qEntity: QEntity<*>

    @Mock
    private lateinit var propertyInfos: PropertyInfos

    @InjectMocks
    private lateinit var qEntityJoin: QEntityJoinImpl<*>

    @ParameterizedTest
    @EnumSource(value = ElementType::class, mode = EnumSource.Mode.EXCLUDE, names = ["MAP", "MAP_VALUE"])
    fun get(elementType: ElementType) {
        val fieldName = "myfield"
        val qPath = mock<QPath<*>>()

        whenever(propertyInfos.elementType).thenReturn(elementType)

        whenever(qEntity.get(fieldName)).thenReturn(qPath)

        assertThat(qEntityJoin.get(fieldName)).isSameAs(qPath)
    }

    @Test
    fun get_map_key() {
        val parentClass = Person::class.java

        val fieldName = MAP_KEY
        val elementType = ElementType.MAP_KEY
        val type = KEY_TYPE

        whenever(propertyInfos.elementType).thenReturn(ElementType.MAP)
        whenever(propertyInfos.parameterizedTypes).thenReturn(listOf(KEY_TYPE, VALUE_TYPE))
        whenever(propertyInfos.parentClass).thenReturn(parentClass)
        whenever(propertyInfos.fieldName).thenReturn(fieldName)

        val metadata = mock<PathMetadata>()
        whenever(qEntity.metadata).thenReturn(metadata)

        val otherPropertyInfos = mock<PropertyInfos>()
        whenever(
            propertyInfos.copy(
                parentClass = parentClass,
                fieldName = fieldName,
                elementType = elementType,
                type = type,
                parameterizedTypes = emptyList(),
                annotations = emptyList(),
                queryType = QEntityImpl::class.java
            )
        ).thenReturn(otherPropertyInfos)

        val qPath = qEntityJoin.get(fieldName)

        assertThat(qPath).isInstanceOf(QPathImpl::class.java)
        assertThat(qPath.propertyInfos).isEqualTo(otherPropertyInfos)
        assertThat(qPath.path).isInstanceOf(MapPathWrapperImpl::class.java)
        assertThat((qPath.path as PathWrapper).elementType).isEqualTo(ElementType.MAP_KEY)
        assertThat(qPath.path.type).isEqualTo(Map::class.java)
        assertThat(qPath.path.root).isInstanceOf(PathImpl::class.java)
        assertThat(qPath.path.root.type).isEqualTo(Map::class.java)
        assertThat(qPath.path.metadata).isEqualTo(metadata)

        verifyNoMoreInteractions(qEntity)
    }

    @Test
    fun get_map_value() {
        val parentClass = Person::class.java

        val fieldName = MAP_VALUE
        val variableName = MAP_VALUE
        val elementType = ElementType.MAP_VALUE
        val type = VALUE_TYPE

        whenever(propertyInfos.elementType).thenReturn(ElementType.MAP)
        whenever(propertyInfos.parameterizedTypes).thenReturn(listOf(KEY_TYPE, VALUE_TYPE))
        whenever(propertyInfos.parentClass).thenReturn(parentClass)
        whenever(propertyInfos.fieldName).thenReturn(fieldName)

        whenever(qEntity.root).thenReturn(qEntity)

        val otherPropertyInfos = mock<PropertyInfos>()
        whenever(
            propertyInfos.copy(
                parentClass = parentClass,
                fieldName = fieldName,
                elementType = elementType,
                type = type,
                parameterizedTypes = emptyList(),
                annotations = emptyList(),
                queryType = QEntityImpl::class.java
            )
        ).thenReturn(otherPropertyInfos)

        val qPath = qEntityJoin.get(fieldName)

        assertThat(qPath).isInstanceOf(QPathImpl::class.java)
        assertThat(qPath.propertyInfos).isEqualTo(otherPropertyInfos)
        assertThat(qPath.path).isInstanceOf(MapPathWrapperImpl::class.java)
        assertThat((qPath.path as PathWrapper).elementType).isEqualTo(ElementType.MAP_VALUE)
        assertThat(qPath.path.type).isEqualTo(Map::class.java)
        assertThat(qPath.path.root).isEqualTo(qEntity)
        // Don't call 'path.annotatedElement' because 'value' (MAP_VALUE) field does not exist in the bean class and PathImpl.getAnnotatedElement fails for PathType.PROPERTY
        // assertThat(qPath.path.annotatedElement).isEqualTo(Map::class.java)
        assertThat(qPath.path.metadata.pathType).isEqualTo(PathType.PROPERTY)
        assertThat(qPath.path.metadata.element).isEqualTo(variableName)
        assertThat(qPath.path.metadata.name).isEqualTo(variableName)
        assertThat(qPath.path.metadata.isRoot).isEqualTo(false)
        assertThat(qPath.path.metadata.parent).isEqualTo(qEntity)
        assertThat(qPath.path.metadata.rootPath).isEqualTo(qEntity)

        verifyNoMoreInteractions(qEntity)
    }

    @Test
    fun get_map_entry_with_unknown_field_name() {
        val fieldName = "myfield"
        val qPath = mock<QPath<*>>()

        whenever(propertyInfos.elementType).thenReturn(ElementType.MAP)

        whenever(qEntity.get(fieldName)).thenReturn(qPath)

        assertThat(qEntityJoin.get(fieldName)).isSameAs(qPath)

        verifyNoMoreInteractions(qEntity)
    }

    @Test
    fun get_element_map_value() {
        val fieldName = "myfield"
        val elementType = ElementType.MAP_VALUE

        whenever(propertyInfos.elementType).thenReturn(ElementType.MAP_VALUE)

        val path = mock<Path<out Any?>>()
        val fieldPropertyInfos = mock<PropertyInfos> {
            on { this.type }.thenReturn(String::class.java)
            on { this.fieldName }.thenReturn(fieldName)
            on { this.parentClass }.thenReturn(qEntity.javaClass)
        }
        val qEntityPath = mock<QPath<*>> {
            on { this.path }.thenReturn(path)
            on { this.propertyInfos }.thenReturn(fieldPropertyInfos)
        }
        whenever(qEntity.get(fieldName)).thenReturn(qEntityPath)

        val otherPropertyInfos = mock<PropertyInfos>()
        whenever(
            fieldPropertyInfos.copy(
                parentClass = qEntity.javaClass,
                fieldName = fieldName,
                elementType = elementType,
                type = String::class.java,
                parameterizedTypes = emptyList(),
                annotations = emptyList(),
                queryType = QEntityImpl::class.java
            )
        ).thenReturn(otherPropertyInfos)

        val qPath = qEntityJoin.get(fieldName)

        assertThat(qPath).isInstanceOf(QPathImpl::class.java)
        assertThat(qPath.path).isInstanceOf(PathWrapperImpl::class.java)
        assertThat((qPath.path as PathWrapperImpl).path).isEqualTo(path)
        assertThat((qPath.path as PathWrapperImpl).elementType).isEqualTo(elementType)
        assertThat(qPath.propertyInfos).isEqualTo(otherPropertyInfos)

        verifyNoMoreInteractions(qEntity)
    }

    @Test
    fun accept() {
        val visitor = mock<Visitor<Any, Any>>()
        val context = mock<Any>()

        val result = mock<QPath<*>>()
        whenever(qEntity.accept(visitor, context)).thenReturn(result)

        assertThat(qEntityJoin.accept(visitor, context)).isSameAs(result)
    }

    @Test
    fun getType() {
        val result = Person::class.java
        whenever(qEntity.type).thenReturn(result)

        assertThat(qEntityJoin.type).isSameAs(result)
    }

    @Test
    fun getMetadata() {
        val result = mock<PathMetadata>()
        whenever(qEntity.metadata).thenReturn(result)

        assertThat(qEntityJoin.metadata).isSameAs(result)
    }

    @Test
    fun getMetadataWithParameter() {
        val property = mock<Path<*>>()

        val result = mock<PathMetadata>()
        whenever(qEntity.getMetadata(property)).thenReturn(result)

        assertThat(qEntityJoin.getMetadata(property)).isSameAs(result)
    }

    @Test
    fun getRoot() {
        val result = mock<Path<*>>()
        whenever(qEntity.root).thenReturn(result)

        assertThat(qEntityJoin.root).isSameAs(result)
    }

    @Test
    fun getAnnotatedElement() {
        val result = mock<AnnotatedElement>()
        whenever(qEntity.annotatedElement).thenReturn(result)

        assertThat(qEntityJoin.annotatedElement).isSameAs(result)
    }

    companion object {
        val KEY_TYPE = String::class.java
        val VALUE_TYPE = Int::class.javaObjectType

        @JvmStatic
        @Suppress("unused")
        fun get_map_entry(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(MAP_KEY, "myalias", "myalias", ElementType.MAP_KEY, KEY_TYPE),
                Arguments.of(MAP_VALUE, "myalias", "value", ElementType.MAP_VALUE, VALUE_TYPE)
            )
        }
    }

    class Person
}