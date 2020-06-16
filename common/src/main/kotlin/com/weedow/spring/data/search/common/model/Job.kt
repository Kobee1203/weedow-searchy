package com.weedow.spring.data.search.common.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToOne

@Entity
class Job(
        @Column(nullable = false)
        val title: String,

        @Column(nullable = false)
        val company: String,

        @Column(nullable = false)
        val salary: Int,

        @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
        val hireDate: OffsetDateTime,

        @OneToOne(optional = false)
        @JsonIgnoreProperties("jobEntity")
        val person: Person

) : JpaPersistable<Long>() {

}
