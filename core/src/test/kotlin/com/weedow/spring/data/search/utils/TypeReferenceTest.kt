package com.weedow.spring.data.search.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Class is based on ideas from
 * [TypeReferenceTest.java](https://github.com/FasterXML/jackson-core/blob/master/src/test/java/com/fasterxml/jackson/core/type/TypeReferenceTest.java)
 */
// Not much to test, but exercise to prevent code coverage tool from showing all red for package
internal class TypeReferenceTest {

    @Test
    fun testSimple() {
        val ref = object : TypeReference<List<String>>() {}
        assertThat(ref).isNotNull()
        assertThat(ref.type.typeName).isEqualTo("java.util.List<? extends java.lang.String>")
    }

    @Test
    fun testComparable() {
        val ref1 = object : TypeReference<List<String>>() {}
        val ref2 = object : TypeReference<List<String>>() {}

        assertThat(ref1.compareTo(ref2)).isEqualTo(0)
    }

}