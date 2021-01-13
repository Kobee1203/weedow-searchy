package com.weedow.searchy.join

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class EntityJoinUtilsTest {

    @Test
    fun get_field_path_with_parent_path() {
        val parentPath = "parentPath"
        val fieldName = "fieldName"
        val fieldPath = EntityJoinUtils.getFieldPath(parentPath, fieldName)

        assertThat(fieldPath).isEqualTo("$parentPath.$fieldName")
    }

    @Test
    fun get_field_path_without_parent_path() {
        val fieldName1 = "fieldName"
        val fieldPath1 = EntityJoinUtils.getFieldPath("", fieldName1)

        assertThat(fieldPath1).isEqualTo(fieldName1)

        val fieldName2 = "fieldName"
        val fieldPath2 = EntityJoinUtils.getFieldPath(" ", fieldName2)

        assertThat(fieldPath2).isEqualTo(fieldName2)
    }

}