package com.weedow.spring.data.search.query.querytype

import com.neovisionaries.i18n.CountryCode
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.querydsl.core.types.Path
import com.querydsl.core.types.PathType
import com.querydsl.core.types.dsl.*
import com.querydsl.core.util.Annotations
import com.weedow.spring.data.search.common.model.Job
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.context.DataSearchContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.extractor.Extractors
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@ExtendWith(MockitoExtension::class)
internal class QEntityImplTest {

    @Test
    fun qentity_with_boolean_field() {
        val fieldName = "myfield"
        val elementType = ElementType.BOOLEAN
        val pathClass = BooleanPath::class.java
        val fieldType = Boolean::class.javaObjectType

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, emptyList())
    }

    @Test
    fun qentity_with_string_field() {
        val fieldName = "myfield"
        val elementType = ElementType.STRING
        val pathClass = StringPath::class.java
        val fieldType = String::class.javaObjectType

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, emptyList())
    }

    @Test
    fun qentity_with_number_field() {
        val fieldName = "myfield"
        val elementType = ElementType.NUMBER
        val fieldType = Long::class.java
        val pathClass = NumberPath::class.java

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.type }.thenReturn(fieldType)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, emptyList())
    }

    @Test
    fun qentity_with_date_field() {
        val fieldName = "myfield"
        val elementType = ElementType.DATE
        val fieldType = LocalDate::class.java
        val pathClass = DatePath::class.java

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.type }.thenReturn(fieldType)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, emptyList())
    }

    @Test
    fun qentity_with_datetime_field() {
        val fieldName = "myfield"
        val elementType = ElementType.DATETIME
        val fieldType = LocalDateTime::class.java
        val pathClass = DateTimePath::class.java

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.type }.thenReturn(fieldType)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, emptyList())
    }

    @Test
    fun qentity_with_time_field() {
        val fieldName = "myfield"
        val elementType = ElementType.TIME
        val fieldType = LocalTime::class.java
        val pathClass = TimePath::class.java

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.type }.thenReturn(fieldType)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, emptyList())
    }

    @Test
    fun qentity_with_enum_field() {
        val fieldName = "myfield"
        val elementType = ElementType.ENUM
        val fieldType = CountryCode::class.java
        val pathClass = EnumPath::class.java

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.type }.thenReturn(fieldType)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, emptyList())
    }

    @Test
    fun qentity_with_array_field() {
        val fieldName = "myfield"
        val elementType = ElementType.ARRAY
        val fieldType = arrayOf<String>().javaClass
        val pathClass = ArrayPath::class.java
        val parameterTypes = listOf(String::class.java)

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.type }.thenReturn(fieldType)
            on { this.parameterizedTypes }.thenReturn(parameterTypes)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, parameterTypes)
    }

    @Test
    fun qentity_with_list_field() {
        val fieldName = "myfield"
        val elementType = ElementType.LIST
        val fieldType = List::class.java
        val pathClass = ListPath::class.java
        val parameterTypes = listOf(String::class.java)

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.parameterizedTypes }.thenReturn(parameterTypes)
            on { this.queryType }.thenReturn(StringPath::class.java)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, parameterTypes)
    }

    @Test
    fun qentity_with_set_field() {
        val fieldName = "myfield"
        val elementType = ElementType.SET
        val fieldType = Set::class.java
        val pathClass = SetPath::class.java
        val parameterTypes = listOf(String::class.java)

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.parameterizedTypes }.thenReturn(parameterTypes)
            on { this.queryType }.thenReturn(StringPath::class.java)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, parameterTypes)
    }

    @Test
    fun qentity_with_collection_field() {
        val fieldName = "myfield"
        val elementType = ElementType.COLLECTION
        val fieldType = Collection::class.java
        val pathClass = CollectionPath::class.java
        val parameterTypes = listOf(String::class.java)

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.parameterizedTypes }.thenReturn(parameterTypes)
            on { this.queryType }.thenReturn(StringPath::class.java)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, parameterTypes)
    }

    @Test
    fun qentity_with_map_field() {
        val fieldName = "myfield"
        val elementType = ElementType.MAP
        val fieldType = Map::class.java
        val pathClass = MapPath::class.java
        val parameterTypes = listOf(Int::class.java, Boolean::class.java)

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.parameterizedTypes }.thenReturn(parameterTypes)
            on { this.queryType }.thenReturn(BooleanPath::class.java)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, parameterTypes)
    }

    @Test
    fun qentity_with_comparable_field() {
        val fieldName = "myfield"
        val elementType = ElementType.COMPARABLE
        val pathClass = ComparablePath::class.java
        val fieldType = Comparable::class.javaObjectType

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.type }.thenReturn(fieldType)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, emptyList())
    }

    @Test
    fun qentity_with_simple_field() {
        val fieldName = "myfield"
        val elementType = ElementType.SIMPLE
        val pathClass = SimplePath::class.java
        val fieldType = Any::class.javaObjectType

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.type }.thenReturn(fieldType)
        }

        processAndVerify(propertyInfos, fieldName, pathClass, fieldType, elementType, emptyList())
    }

    /** This test check ElementType.ENTITY and checks if there is no infinite loop thanks to PathInits */
    @Test
    fun qentity_with_entity_field() {
        val variable = "any"
        val entityClass = Person::class.java

        val fieldName = "myfield"
        val elementType = ElementType.ENTITY
        val pathClass = QEntityImpl::class.java
        val fieldType = Job::class.javaObjectType

        val propertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(fieldName)
            on { this.elementType }.thenReturn(elementType)
            on { this.type }.thenReturn(fieldType)
        }

        // PropertyInfos returned by the inner entity field when the related QEntity is instantiated
        // It represents the parent class as ENTITY. It checks if there is no infinite loop thanks to PathInits
        val otherFieldName = "parent"
        val otherElementType = ElementType.ENTITY
        val otherPathClass = QEntityImpl::class.java
        val otherFieldType = entityClass
        val otherPropertyInfos = mock<PropertyInfos> {
            on { this.fieldName }.thenReturn(otherFieldName)
            on { this.elementType }.thenReturn(otherElementType)
            on { this.type }.thenReturn(otherFieldType)
        }

        val dataSearchContext = mock<DataSearchContext> {
            on { this.getAllPropertyInfos(entityClass) }.thenReturn(
                listOf(propertyInfos)
            )

            // Called when the QEntity of the field 'myfield' is instantiated
            on { this.getAllPropertyInfos(fieldType) }.thenReturn(
                listOf(otherPropertyInfos)
            )
        }

        val qEntity = QEntityImpl(dataSearchContext, entityClass, variable)

        val qPath = qEntity.get(fieldName)

        verifyQPath(qPath, propertyInfos, pathClass, qEntity, fieldType, fieldName, elementType, emptyList())

        val qEntityPath = (qPath.path as QEntity)
        val parentQPath = qEntityPath.get(otherFieldName)

        verifyQPath(parentQPath, otherPropertyInfos, otherPathClass, qEntityPath, otherFieldType, otherFieldName, otherElementType, emptyList())

        verifyNoMoreInteractions(propertyInfos)
        verifyNoMoreInteractions(otherPropertyInfos)
        verifyNoMoreInteractions(dataSearchContext)
    }

    @Test
    fun throw_exception_when_field_not_found_in_qentity() {
        val variable = "any"
        val entityClass = Any::class.java
        val dataSearchContext = mock<DataSearchContext>()

        val qEntity = QEntityImpl(dataSearchContext, entityClass, variable)

        assertThatThrownBy { qEntity.get("myfield") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Could not found the Path related to the given field name 'myfield'")

        verify(dataSearchContext).getAllPropertyInfos(entityClass)
    }

    private fun processAndVerify(
        propertyInfos: PropertyInfos,
        fieldName: String,
        pathClass: Class<out Path<*>>,
        fieldType: Class<*>,
        elementType: ElementType,
        parameterTypes: List<Class<*>>
    ) {
        val variable = "any"
        val entityClass = Any::class.java
        val dataSearchContext = mock<DataSearchContext> {
            on { this.getAllPropertyInfos(entityClass) }.thenReturn(
                listOf(propertyInfos)
            )
        }

        val qEntity = QEntityImpl(dataSearchContext, entityClass, variable)

        val qPath = qEntity.get(fieldName)

        verifyQPath(qPath, propertyInfos, pathClass, qEntity, fieldType, fieldName, elementType, parameterTypes)

        verifyNoMoreInteractions(propertyInfos)
        verifyNoMoreInteractions(dataSearchContext)
    }

    private fun verifyQPath(
        qPath: QPath<*>,
        propertyInfos: PropertyInfos,
        pathClass: Class<out Path<*>>,
        qEntity: QEntity<*>,
        fieldType: Class<*>,
        fieldName: String,
        elementType: ElementType,
        parameterTypes: List<Class<*>>
    ) {
        assertThat(qPath.propertyInfos).isSameAs(propertyInfos)
        assertThat(qPath.path).isInstanceOf(pathClass)
        assertThat(qPath.path.type).isEqualTo(fieldType)
        assertThat(qPath.path.root).isSameAs(qEntity.root)
        assertThat(qPath.path.annotatedElement).isInstanceOf(Annotations::class.java)
        assertThat(qPath.path.metadata.pathType).isEqualTo(PathType.PROPERTY)
        assertThat(qPath.path.metadata.element).isEqualTo(fieldName)
        assertThat(qPath.path.metadata.name).isEqualTo(fieldName)
        assertThat(qPath.path.metadata.isRoot).isEqualTo(false)
        assertThat(qPath.path.metadata.parent).isEqualTo(qEntity)
        assertThat(qPath.path.metadata.rootPath).isEqualTo(qEntity.root)
        if (elementType === ElementType.ARRAY || elementType === ElementType.LIST || elementType === ElementType.SET || elementType === ElementType.COLLECTION) {
            assertThat(qPath.path).extracting(Extractors.resultOf("getElementType")).isEqualTo(parameterTypes[0])
        }
        if (elementType === ElementType.MAP) {
            assertThat(qPath.path).extracting(Extractors.resultOf("getKeyType")).isEqualTo(parameterTypes[0])
            assertThat(qPath.path).extracting(Extractors.resultOf("getValueType")).isEqualTo(parameterTypes[1])
        }
    }
}