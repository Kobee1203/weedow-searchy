package com.weedow.searchy.query.querytype

import com.neovisionaries.i18n.CountryCode
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.*
import com.weedow.searchy.context.SearchyContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.junit.jupiter.MockitoExtension
import java.time.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
internal class ElementTypeTest {

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun element_type_with_path_class(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(ElementType.BOOLEAN, BooleanPath::class.java),
                Arguments.of(ElementType.STRING, StringPath::class.java),
                Arguments.of(ElementType.NUMBER, NumberPath::class.java),
                Arguments.of(ElementType.DATE, DatePath::class.java),
                Arguments.of(ElementType.DATETIME, DateTimePath::class.java),
                Arguments.of(ElementType.TIME, TimePath::class.java),
                Arguments.of(ElementType.ENUM, EnumPath::class.java),
                Arguments.of(ElementType.ARRAY, ArrayPath::class.java),
                Arguments.of(ElementType.LIST, ListPath::class.java),
                Arguments.of(ElementType.SET, SetPath::class.java),
                Arguments.of(ElementType.COLLECTION, CollectionPath::class.java),
                Arguments.of(ElementType.MAP, MapPath::class.java),
                Arguments.of(ElementType.ENTITY, QEntityImpl::class.java),
                Arguments.of(ElementType.COMPARABLE, ComparablePath::class.java),
                Arguments.of(ElementType.SIMPLE, SimplePath::class.java),
                Arguments.of(ElementType.MAP_KEY, QEntityImpl::class.java),
                Arguments.of(ElementType.MAP_VALUE, QEntityImpl::class.java)
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun class_with_element_type(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Boolean::class.java, ElementType.BOOLEAN, null),
                Arguments.of(Boolean::class.javaObjectType, ElementType.BOOLEAN, null),

                Arguments.of(String::class.java, ElementType.STRING, null),

                Arguments.of(Number::class.java, ElementType.NUMBER, null),
                Arguments.of(Int::class.java, ElementType.NUMBER, null),
                Arguments.of(Int::class.javaObjectType, ElementType.NUMBER, null),
                Arguments.of(Float::class.java, ElementType.NUMBER, null),
                Arguments.of(Float::class.javaObjectType, ElementType.NUMBER, null),
                Arguments.of(Long::class.java, ElementType.NUMBER, null),
                Arguments.of(Long::class.javaObjectType, ElementType.NUMBER, null),
                Arguments.of(Double::class.java, ElementType.NUMBER, null),
                Arguments.of(Double::class.javaObjectType, ElementType.NUMBER, null),
                Arguments.of(Byte::class.java, ElementType.NUMBER, null),
                Arguments.of(Byte::class.javaObjectType, ElementType.NUMBER, null),
                Arguments.of(Short::class.java, ElementType.NUMBER, null),
                Arguments.of(Short::class.javaObjectType, ElementType.NUMBER, null),

                Arguments.of(LocalDate::class.java, ElementType.DATE, null),
                Arguments.of(java.sql.Date::class.java, ElementType.DATE, null),

                Arguments.of(Calendar::class.java, ElementType.DATETIME, null),
                Arguments.of(Date::class.java, ElementType.DATETIME, null),
                Arguments.of(Instant::class.java, ElementType.DATETIME, null),
                Arguments.of(LocalDateTime::class.java, ElementType.DATETIME, null),
                Arguments.of(OffsetDateTime::class.java, ElementType.DATETIME, null),
                Arguments.of(ZonedDateTime::class.java, ElementType.DATETIME, null),
                Arguments.of(LocalDateTime::class.java, ElementType.DATETIME, null),
                Arguments.of(java.sql.Timestamp::class.java, ElementType.DATETIME, null),

                Arguments.of(LocalTime::class.java, ElementType.TIME, null),
                Arguments.of(OffsetTime::class.java, ElementType.TIME, null),
                Arguments.of(java.sql.Time::class.java, ElementType.TIME, null),

                Arguments.of(Enum::class.java, ElementType.ENUM, null),
                Arguments.of(CountryCode::class.java, ElementType.ENUM, null),

                Arguments.of(arrayOf<String>().javaClass, ElementType.ARRAY, null),

                Arguments.of(List::class.java, ElementType.LIST, null),
                Arguments.of(ArrayList::class.java, ElementType.LIST, null),

                Arguments.of(Set::class.java, ElementType.SET, null),
                Arguments.of(HashSet::class.java, ElementType.SET, null),

                Arguments.of(Collection::class.java, ElementType.COLLECTION, null),
                Arguments.of(MyCollection::class.java, ElementType.COLLECTION, null),

                Arguments.of(Map::class.java, ElementType.MAP, null),
                Arguments.of(HashMap::class.java, ElementType.MAP, null),

                Arguments.of(mock<Any>().javaClass, ElementType.ENTITY, true),
                Arguments.of(Comparable::class.java, ElementType.ENTITY, true),

                Arguments.of(Comparable::class.java, ElementType.COMPARABLE, false),
                Arguments.of(Duration::class.java, ElementType.COMPARABLE, false),
                Arguments.of(Char::class.java, ElementType.COMPARABLE, false),
                Arguments.of(UUID::class.java, ElementType.COMPARABLE, false),

                Arguments.of(Object::class.java, ElementType.SIMPLE, false),
                Arguments.of(Any::class.java, ElementType.SIMPLE, false),
                Arguments.of(AtomicBoolean::class.java, ElementType.SIMPLE, false),

                // The following ElementTypes are never called from ElementType methods
                // Arguments.of(mock<Any>().javaClass, ElementType.MAP_KEY, false),
                // Arguments.of(mock<Any>().javaClass, ElementType.MAP_VALUE, false)
            )
        }
    }

    @Test
    fun get_element_type_count() {
        assertThat(ElementType.values()).hasSize(17)
    }

    @ParameterizedTest
    @MethodSource("element_type_with_path_class")
    fun get_path_class_from_element_type(elementType: ElementType, pathClass: Class<out Path<*>>) {
        assertThat(elementType.pathClass).isEqualTo(pathClass)
    }

    @ParameterizedTest
    @MethodSource("class_with_element_type")
    fun get_element_type_from_class(clazz: Class<*>, elementType: ElementType, isEntity: Boolean?) {
        val searchyContext = mock<SearchyContext>()

        if (isEntity != null) {
            whenever(searchyContext.isEntity(clazz)).thenReturn(isEntity)
        }

        assertThat(ElementType.get(clazz, searchyContext)).isEqualTo(elementType)
    }

    @Test
    fun special_element_types() {
        assertThat(ElementType.MAP_KEY.pathClass).isEqualTo(QEntityImpl::class.java)
        assertThat(ElementType.MAP_VALUE.pathClass).isEqualTo(QEntityImpl::class.java)
    }

    internal class MyCollection<out T>(private val array: Array<T>) : kotlin.collections.AbstractCollection<T>() {
        override fun iterator(): Iterator<T> = array.iterator()

        override val size: Int
            get() = array.size
    }

}