package com.weedow.spring.data.search.utils

import com.weedow.spring.data.search.example.model.Address
import com.weedow.spring.data.search.example.model.JpaPersistable
import com.weedow.spring.data.search.example.model.Person
import com.weedow.spring.data.search.example.model.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.persistence.*

internal class EntityUtilsTest {

    @Test
    fun getFieldsWithAnnotation() {
        val idFields = EntityUtils.getFieldsWithAnnotation(Person::class.java, Id::class.java)
        assertThat(idFields).isNotNull()
        assertThat(idFields).hasSize(1)
        assertThat(idFields[0]).isEqualTo(Person::class.java.superclass.getDeclaredField("id"))

        val columnFields = EntityUtils.getFieldsWithAnnotation(Person::class.java, Column::class.java)
        assertThat(columnFields).isNotNull()
        assertThat(columnFields).hasSize(9)
        assertThat(columnFields).containsExactlyInAnyOrder(
                Person::class.java.getDeclaredField("firstName"),
                Person::class.java.getDeclaredField("lastName"),
                Person::class.java.getDeclaredField("email"),
                Person::class.java.getDeclaredField("birthday"),
                Person::class.java.getDeclaredField("height"),
                Person::class.java.getDeclaredField("weight"),
                Person::class.java.getDeclaredField("phoneNumbers"),
                JpaPersistable::class.java.getDeclaredField("createdOn"),
                JpaPersistable::class.java.getDeclaredField("updatedOn")
        )

        val embeddedIdFields = EntityUtils.getFieldsWithAnnotation(Person::class.java, EmbeddedId::class.java)
        assertThat(embeddedIdFields).isNotNull()
        assertThat(embeddedIdFields).isEmpty()
    }

    @Test
    fun getFieldClass() {
        assertThat(EntityUtils.getFieldClass(Person::class.java.getDeclaredField("firstName")))
                .isEqualTo(String::class.java)

        assertThat(EntityUtils.getFieldClass(Person::class.java.getDeclaredField("phoneNumbers")))
                .isEqualTo(String::class.java)

        assertThat(EntityUtils.getFieldClass(Person::class.java.getDeclaredField("addressEntities")))
                .isEqualTo(Address::class.java)
    }

    @Test
    fun getJoinAnnotationClass() {
        assertThat(EntityUtils.getJoinAnnotationClass(Person::class.java.getDeclaredField("jobEntity")))
                .isEqualTo(OneToOne::class.java)

        assertThat(EntityUtils.getJoinAnnotationClass(Person::class.java.getDeclaredField("vehicles")))
                .isEqualTo(OneToMany::class.java)

        assertThat(EntityUtils.getJoinAnnotationClass(Person::class.java.getDeclaredField("addressEntities")))
                .isEqualTo(ManyToMany::class.java)

        assertThat(EntityUtils.getJoinAnnotationClass(Person::class.java.getDeclaredField("phoneNumbers")))
                .isEqualTo(ElementCollection::class.java)

        assertThat(EntityUtils.getJoinAnnotationClass(Vehicle::class.java.getDeclaredField("person")))
                .isEqualTo(ManyToOne::class.java)

        assertThat(EntityUtils.getJoinAnnotationClass(Person::class.java.getDeclaredField("firstName")))
                .isNull()
    }

    @Test
    fun isElementCollection() {
    }
}