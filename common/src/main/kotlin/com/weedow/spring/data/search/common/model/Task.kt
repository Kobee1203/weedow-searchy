package com.weedow.spring.data.search.common.model

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Task(
    @Column(nullable = false)
    val name: String,

    @Column
    val description: String

) : JpaPersistable<Long>() {

    // Override toString() method to get a JSON representation used by the HTTP response
    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE)
    }
}