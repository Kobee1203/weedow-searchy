package com.weedow.searchy.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Id

internal class EntityUtilsTest {

    @Test
    fun getFieldWithAnnotation() {
        val idField = EntityUtils.getFieldWithAnnotation(MyObject::class.java, Id::class.java)
        assertThat(idField).isNotNull
        assertThat(idField).isEqualTo(MyObject::class.java.superclass.getDeclaredField("id"))

        val columnField = EntityUtils.getFieldWithAnnotation(MyObject::class.java, Column::class.java)
        assertThat(columnField).isNotNull
        assertThat(columnField).isEqualTo(MyObject::class.java.getDeclaredField("field1"))

        val embeddedField = EntityUtils.getFieldWithAnnotation(MyObject::class.java, EmbeddedId::class.java)
        assertThat(embeddedField).isNull()
    }

    @Test
    fun getParameterizedTypes() {
        val field1 = MyObject::class.java.getDeclaredField("field1")
        val parameterizedTypes1 = EntityUtils.getParameterizedTypes(field1)
        assertThat(parameterizedTypes1).containsExactly(String::class.java)

        val field2 = MyObject::class.java.getDeclaredField("field2")
        val parameterizedTypes2 = EntityUtils.getParameterizedTypes(field2)
        assertThat(parameterizedTypes2).containsExactly(Int::class.javaObjectType, Boolean::class.javaObjectType)

        val field3 = MyObject::class.java.getDeclaredField("field3")
        val parameterizedTypes3 = EntityUtils.getParameterizedTypes(field3)
        assertThat(parameterizedTypes3).containsExactly(Double::class.javaObjectType)

        val field4 = MyObject::class.java.getDeclaredField("field4")
        val parameterizedTypes4 = EntityUtils.getParameterizedTypes(field4)
        assertThat(parameterizedTypes4).isEmpty()

        val field5 = MyObject::class.java.getDeclaredField("field5")
        val parameterizedTypes5 = EntityUtils.getParameterizedTypes(field5)
        assertThat(parameterizedTypes5).containsExactly(Number::class.java)
    }

    open class Parent(
        @Id
        val id: Long
    )

    class MyObject(
        @Column
        val field1: Set<String>,

        @Column
        val field2: Map<Int, Boolean>,

        @Column
        val field3: Array<Double>,

        @Column
        val field4: String,

        /**
         * Field to test [java.lang.reflect.WildcardType] parameterized types.
         * Declaring a field with generic types as 'var' causes the parameterized types to be WildcardType.
         */
        @Column
        var field5: List<Number>
    ) : Parent(0)

}