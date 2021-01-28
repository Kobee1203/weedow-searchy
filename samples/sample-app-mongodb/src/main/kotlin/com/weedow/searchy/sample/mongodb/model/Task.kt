package com.weedow.searchy.sample.mongodb.model

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Task(
    val name: String,

    val description: String?
) {

    @Id
    private var id: Long? = null

    fun getId(): Long? {
        return id
    }

    fun setId(id: Long) {
        this.id = id
    }

    // Override toString() method to get a JSON representation used by the HTTP response
    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE)
    }
}