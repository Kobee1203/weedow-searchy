package com.weedow.spring.data.search.utils

import com.weedow.spring.data.search.common.model.JpaPersistable
import com.weedow.spring.data.search.common.model.Person
import org.apache.commons.lang3.reflect.FieldUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Id

internal class EntityUtilsTest {

    @Test
    fun getFieldsWithAnnotation() {
        val idFields = FieldUtils.getFieldsWithAnnotation(Person::class.java, Id::class.java)
        assertThat(idFields).isNotNull()
        assertThat(idFields).hasSize(1)
        assertThat(idFields[0]).isEqualTo(Person::class.java.superclass.getDeclaredField("id"))

        val columnFields = FieldUtils.getFieldsWithAnnotation(Person::class.java, Column::class.java)
        assertThat(columnFields).isNotNull()
        assertThat(columnFields).hasSize(11)
        assertThat(columnFields).containsExactlyInAnyOrder(
                Person::class.java.getDeclaredField("firstName"),
                Person::class.java.getDeclaredField("lastName"),
                Person::class.java.getDeclaredField("email"),
                Person::class.java.getDeclaredField("birthday"),
                Person::class.java.getDeclaredField("height"),
                Person::class.java.getDeclaredField("weight"),
                Person::class.java.getDeclaredField("phoneNumbers"),
                Person::class.java.getDeclaredField("characteristics"),
                Person::class.java.getDeclaredField("tasks"),
                JpaPersistable::class.java.getDeclaredField("createdOn"),
                JpaPersistable::class.java.getDeclaredField("updatedOn")
        )

        val embeddedIdFields = FieldUtils.getFieldsWithAnnotation(Person::class.java, EmbeddedId::class.java)
        assertThat(embeddedIdFields).isNotNull()
        assertThat(embeddedIdFields).isEmpty()
    }

}